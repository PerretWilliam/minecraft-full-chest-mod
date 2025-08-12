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

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, FullChest.MODID);

    // Helper to collect all chest blocks into an array for the builder varargs
    private static Block[] allChestBlocks() {
        return ModBlocks.ALL_CHESTS.stream().map(DeferredHolder::get).toArray(Block[]::new);
    }

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChestForgeBlockEntity>> CHEST_FORGE_BE =
            BLOCK_ENTITY_TYPES.register("chest_forge",
                    () -> BlockEntityType.Builder.of(ChestForgeBlockEntity::new, ModBlocks.CHEST_FORGE.get())
                            .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BaseChestBlockEntity>> BASE_CHEST_BE =
            BLOCK_ENTITY_TYPES.register("base_chest",
                    () -> BlockEntityType.Builder.of(
                            // Factory MUST match (BlockPos, BlockState)
                            (pos, state) -> {
                                if (!(state.getBlock() instanceof BaseChestBlock chest)) {
                                    throw new IllegalStateException("BaseChestBlockEntity requires BaseChestBlock");
                                }
                                // Pass the spec from the block to the BE
                                return new BaseChestBlockEntity(pos, state, chest.spec());
                            },
                            // IMPORTANT: include ALL chest blocks here, otherwise NBT (de)serialization will fail
                            allChestBlocks()
                    ).build(null));

    // Register Method
    public static void register(IEventBus bus) {
        BLOCK_ENTITY_TYPES.register(bus);
    }
}
