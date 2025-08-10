package net.wyrium.fullchest.screen;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class PagedViewContainer implements Container {
    private final Container backing;
    private static final int VISIBLE = 54;
    private final boolean mirrorMode; // true côté client (backing == 54), false côté serveur (backing == TOTAL)

    private int page = 0;
    private int total = 0;

    public PagedViewContainer(Container backing) {
        this.backing = backing;
        this.mirrorMode = backing.getContainerSize() <= VISIBLE; // <= 54 => client
    }

    public void setPage(int page)  { this.page  = Math.max(0, page); }
    public void setTotal(int total){ this.total = Math.max(0, total); }
    public int  getPage()          { return page; }

    private int base()         { return page * VISIBLE; }
    private int map(int local) { return mirrorMode ? local : base() + local; }

    public boolean isInRange(int localIndex) {
        int global = base() + localIndex;
        return global < total;
    }

    @Override public int getContainerSize() { return VISIBLE; }

    private boolean inBackingBounds(int idx) {
        return idx >= 0 && idx < backing.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        int start = mirrorMode ? 0 : base();
        int end   = Math.min(start + VISIBLE, backing.getContainerSize());
        for (int i = start; i < end; i++) {
            if (!backing.getItem(i).isEmpty()) return false;
        }
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getItem(int localIndex) {
        int i = map(localIndex);
        if (!inBackingBounds(i)) return ItemStack.EMPTY;
        return backing.getItem(i);
    }

    @Override
    public void setItem(int localIndex, @Nonnull ItemStack stack) {
        int i = map(localIndex);
        if (!inBackingBounds(i)) return;
        backing.setItem(i, stack);
        setChanged();
    }

    @Nonnull
    @Override
    public ItemStack removeItem(int localIndex, int amount) {
        int i = map(localIndex);
        if (!inBackingBounds(i)) return ItemStack.EMPTY;
        ItemStack out = backing.removeItem(i, amount);
        setChanged();
        return out;
    }

    @Nonnull
    @Override
    public ItemStack removeItemNoUpdate(int localIndex) {
        int i = map(localIndex);
        if (!inBackingBounds(i)) return ItemStack.EMPTY;
        ItemStack out = backing.removeItemNoUpdate(i);
        setChanged();
        return out;
    }

    @Override public void clearContent() {
        int start = mirrorMode ? 0 : base();
        int end   = Math.min(start + VISIBLE, backing.getContainerSize());
        for (int i = start; i < end; i++) backing.setItem(i, ItemStack.EMPTY);
        setChanged();
    }

    @Override
    public boolean canPlaceItem(int localIndex, @Nonnull ItemStack stack) {
        // Interaction UX: bloque hors page
        return isInRange(localIndex) && backing.canPlaceItem(map(localIndex), stack);
    }

    @Override public void setChanged() { backing.setChanged(); }
    @Override public boolean stillValid(@Nonnull Player p) { return backing.stillValid(p); }
}