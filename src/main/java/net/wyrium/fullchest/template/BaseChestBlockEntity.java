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

/**
 * Block entity backing {@link BaseChestBlock}. Stores items, persists them to NBT,
 * and exposes a paginated container menu based on a provided {@link ChestSpec}.
 * <p>
 * Key points:
 * <ul>
 *   <li>Inventory size is driven by {@code ChestSpec#totalSlots()}.</li>
 *   <li>Title is resolved from {@code ChestSpec#titleKey()}.</li>
 *   <li>Loot tables are supported via the standard vanilla helpers.</li>
 *   <li>Implements {@link MenuProvider} for UI and {@link LidBlockEntity} for lid behavior hooks.</li>
 * </ul>
 */
public class BaseChestBlockEntity extends ChestBlockEntity implements MenuProvider, LidBlockEntity {
    /** Immutable chest configuration (capacity, title, etc.). */
    private final ChestSpec spec;

    /** Backing list for the inventory contents. Size follows {@link #spec}. */
    private NonNullList<ItemStack> items;

    /**
     * @param pos   block position
     * @param state current block state
     * @param spec  chest configuration used for size/title/behavior
     */
    public BaseChestBlockEntity(BlockPos pos, BlockState state, ChestSpec spec) {
        super(ModBlockEntities.BASE_CHEST_BE.get(), pos, state);
        this.spec = spec;
        this.items = NonNullList.withSize(spec.totalSlots(), ItemStack.EMPTY);
    }

    /** Exposes the chest spec to callers (e.g., containers/menus). */
    public ChestSpec spec() { return spec; }

    /** Localized default name displayed in the UI. */
    @Nonnull
    @Override
    protected Component getDefaultName() {
        return Component.translatable(spec.titleKey());
    }

    /** Returns the live backing list for inventory access. */
    @Nonnull @Override
    public NonNullList<ItemStack> getItems() { return items; }

    /** Replaces the internal inventory list (used by vanilla helpers). */
    @Override
    protected void setItems(@Nonnull NonNullList<ItemStack> items) { this.items = items; }

    /**
     * Creates the container menu. Uses a custom paged menu that can handle large inventories.
     *
     * @param containerId sync id
     * @param inv         the player's inventory
     */
    @Nonnull @Override
    protected AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory inv) {
        return new PagedChestMenu(containerId, inv, this);
    }

    /** Report container size to vanilla; driven entirely by the spec. */
    @Override
    public int getContainerSize() { return spec.totalSlots(); }

    /**
     * Save inventory to NBT. If a loot table is present, defer to vanilla behavior.
     */
    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, registries);
        }
    }

    /**
     * Load inventory from NBT. If a loot table is present, vanilla will populate on first open.
     */
    @Override
    protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (!tryLoadLootTable(tag)) {
            this.items = NonNullList.withSize(spec.totalSlots(), ItemStack.EMPTY);
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }
    }

    /**
     * Standard proximity check (8 blocks = 64.0 distance squared) so players
     * must be nearby to interact.
     */
    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(worldPosition.getCenter()) <= 64.0D;
    }
}
