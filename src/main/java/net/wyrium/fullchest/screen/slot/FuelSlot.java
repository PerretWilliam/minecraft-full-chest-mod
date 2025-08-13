package net.wyrium.fullchest.screen.slot;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.Container;
import org.jetbrains.annotations.NotNull;

/**
 * Slot used for the forge's fuel input.
 * <p>
 * Current behavior:
 * <ul>
 *   <li>Accepts exactly one item per slot ({@link #getMaxStackSize(ItemStack)} = 1).</li>
 *   <li>Accepts <b>lava buckets</b> <em>and</em> <b>empty buckets</b> (see {@link #mayPlace(ItemStack)}).</li>
 * </ul>
 * Note: The inline comment says "Only lava bucket", but the code also allows {@code Items.BUCKET}.
 * Keep or remove that condition depending on design.
 */
public class FuelSlot extends Slot {
    public FuelSlot(Container inv, int index, int x, int y) { super(inv, index, x, y); }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        // Currently allows both LAVA_BUCKET and empty BUCKET.
        return stack.is(Items.LAVA_BUCKET) || stack.is(Items.BUCKET);
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return 1;
    }
}
