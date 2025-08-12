package net.wyrium.fullchest.screen;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class PagedChestMenu extends AbstractContainerMenu {
    public static final int ROWS = 6;
    public static final int COLS = 9;
    public static final int VISIBLE = ROWS * COLS;

    private final Container full;
    private final PagedViewContainer view; // Paged view 54 slots
    private final ContainerData data; // [0]=page, [1]=total

    public PagedChestMenu(int id, Inventory playerInv, Container full) {
        super(ModMenuTypes.PAGED_CHEST.get(), id);
        this.full = full;
        this.view = new PagedViewContainer(full);

        // page + total
        this.data = new SimpleContainerData(2);
        addDataSlots(this.data);

        // total (serveur renseigne la vérité)
        this.view.setTotal(full.getContainerSize());
        if (!playerInv.player.level().isClientSide) {
            this.data.set(1, full.getContainerSize());
            this.view.setTotal(full.getContainerSize());
        }

        // slots coffre (6x9)
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int local = c + r * COLS;
                addSlot(new PagedSlot(this.view, local, 8 + c * 18, 18 + r * 18));
            }
        }

        // slots joueur
        int invY = 18 + ROWS * 18 + 14;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < COLS; c++) {
                addSlot(new Slot(playerInv, c + r * COLS + 9, 8 + c * 18, invY + r * 18));
            }
        }
        for (int c = 0; c < COLS; c++) {
            addSlot(new Slot(playerInv, c, 8 + c * 18, invY + 58));
        }

        this.full.stopOpen(playerInv.player);

        // Page 0
        setPage(0);

        sendAllDataToRemote();
    }

    /* ==== Sync Data -> client ==== */
    @Override
    public void setData(int id, int value) {
        super.setData(id, value);
        if (id == 0) {
            // page
            view.setPage(Mth.clamp(value, 0, getMaxPages() - 1));
        } else if (id == 1) {
            // total
            view.setTotal(Math.max(0, value));
            // si total baisse et invalide la page courante, reclamp
            int clamped = Mth.clamp(getPage(), 0, getMaxPages() - 1);
            if (clamped != getPage()) {
                data.set(0, clamped);
                view.setPage(clamped);
            }
        }
    }

    public ContainerData getData() { return this.data; }
    public int getPage() { return data.get(0); }

    public int getMaxPages() {
        int total = Math.max(data.get(1), VISIBLE); // fallback avant sync
        int pages = (int) Math.ceil(total / (double) VISIBLE);
        return Math.max(1, pages);
    }

    private void resyncVisible() {
        this.slotsChanged(this.view);
        this.broadcastChanges();
    }

    private void setPage(int page) {
        int clamped = Mth.clamp(page, 0, getMaxPages() - 1);
        view.setPage(clamped);
        if (clamped != getPage()) data.set(0, clamped);
        resyncVisible();
    }

    /* ==== API boutons ==== */
    public void nextPage() { setPage(getPage() + 1); }
    public void prevPage() { setPage(getPage() - 1); }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int id) {
        if (id == 0) { prevPage(); return true; }
        if (id == 1) { nextPage(); return true; }
        return false;
    }

    @Override
    public boolean stillValid(@NotNull Player p) { return full.stillValid(p); }

    @NotNull
    @Override
    public ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack ret = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            ret = stack.copy();

            int chestEnd = VISIBLE;
            int totalSlots = this.slots.size();

            if (index < chestEnd) {
                if (!this.moveItemStackTo(stack, chestEnd, totalSlots, true)) return ItemStack.EMPTY;
            } else {
                if (!this.moveItemStackTo(stack, 0, chestEnd, false)) return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return ret;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.full.startOpen(player);
    }
}
