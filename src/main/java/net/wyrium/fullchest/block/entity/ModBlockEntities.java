package net.wyrium.fullchest.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.block.ModBlocks;
import net.wyrium.fullchest.template.BaseChestBlock;
import net.wyrium.fullchest.template.BaseChestBlockEntity;

/**
 * Registers all Block Entity types used by the FullChest mod.
 * <p>
 * Block Entities (BEs) are used to store additional data and logic for blocks.
 * They are required for containers, ticking blocks, and custom block state behavior.
 */
public class ModBlockEntities {

    /** Main registry for all BlockEntityType entries in this mod. */
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, FullChest.MODID);

    /**
     * Helper method to get all registered chest blocks as an array.
     * <p>
     * This is used to register the {@link BaseChestBlockEntity} so it works with all chest variants.
     *
     * @return Array of all chest block instances in this mod.
     */
    private static Block[] allChestBlocks() {
        return ModBlocks.ALL_CHESTS
                .stream()
                .map(DeferredHolder::get)
                .toArray(Block[]::new);
    }

    /* =========================
       Chest Forge Block Entity
       ========================= */

    /**
     * Block Entity type for the {@link net.wyrium.fullchest.block.ChestForgeBlock}.
     * Handles the logic of the custom crafting/upgrade station.
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChestForgeBlockEntity>> CHEST_FORGE_BE =
            BLOCK_ENTITY_TYPES.register(
                    "chest_forge", () -> BlockEntityType.Builder.of(
                            ChestForgeBlockEntity::new, ModBlocks.CHEST_FORGE.get()).build(null)
            );

    /* =========================
       Base Chest Block Entity
       ========================= */

    /**
     * Block Entity type for all {@link BaseChestBlock} variants.
     * <p>
     * This registration:
     * <ul>
     *     <li>Ensures the BE can be deserialized for any registered chest variant.</li>
     *     <li>Passes the correct {@link net.wyrium.fullchest.template.ChestSpec} from the block to the BE.</li>
     * </ul>
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BaseChestBlockEntity>> BASE_CHEST_BE =
            BLOCK_ENTITY_TYPES.register(
                    "base_chest", () -> BlockEntityType.Builder.of(
                            // Factory method must match (BlockPos, BlockState)
                            (pos, state) -> {
                                if (!(state.getBlock() instanceof BaseChestBlock chest)) throw new IllegalStateException("BaseChestBlockEntity requires BaseChestBlock");

                                // Create the BE using the spec from the block
                                return new BaseChestBlockEntity(pos, state, chest.spec());
                            },
                            // Register for ALL chest block variants (important for NBT serialization)
                            allChestBlocks()).build(null)
            );

    /* =========================
       Registration Method
       ========================= */

    /**
     * Registers all block entity types with the given event bus.
     *
     * @param bus The mod event bus.
     */
    public static void register(IEventBus bus) {
        BLOCK_ENTITY_TYPES.register(bus);
    }
}
