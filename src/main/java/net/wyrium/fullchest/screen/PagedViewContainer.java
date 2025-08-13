package net.wyrium.fullchest.screen;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * A 54-slot window over a larger {@link Container}, used to implement paginated chests.
 * <p>
 * Modes :
 * <ul>
 *   <li><b>mirrorMode = true</b>: client side — the backing container already exposes 54 slots
 *       (the "page window" is mirrored locally; indices map 1:1).</li>
 *   <li><b>mirrorMode = false</b>: server side — the backing container holds the full size;
 *       this view maps local indices 0..53 to a page-sized slice of the backing container.</li>
 * </ul>
 * Responsibilities:
 * <ul>
 *   <li>Expose a fixed-size (54) {@link Container} interface to menus/slots.</li>
 *   <li>Translate local indices to backing indices depending on the current page.</li>
 *   <li>Prevent interactions beyond {@code total} visible items via {@link #isInRange(int)} and {@link #canPlaceItem(int, ItemStack)}.</li>
 * </ul>
 */
public class PagedViewContainer implements Container {
    private final Container backing;
    private static final int VISIBLE = 54;

    /** true on client (backing == 54), false on server (backing == TOTAL). */
    private final boolean mirrorMode;

    /** Zero-based page index and authoritative total slots (set by server). */
    private int page = 0;
    private int total = 0;

    /**
     * @param backing the underlying container (full size on server, 54 on client)
     */
    public PagedViewContainer(Container backing) {
        this.backing = backing;
        this.mirrorMode = backing.getContainerSize() <= VISIBLE; // <= 54 ⇒ likely client-side mirror
    }

    /** Sets current page (clamped to ≥ 0). */
    public void setPage(int page)  { this.page  = Math.max(0, page); }

    /** Sets total number of logical slots across all pages (clamped to ≥ 0). */
    public void setTotal(int total){ this.total = Math.max(0, total); }

    /** Current zero-based page. */
    public int  getPage()          { return page; }

    /** Base global index of the current page. */
    private int base()         { return page * VISIBLE; }

    /** Maps local 0..53 to a backing index, depending on mode. */
    private int map(int local) { return mirrorMode ? local : base() + local; }

    /**
     * Whether the given local index (0..53) falls within the logical total for this page.
     * Used to disable slots on the last (partial) page.
     */
    public boolean isInRange(int localIndex) {
        int global = base() + localIndex;
        return global < total;
    }

    /** Always expose 54 visible slots to the menu. */
    @Override public int getContainerSize() { return VISIBLE; }

    /** Backing bounds guard. */
    private boolean inBackingBounds(int idx) {
        return idx >= 0 && idx < backing.getContainerSize();
    }

    /**
     * Checks emptiness within the visible window.
     * In mirror mode, checks 0..53; otherwise checks the current page slice.
     */
    @Override
    public boolean isEmpty() {
        int start = mirrorMode ? 0 : base();
        int end   = Math.min(start + VISIBLE, backing.getContainerSize());
        for (int i = start; i < end; i++) {
            if (!backing.getItem(i).isEmpty()) return false;
        }
        return true;
    }

    /** Reads an item from the mapped backing slot; returns EMPTY if out of bounds. */
    @Nonnull
    @Override
    public ItemStack getItem(int localIndex) {
        int i = map(localIndex);
        if (!inBackingBounds(i)) return ItemStack.EMPTY;
        return backing.getItem(i);
    }

    /** Writes an item to the mapped backing slot and marks changed; ignored if OOB. */
    @Override
    public void setItem(int localIndex, @Nonnull ItemStack stack) {
        int i = map(localIndex);
        if (!inBackingBounds(i)) return;
        backing.setItem(i, stack);
        setChanged();
    }

    /** Removes up to {@code amount} from the mapped backing slot; EMPTY if OOB. */
    @Nonnull
    @Override
    public ItemStack removeItem(int localIndex, int amount) {
        int i = map(localIndex);
        if (!inBackingBounds(i)) return ItemStack.EMPTY;
        ItemStack out = backing.removeItem(i, amount);
        setChanged();
        return out;
    }

    /** Removes the entire stack from the mapped backing slot without updates; EMPTY if OOB. */
    @Nonnull
    @Override
    public ItemStack removeItemNoUpdate(int localIndex) {
        int i = map(localIndex);
        if (!inBackingBounds(i)) return ItemStack.EMPTY;
        ItemStack out = backing.removeItemNoUpdate(i);
        setChanged();
        return out;
    }

    /**
     * Clears only the visible window (54 slots or the page slice on server) then marks changed.
     */
    @Override public void clearContent() {
        int start = mirrorMode ? 0 : base();
        int end   = Math.min(start + VISIBLE, backing.getContainerSize());
        for (int i = start; i < end; i++) backing.setItem(i, ItemStack.EMPTY);
        setChanged();
    }

    /**
     * Allows placement only if the local slot is within the logical total
     * and the backing container accepts the item at the mapped index.
     * Prevents interacting with disabled slots on the last page.
     */
    @Override
    public boolean canPlaceItem(int localIndex, @Nonnull ItemStack stack) {
        return isInRange(localIndex) && backing.canPlaceItem(map(localIndex), stack);
    }

    /** Propagate change notifications to the backing container. */
    @Override public void setChanged() { backing.setChanged(); }

    /** Proximity/validity is delegated to the backing container. */
    @Override public boolean stillValid(@Nonnull Player p) { return backing.stillValid(p); }
}
