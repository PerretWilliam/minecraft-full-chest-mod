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

public class ChestForgeMenu extends AbstractContainerMenu {
    public static final int SLOT_FUEL     = ChestForgeBlockEntity.SLOT_FUEL;     // 0
    public static final int SLOT_OUT      = ChestForgeBlockEntity.SLOT_OUT;      // 1
    public static final int SLOT_IN_START = ChestForgeBlockEntity.SLOT_IN_START; // 2

    private final Container inv;

    private final ChestForgeBlockEntity be;
    private final ContainerData data;

    public ChestForgeMenu(int id, Inventory playerInv, Container inv, ChestForgeBlockEntity be, ContainerData data) {
        super(ModMenuTypes.CHEST_FORGE.get(), id);
        this.inv = inv;
        this.be = be;
        this.data = data;

        // Fuel Slot
        this.addSlot(new FuelSlot(inv, SLOT_FUEL, 26, 33));

        // Output Slot
        this.addSlot(new OutputSlot(inv, SLOT_OUT, 144, 33));

        // Slots grille 3x3
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                this.addSlot(new Slot(inv,
                        SLOT_IN_START + r * 3 + c, // So 2..10
                        50 + c * 18,
                        15 + r * 18));
            }
        }

        // Slots inventaire joueur (classique)
        int startY = 84;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv,
                        col + row * 9 + 9,
                        8 + col * 18,
                        startY + row * 18));
            }
        }

        // Slots hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, startY + 58));
        }
        this.addDataSlots(this.data);
    }

    @Override public boolean stillValid(@NotNull Player p) { return inv.stillValid(p); }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack ret = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ret = stack.copy();

        int beEnd = 11;
        int total = this.slots.size();

        if (index < beEnd) {
            // from BE -> player
            if (!moveItemStackTo(stack, beEnd, total, true)) return ItemStack.EMPTY;
        } else {
            // from player -> try fuel, then inputs
            if (stack.is(net.minecraft.world.item.Items.LAVA_BUCKET)) {
                if (!moveItemStackTo(stack, ChestForgeBlockEntity.SLOT_FUEL, ChestForgeBlockEntity.SLOT_FUEL + 1, false))
                    return ItemStack.EMPTY;
            } else {
                if (!moveItemStackTo(stack, ChestForgeBlockEntity.SLOT_IN_START, beEnd, false))
                    return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        return ret;
    }

    public int burnTime() { return data.get(0); }
    public int burnTimeTotal() { return data.get(1); }
    public int progress() { return data.get(2); }
    public int maxProgress() { return data.get(3); }

    public boolean isBurning()     { return burnTimeTotal() > 0 && burnTime() > 0; }
    public boolean isCrafting()    { return maxProgress() > 0 && progress() > 0; }
    public int getScaledFlame()    { return burnTimeTotal() == 0 ? 0 : (int)Math.ceil(burnTime() * 14.0 / burnTimeTotal()); }
    public int getScaledProgress() { return maxProgress() == 0   ? 0 : (int)Math.floor(progress() * 24.0 / maxProgress()); }

}
