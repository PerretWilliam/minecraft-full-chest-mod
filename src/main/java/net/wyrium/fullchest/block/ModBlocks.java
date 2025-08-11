package net.wyrium.fullchest.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.block.entity.ModBlockEntities;
import net.wyrium.fullchest.template.BaseChestBlock;
import net.wyrium.fullchest.template.ChestSpec;
import net.wyrium.fullchest.template.ChestSpecs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, FullChest.MODID);

    public static final DeferredHolder<
            net.minecraft.world.level.block.Block,
            net.minecraft.world.level.block.Block
            > CHEST_FORGE = BLOCKS.register("chest_forge",
            () -> new ChestForgeBlock(
                    BlockBehaviour.Properties.of()
                            .strength(2.5F)
                            .requiresCorrectToolForDrops()
            ));

    /**
     * Register a single chest block for the given spec.
     */
    private static DeferredHolder<Block, Block> chest(String id, ChestSpec spec) {
        return BLOCKS.register(id + "_chest",
                () -> new BaseChestBlock(
                        BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST),
                        () -> ModBlockEntities.BASE_CHEST_BE.get(),
                        spec
                )
        );
    }

    // --- Central registries for iteration and lookups ---
    /** Keeps insertion order (same as ChestSpecs.ALL). */
    public static final Map<String, DeferredHolder<Block, Block>> BY_ID = new LinkedHashMap<>();
    public static final Map<ChestSpec, DeferredHolder<Block, Block>> BY_SPEC = new LinkedHashMap<>();
    public static final List<DeferredHolder<Block, Block>> ALL_CHESTS;

    // --- Named constants for convenience (assigned in static initializer) ---
    public static final DeferredHolder<Block, Block> DIRT_CHEST;
    public static final DeferredHolder<Block, Block> STONE_CHEST;
    public static final DeferredHolder<Block, Block> COPPER_CHEST;
    public static final DeferredHolder<Block, Block> IRON_CHEST;
    public static final DeferredHolder<Block, Block> GOLD_CHEST;
    public static final DeferredHolder<Block, Block> EMERALD_CHEST;
    public static final DeferredHolder<Block, Block> DIAMOND_CHEST;
    public static final DeferredHolder<Block, Block> OBSIDIAN_CHEST;
    public static final DeferredHolder<Block, Block> NETHERITE_CHEST;

    static {
        // Register every chest defined in ChestSpecs.ALL
        for (ChestSpec spec : ChestSpecs.ALL) {
            var holder = chest(spec.id(), spec);
            BY_ID.put(spec.id(), holder);
            BY_SPEC.put(spec, holder);
        }

        // Build a stable list for easy iteration elsewhere (datagen, tags, loot, etc.)
        ALL_CHESTS = List.copyOf(BY_ID.values());

        // Assign named constants (safe because keys are known in your progression)
        DIRT_CHEST      = BY_ID.get("dirt");
        STONE_CHEST     = BY_ID.get("stone");
        COPPER_CHEST    = BY_ID.get("copper");
        IRON_CHEST      = BY_ID.get("iron");
        GOLD_CHEST      = BY_ID.get("gold");
        EMERALD_CHEST   = BY_ID.get("emerald");
        DIAMOND_CHEST   = BY_ID.get("diamond");
        OBSIDIAN_CHEST  = BY_ID.get("obsidian");
        NETHERITE_CHEST = BY_ID.get("netherite");
    }

    /** Call this from your mod constructor to wire the register. */
    public static void register(net.neoforged.bus.api.IEventBus bus) {
        BLOCKS.register(bus);
    }
}
