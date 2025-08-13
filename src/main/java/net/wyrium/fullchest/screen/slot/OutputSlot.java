package net.wyrium.fullchest.screen.slot;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Container;
import org.jetbrains.annotations.NotNull;

/**
 * Slot representing the output of a container (e.g., crafting or smelting result).
 * <p>
 * Characteristics:
 * <ul>
 *   <li><b>Read-only:</b> The player cannot place items into this slot
 *       ({@link #mayPlace(ItemStack)} always returns {@code false}).</li>
 *   <li>Intended for retrieving finished products, not for input.</li>
 * </ul>
 */
public class OutputSlot extends Slot {

    /**
     * Creates an output slot at the given position.
     *
     * @param inv the container inventory
     * @param index the slot index within the container
     * @param x the X coordinate (GUI position)
     * @param y the Y coordinate (GUI position)
     */
    public OutputSlot(Container inv, int index, int x, int y) {
        super(inv, index, x, y);
    }

    /**
     * Prevents placing any item in this slot.
     *
     * @param stack the stack being tested
     * @return always {@code false}
     */
    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }
}
