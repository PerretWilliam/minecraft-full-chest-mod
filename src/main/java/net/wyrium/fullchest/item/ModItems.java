package net.wyrium.fullchest.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.block.ModBlocks;
import net.wyrium.fullchest.template.ChestUpgradeItem;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles registration of all items for the FullChest mod.
 * <p>
 * Includes:
 * <ul>
 *   <li>Block items for all chest block variants</li>
 *   <li>Special items like the Chest Forge</li>
 *   <li>Upgrade items to transform one chest tier into another</li>
 * </ul>
 */
public class ModItems {

    /** Global item registry for this mod. */
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, FullChest.MODID);

    /** Block item for the Chest Forge block. */
    public static final DeferredHolder<Item, Item> CHEST_FORGE =
            ITEMS.register("chest_forge", () -> new BlockItem(ModBlocks.CHEST_FORGE.get(), new Item.Properties()));

    /**
     * Map of chest IDs (e.g. "dirt", "stone") to their corresponding BlockItem.
     * Populated dynamically from {@link ModBlocks#BY_ID}.
     */
    public static final Map<String, DeferredHolder<Item, BlockItem>> BY_ID = new LinkedHashMap<>();

    /** Immutable list of all registered BlockItems for chests. */
    public static final List<DeferredHolder<Item, BlockItem>> ALL_ITEMS;

    // Named constants for direct access to specific chest block items.
    public static final DeferredHolder<Item, BlockItem> DIRT_CHEST;
    public static final DeferredHolder<Item, BlockItem> STONE_CHEST;
    public static final DeferredHolder<Item, BlockItem> COPPER_CHEST;
    public static final DeferredHolder<Item, BlockItem> IRON_CHEST;
    public static final DeferredHolder<Item, BlockItem> GOLD_CHEST;
    public static final DeferredHolder<Item, BlockItem> EMERALD_CHEST;
    public static final DeferredHolder<Item, BlockItem> DIAMOND_CHEST;
    public static final DeferredHolder<Item, BlockItem> OBSIDIAN_CHEST;
    public static final DeferredHolder<Item, BlockItem> NETHERITE_CHEST;

    static {
        // Create a matching BlockItem for every chest block defined in ModBlocks
        ModBlocks.BY_ID.forEach((id, blockHolder) -> {
            DeferredHolder<Item, BlockItem> itemHolder = ITEMS.register(
                    id + "_chest",
                    () -> new BlockItem(blockHolder.get(), new Item.Properties())
            );
            BY_ID.put(id, itemHolder);
        });

        ALL_ITEMS = List.copyOf(BY_ID.values());

        // Assign constants for easier access to specific chest variants
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

    /* =========================
       Chest Upgrade Items
       ========================= */

    /** Base upgrade item (non-functional placeholder). */
    public static final DeferredHolder<Item, Item> BASE_CHEST_UPGRADE =
            ITEMS.register("base_chest_upgrade", () -> new Item(new Item.Properties()));

    // Upgrade items for tier progression
    public static final DeferredHolder<Item, Item> DIRT_TO_STONE_UPGRADE =
            ITEMS.register("dirt_chest_to_stone_chest", () -> new ChestUpgradeItem(new Item.Properties(),
                    ModBlocks.DIRT_CHEST, ModBlocks.STONE_CHEST));

    public static final DeferredHolder<Item, Item> STONE_TO_COPPER_UPGRADE =
            ITEMS.register("stone_chest_to_copper_chest", () -> new ChestUpgradeItem(new Item.Properties(),
                    ModBlocks.STONE_CHEST, ModBlocks.COPPER_CHEST));

    public static final DeferredHolder<Item, Item> COPPER_TO_IRON_UPGRADE =
            ITEMS.register("copper_chest_to_iron_chest", () -> new ChestUpgradeItem(new Item.Properties(),
                    ModBlocks.COPPER_CHEST, ModBlocks.IRON_CHEST));

    public static final DeferredHolder<Item, Item> IRON_TO_GOLD_UPGRADE =
            ITEMS.register("iron_chest_to_gold_chest", () -> new ChestUpgradeItem(new Item.Properties(),
                    ModBlocks.IRON_CHEST, ModBlocks.GOLD_CHEST));

    public static final DeferredHolder<Item, Item> GOLD_TO_DIAMOND_UPGRADE =
            ITEMS.register("gold_chest_to_diamond_chest", () -> new ChestUpgradeItem(new Item.Properties(),
                    ModBlocks.GOLD_CHEST, ModBlocks.DIAMOND_CHEST));

    public static final DeferredHolder<Item, Item> DIAMOND_TO_EMERALD_UPGRADE =
            ITEMS.register("diamond_chest_to_emerald_chest", () -> new ChestUpgradeItem(new Item.Properties(),
                    ModBlocks.DIAMOND_CHEST, ModBlocks.EMERALD_CHEST));

    public static final DeferredHolder<Item, Item> EMERALD_TO_OBSIDIAN_UPGRADE =
            ITEMS.register("emerald_chest_to_obsidian_chest", () -> new ChestUpgradeItem(new Item.Properties(),
                    ModBlocks.EMERALD_CHEST, ModBlocks.OBSIDIAN_CHEST));

    public static final DeferredHolder<Item, Item> OBSIDIAN_TO_NETHERITE_UPGRADE =
            ITEMS.register("obsidian_chest_to_netherite_chest", () -> new ChestUpgradeItem(new Item.Properties(),
                    ModBlocks.OBSIDIAN_CHEST, ModBlocks.NETHERITE_CHEST));

    /** Immutable list of all upgrade items in tier order. */
    public static final List<DeferredHolder<Item, Item>> ALL_UPGRADES = List.of(
            DIRT_TO_STONE_UPGRADE,
            STONE_TO_COPPER_UPGRADE,
            COPPER_TO_IRON_UPGRADE,
            IRON_TO_GOLD_UPGRADE,
            GOLD_TO_DIAMOND_UPGRADE,
            DIAMOND_TO_EMERALD_UPGRADE,
            EMERALD_TO_OBSIDIAN_UPGRADE,
            OBSIDIAN_TO_NETHERITE_UPGRADE
    );

    /**
     * Registers all items to the given mod event bus.
     *
     * @param bus The mod's event bus from {@link net.neoforged.bus.api.IEventBus}.
     */
    public static void register(net.neoforged.bus.api.IEventBus bus) {
        ITEMS.register(bus);
    }

    /** Private constructor to prevent instantiation. */
    private ModItems() {}
}
