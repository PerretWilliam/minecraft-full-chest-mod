package net.wyrium.fullchest.screen.slot;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.Container;
import org.jetbrains.annotations.NotNull;

public class FuelSlot extends Slot {
    public FuelSlot(Container inv, int index, int x, int y) { super(inv, index, x, y); }

    @Override public boolean mayPlace(@NotNull ItemStack stack) {
        // Only lava bucket, 1 per slot
        return stack.is(Items.LAVA_BUCKET) || stack.is(Items.BUCKET);
    }
    @Override public int getMaxStackSize(@NotNull ItemStack stack) { return 1; }
}
