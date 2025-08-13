package net.wyrium.fullchest.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.block.entity.ModBlockEntities;
import net.wyrium.fullchest.sound.ModSoundTypes;
import net.wyrium.fullchest.template.BaseChestBlock;
import net.wyrium.fullchest.template.ChestSpec;
import net.wyrium.fullchest.template.ChestSpecs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles registration of all blocks for the FullChest mod.
 * <p>
 * Includes:
 * <ul>
 *   <li>Special functional blocks (e.g., Chest Forge)</li>
 *   <li>All chest tier variants defined in {@link ChestSpecs}</li>
 * </ul>
 */
public class ModBlocks {

    /** Global block registry for this mod. */
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, FullChest.MODID);

    /** The Chest Forge block — a custom crafting/upgrade station for chests. */
    public static final DeferredHolder<Block, Block> CHEST_FORGE =
            BLOCKS.register("chest_forge", () -> new ChestForgeBlock(BlockBehaviour.Properties.of()
                    .strength(2.5F)
                    .requiresCorrectToolForDrops()
            ));

    /**
     * Registers a single chest block for the given chest specification.
     *
     * @param id   The base ID (e.g. "dirt", "stone")
     * @param spec The chest spec defining capacity, texture, sound, etc.
     * @return     A {@link DeferredHolder} reference to the registered chest block.
     */
    private static DeferredHolder<Block, Block> chest(String id, ChestSpec spec) {
        return BLOCKS.register(id + "_chest", () -> new BaseChestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST)
                .sound(ModSoundTypes.forSpec(spec)), ModBlockEntities.BASE_CHEST_BE::get, spec)
        );
    }

    /* =========================
       Central registries
       ========================= */

    /** Map of chest IDs to their registered block instances (keeps insertion order). */
    public static final Map<String, DeferredHolder<Block, Block>> BY_ID = new LinkedHashMap<>();

    /** Map of chest specs to their registered block instances. */
    public static final Map<ChestSpec, DeferredHolder<Block, Block>> BY_SPEC = new LinkedHashMap<>();

    /** Immutable list of all registered chest blocks (stable order). */
    public static final List<DeferredHolder<Block, Block>> ALL_CHESTS;

    /* =========================
       Named constants for convenience
       ========================= */
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
        // Register every chest variant defined in ChestSpecs.ALL
        for (ChestSpec spec : ChestSpecs.ALL) {
            var holder = chest(spec.id(), spec);
            BY_ID.put(spec.id(), holder);
            BY_SPEC.put(spec, holder);
        }

        // Create an immutable list for easier iteration elsewhere (tags, loot tables, datagen, etc.)
        ALL_CHESTS = List.copyOf(BY_ID.values());

        // Assign named constants for direct use in code (safe because chest IDs are fixed)
        DIRT_CHEST = BY_ID.get("dirt");
        STONE_CHEST = BY_ID.get("stone");
        COPPER_CHEST = BY_ID.get("copper");
        IRON_CHEST = BY_ID.get("iron");
        GOLD_CHEST = BY_ID.get("gold");
        EMERALD_CHEST = BY_ID.get("emerald");
        DIAMOND_CHEST = BY_ID.get("diamond");
        OBSIDIAN_CHEST = BY_ID.get("obsidian");
        NETHERITE_CHEST = BY_ID.get("netherite");
    }

    /**
     * Registers all blocks to the mod event bus.
     *
     * @param bus The mod's event bus from {@link net.neoforged.bus.api.IEventBus}.
     */
    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }

    /** Private constructor to prevent instantiation. */
    private ModBlocks() {}
}
