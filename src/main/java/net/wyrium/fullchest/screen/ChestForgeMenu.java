package net.wyrium.fullchest.screen;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.wyrium.fullchest.block.entity.ChestForgeBlockEntity;
import net.wyrium.fullchest.screen.slot.FuelSlot;
import net.wyrium.fullchest.screen.slot.OutputSlot;
import org.jetbrains.annotations.NotNull;

/**
 * Server-side menu for the {@link ChestForgeBlockEntity}, defining slot layout and transfer logic.
 * <p>
 * Slots layout:
 * <ul>
 *   <li>Fuel slot (index 0)</li>
 *   <li>Output slot (index 1)</li>
 *   <li>3×3 input grid (indices 2..10)</li>
 *   <li>Player inventory (indices after BE slots)</li>
 *   <li>Player hotbar (last 9 slots)</li>
 * </ul>
 * Also syncs forge progress/burn data to the client via {@link ContainerData}.
 */
public class ChestForgeMenu extends AbstractContainerMenu {
    public static final int SLOT_FUEL = ChestForgeBlockEntity.SLOT_FUEL; // 0
    public static final int SLOT_OUT = ChestForgeBlockEntity.SLOT_OUT; // 1
    public static final int SLOT_IN_START = ChestForgeBlockEntity.SLOT_IN_START; // 2

    private final Container inv;
    private final ChestForgeBlockEntity be;
    private final ContainerData data;

    /**
     * @param id window id
     * @param playerInv player inventory reference
     * @param inv block entity inventory
     * @param be block entity instance
     * @param data synchronized data array for burn/progress values
     */
    public ChestForgeMenu(int id, Inventory playerInv, Container inv, ChestForgeBlockEntity be, ContainerData data) {
        super(ModMenuTypes.CHEST_FORGE.get(), id);
        this.inv = inv;
        this.be = be;
        this.data = data;

        // --- Block Entity Slots ---
        // Fuel
        this.addSlot(new FuelSlot(inv, SLOT_FUEL, 26, 33));
        // Output
        this.addSlot(new OutputSlot(inv, SLOT_OUT, 144, 33));
        // 3x3 input grid
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                this.addSlot(new Slot(inv, SLOT_IN_START + r * 3 + c, 50 + c * 18, 15 + r * 18));
            }
        }

        // --- Player Inventory Slots ---
        int startY = 84;
        // Main inventory (3 rows)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, startY + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, startY + 58));
        }

        // Sync forge progress/burn data
        this.addDataSlots(this.data);
    }

    @Override
    public boolean stillValid(@NotNull Player p) { return inv.stillValid(p); }

    /**
     * Handles shift-click item transfers between BE inventory and player inventory.
     * <ul>
     *   <li>BE → player: move all slots 0..10 into player inventory/hotbar.</li>
     *   <li>Player → BE: try fuel slot first (lava bucket), else input grid.</li>
     * </ul>
     */
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack ret = stack.copy();

        int beEnd = 11; // exclusive end index for BE inventory
        int total = this.slots.size();

        if (index < beEnd) {
            // From BE → player inventory
            if (!moveItemStackTo(stack, beEnd, total, true)) return ItemStack.EMPTY;
        } else {
            // From player → BE
            if (stack.is(net.minecraft.world.item.Items.LAVA_BUCKET)) {
                if (!moveItemStackTo(stack, SLOT_FUEL, SLOT_FUEL + 1, false))
                    return ItemStack.EMPTY;
            } else {
                if (!moveItemStackTo(stack, SLOT_IN_START, beEnd, false))
                    return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return ret;
    }

    // --- Data accessors for client-side rendering ---
    public int burnTime()         { return data.get(0); }
    public int burnTimeTotal()    { return data.get(1); }
    public int progress()         { return data.get(2); }
    public int maxProgress()      { return data.get(3); }

    public boolean isBurning()    { return burnTimeTotal() > 0 && burnTime() > 0; }
    public boolean isCrafting()   { return maxProgress() > 0 && progress() > 0; }

    public int getScaledFlame() {
        return burnTimeTotal() == 0 ? 0 :
                (int) Math.ceil(burnTime() * 14.0 / burnTimeTotal());
    }

    public int getScaledProgress() {
        return maxProgress() == 0 ? 0 :
                (int) Math.floor(progress() * 24.0 / maxProgress());
    }
}