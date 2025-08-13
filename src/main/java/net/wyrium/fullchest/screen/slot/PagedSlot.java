package net.wyrium.fullchest.screen.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.wyrium.fullchest.screen.PagedViewContainer;
import org.jetbrains.annotations.NotNull;

/**
 * Slot wrapper used for a paged inventory view (e.g., large chests split into multiple pages).
 * <p>
 * Behavior:
 * <ul>
 *   <li>Delegates to a {@link PagedViewContainer} to determine if the slot is within the
 *       currently visible page.</li>
 *   <li>Prevents placing, picking up, or interacting with items if the slot is outside
 *       the visible range for the current page.</li>
 *   <li>Controls the slot's active state so it can be visually disabled in the GUI.</li>
 * </ul>
 */
public class PagedSlot extends Slot {
    private final PagedViewContainer view;

    /**
     * Creates a paged slot bound to a specific {@link PagedViewContainer}.
     *
     * @param view   the paged view container that manages visible slots
     * @param index  the slot index within the visible page
     * @param x      the X coordinate in the GUI
     * @param y      the Y coordinate in the GUI
     */
    public PagedSlot(PagedViewContainer view, int index, int x, int y) {
        super(view, index, x, y);
        this.view = view;
    }

    /**
     * Checks if items can be placed in this slot.
     * Only allowed if the slot is within the currently visible page.
     */
    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return view.isInRange(this.getSlotIndex());
    }

    /**
     * Checks if the player can pick up items from this slot.
     * Only allowed if the slot is within the currently visible page.
     */
    @Override
    public boolean mayPickup(@NotNull Player player) {
        return view.isInRange(this.getSlotIndex()) && super.mayPickup(player);
    }

    /**
     * Indicates whether the slot is active in the GUI.
     * Used to render a "disabled" overlay for inactive slots.
     */
    @Override
    public boolean isActive() {
        return view.isInRange(this.getSlotIndex());
    }
}
