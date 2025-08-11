package net.wyrium.fullchest.screen.slot;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Container;
import org.jetbrains.annotations.NotNull;

public class OutputSlot extends Slot {
    public OutputSlot(Container inv, int index, int x, int y) { super(inv, index, x, y); }
    @Override public boolean mayPlace(@NotNull ItemStack stack) { return false; }
}