package net.wyrium.fullchest.screen;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PagedSlot extends Slot {
    private final PagedViewContainer view;

    public PagedSlot(PagedViewContainer view, int index, int x, int y) {
        super(view, index, x, y);
        this.view = view;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return view.isInRange(this.getSlotIndex());
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return view.isInRange(this.getSlotIndex()) && super.mayPickup(player);
    }

    @Override
    public boolean isActive() {
        return view.isInRange(this.getSlotIndex());
    }

}

