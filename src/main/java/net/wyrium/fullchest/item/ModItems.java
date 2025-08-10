package net.wyrium.fullchest.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.block.ModBlocks;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, FullChest.MODID);

    // Use wildcard to accept BlockItem (or any future custom item)
    public static final Map<String, DeferredHolder<Item, ? extends Item>> BY_ID = new LinkedHashMap<>();
    public static final List<DeferredHolder<Item, ? extends Item>> ALL_ITEMS;

    // Named constants (typed as BlockItem since that's what we register)
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
        // Mirror ModBlocks.BY_ID to create matching BlockItems
        ModBlocks.BY_ID.forEach((id, blockHolder) -> {
            DeferredHolder<Item, BlockItem> itemHolder = ITEMS.register(id + "_chest",
                    () -> new BlockItem(blockHolder.get(), new Item.Properties()));
            BY_ID.put(id, itemHolder);
        });

        ALL_ITEMS = List.copyOf(BY_ID.values());

        // Assign named constants
        DIRT_CHEST      = (DeferredHolder<Item, BlockItem>) BY_ID.get("dirt");
        STONE_CHEST     = (DeferredHolder<Item, BlockItem>) BY_ID.get("stone");
        COPPER_CHEST    = (DeferredHolder<Item, BlockItem>) BY_ID.get("copper");
        IRON_CHEST      = (DeferredHolder<Item, BlockItem>) BY_ID.get("iron");
        GOLD_CHEST      = (DeferredHolder<Item, BlockItem>) BY_ID.get("gold");
        EMERALD_CHEST   = (DeferredHolder<Item, BlockItem>) BY_ID.get("emerald");
        DIAMOND_CHEST   = (DeferredHolder<Item, BlockItem>) BY_ID.get("diamond");
        OBSIDIAN_CHEST  = (DeferredHolder<Item, BlockItem>) BY_ID.get("obsidian");
        NETHERITE_CHEST = (DeferredHolder<Item, BlockItem>) BY_ID.get("netherite");
    }

    public static void register(net.neoforged.bus.api.IEventBus bus) {
        ITEMS.register(bus);
    }
}
