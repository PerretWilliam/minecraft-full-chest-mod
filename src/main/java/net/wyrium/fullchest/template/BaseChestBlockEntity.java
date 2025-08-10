package net.wyrium.fullchest.template;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.wyrium.fullchest.block.entity.ModBlockEntities;
import net.wyrium.fullchest.screen.PagedChestMenu;

import javax.annotation.Nonnull;

// BaseChestBlockEntity.java
public class BaseChestBlockEntity extends ChestBlockEntity implements MenuProvider, LidBlockEntity {
    private final ChestSpec spec;
    private NonNullList<ItemStack> items;

    public BaseChestBlockEntity(BlockPos pos, BlockState state, ChestSpec spec) {
        super(ModBlockEntities.BASE_CHEST_BE.get(), pos, state); // un seul type pour tous ces coffres
        this.spec = spec;
        this.items = NonNullList.withSize(spec.totalSlots(), ItemStack.EMPTY);
    }

    public ChestSpec spec() { return spec; }

    @Nonnull
    @Override
    protected Component getDefaultName() {
        return Component.translatable(spec.titleKey());
    }

    @Nonnull @Override
    public NonNullList<ItemStack> getItems() { return items; }

    @Override
    protected void setItems(@Nonnull NonNullList<ItemStack> items) { this.items = items; }

    @Nonnull @Override
    protected AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory inv) {
        return new PagedChestMenu(containerId, inv, this); // menu commun avec pagination
    }

    @Override
    public int getContainerSize() { return spec.totalSlots(); }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, registries);
        }
    }

    @Override
    protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (!tryLoadLootTable(tag)) {
            this.items = NonNullList.withSize(spec.totalSlots(), ItemStack.EMPTY);
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(worldPosition.getCenter()) <= 64.0D;
    }
}
