package net.wyrium.fullchest.screen;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;
import net.wyrium.fullchest.screen.slot.PagedSlot;
import org.jetbrains.annotations.NotNull;

/**
 * Server-side menu for a paginated chest UI.
 * <p>
 * Layout:
 * <ul>
 *   <li>Chest view: 6 rows × 9 cols (54 visible slots), backed by a full {@link Container} via {@link PagedViewContainer}.</li>
 *   <li>Player inventory: 3 rows × 9 cols + hotbar.</li>
 * </ul>
 * Synchronization:
 * <ul>
 *   <li>{@link #data}: [0] = current page (0-based), [1] = total slots in the full container.</li>
 *   <li>Server sets {@code data[1]} to the authoritative total; client derives page count from it.</li>
 *   <li>Page changes propagate through {@link #setData(int, int)} and update the view window.</li>
 * </ul>
 */
public class PagedChestMenu extends AbstractContainerMenu {
    public static final int ROWS = 6;
    public static final int COLS = 9;
    /** Number of visible slots per page (fixed 6×9). */
    public static final int VISIBLE = ROWS * COLS;

    /** The full backing container (may exceed 54 slots). */
    private final Container full;
    /** A windowed view into {@link #full} that exposes exactly 54 visible slots. */
    private final PagedViewContainer view;
    /** [0] = page, [1] = total slots (authoritative on server). */
    private final ContainerData data;

    /**
     * @param id window/menu id
     * @param playerInv player inventory
     * @param full full backing container (all slots)
     */
    public PagedChestMenu(int id, Inventory playerInv, Container full) {
        super(ModMenuTypes.PAGED_CHEST.get(), id);
        this.full = full;
        this.view = new PagedViewContainer(full);

        // Two synced integers: current page + total slots
        this.data = new SimpleContainerData(2);
        addDataSlots(this.data);

        // Initialize totals; server is the source of truth
        this.view.setTotal(full.getContainerSize());
        if (!playerInv.player.level().isClientSide) {
            this.data.set(1, full.getContainerSize());
            this.view.setTotal(full.getContainerSize());
        }

        // Chest slots (6×9) mapped to the paged view's local indices 0..53
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int local = c + r * COLS;
                addSlot(new PagedSlot(this.view, local, 8 + c * 18, 18 + r * 18));
            }
        }

        // Player inventory (3 rows)
        int invY = 18 + ROWS * 18 + 14;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < COLS; c++) {
                addSlot(new Slot(playerInv, c + r * COLS + 9, 8 + c * 18, invY + r * 18));
            }
        }
        // Hotbar
        for (int c = 0; c < COLS; c++) {
            addSlot(new Slot(playerInv, c, 8 + c * 18, invY + 58));
        }

        // Prevent vanilla double-open semantics; the BE/Container is already open server-side
        this.full.stopOpen(playerInv.player);

        // Start on page 0
        setPage(0);

        // Push initial data to client
        sendAllDataToRemote();
    }

    /* ==== Sync Data -> client ==== */

    /**
     * Receives server updates for page/total and updates the paged view.
     * id=0: current page; id=1: total slots.
     * Ensures the current page stays within valid bounds if the total shrinks.
     */
    @Override
    public void setData(int id, int value) {
        super.setData(id, value);
        if (id == 0) {
            // page
            view.setPage(Mth.clamp(value, 0, getMaxPages() - 1));
        } else if (id == 1) {
            // total
            view.setTotal(Math.max(0, value));
            // if total decreased and invalidated current page, clamp and propagate
            int clamped = Mth.clamp(getPage(), 0, getMaxPages() - 1);
            if (clamped != getPage()) {
                data.set(0, clamped);
                view.setPage(clamped);
            }
        }
    }

    /** Exposes the data array (useful for client screens). */
    public ContainerData getData() { return this.data; }

    /** Current page (0-based), synced via {@link #data}. */
    public int getPage() { return data.get(0); }

    /**
     * Computes the number of available pages from the total slot count.
     * Uses {@link #VISIBLE} as page size; minimum is 1 page.
     * Falls back to {@code VISIBLE} if total hasn't synced yet (client side).
     */
    public int getMaxPages() {
        int total = Math.max(data.get(1), VISIBLE); // fallback prior to server sync
        int pages = (int) Math.ceil(total / (double) VISIBLE);
        return Math.max(1, pages);
    }

    /** Notifies listeners that the visible window changed. */
    private void resyncVisible() {
        this.slotsChanged(this.view);
        this.broadcastChanges();
    }

    /** Sets the current page (clamped) and triggers a visible window refresh. */
    private void setPage(int page) {
        int clamped = Mth.clamp(page, 0, getMaxPages() - 1);
        view.setPage(clamped);
        if (clamped != getPage()) data.set(0, clamped);
        resyncVisible();
    }

    /* ==== Button API (client → server) ==== */

    /** Go to the next page (server-authoritative). */
    public void nextPage() { setPage(getPage() + 1); }

    /** Go to the previous page (server-authoritative). */
    public void prevPage() { setPage(getPage() - 1); }

    /**
     * Handles screen button clicks relayed by the client.
     * id=0: previous page, id=1: next page.
     */
    @Override
    public boolean clickMenuButton(@NotNull Player player, int id) {
        if (id == 0) { prevPage(); return true; }
        if (id == 1) { nextPage(); return true; }
        return false;
    }

    @Override
    public boolean stillValid(@NotNull Player p) { return full.stillValid(p); }

    /**
     * Shift-click transfer logic between chest view (0...VISIBLE-1) and player inventory.
     */
    @NotNull
    @Override
    public ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack ret = stack.copy();

        int chestEnd = VISIBLE;
        int totalSlots = this.slots.size();

        if (index < chestEnd) {
            // From chest view → player inventory/hotbar
            if (!this.moveItemStackTo(stack, chestEnd, totalSlots, true)) return ItemStack.EMPTY;
        } else {
            // From player → chest view
            if (!this.moveItemStackTo(stack, 0, chestEnd, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return ret;
    }

    /**
     * Called when the menu is closed. Restores the "open" status on the backing container.
     */
    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.full.startOpen(player);
    }
}
