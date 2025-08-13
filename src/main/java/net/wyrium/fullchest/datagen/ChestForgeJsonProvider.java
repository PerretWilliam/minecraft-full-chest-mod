package net.wyrium.fullchest.datagen;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Data generator for the "chest_forge" custom recipes.
 * <p>
 * This provider creates JSON recipe files for all chest types
 * (stone, copper, iron, gold, diamond, emerald, obsidian, netherite)
 * as well as their corresponding upgrade items.
 * <p>
 * The generated JSON files are placed in the data pack's recipe folder.
 */
public record ChestForgeJsonProvider(PackOutput packOutput) implements DataProvider {

    /**
     * Generates all recipe JSON files for chests and upgrades.
     */
    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        // Path provider for JSON recipe files inside the data pack
        var pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");

        /* ==================================================
         * CHEST RECIPES
         * ================================================== */

        // Stone Chest
        JsonObject stoneChest = ChestForgeRecipeBuilder.chestForge(ModItems.STONE_CHEST.get())
                .pattern("MMM")
                .pattern("MCM")
                .pattern("MMM")
                .define('M', Items.STONE)
                .define('C', ModItems.DIRT_CHEST.get())
                .time(300)       // Crafting time in ticks
                .mirror(true)    // Recipe can be mirrored
                .toJson();
        Path stoneChestPath = pathProvider.json(ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "stone_chest"));
        CompletableFuture<?> stoneChestRecipe = DataProvider.saveStable(cache, stoneChest, stoneChestPath);

        // Copper Chest
        JsonObject copperChest = ChestForgeRecipeBuilder.chestForge(ModItems.COPPER_CHEST.get())
                .pattern("MMM")
                .pattern("MCM")
                .pattern("MMM")
                .define('M', Items.COPPER_INGOT)
                .define('C', ModItems.STONE_CHEST.get())
                .time(300)
                .mirror(true)
                .toJson();
        Path copperChestPath = pathProvider.json(ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "copper_chest"));
        CompletableFuture<?> copperChestRecipe = DataProvider.saveStable(cache, copperChest, copperChestPath);

        // Iron Chest
        JsonObject ironChest = ChestForgeRecipeBuilder.chestForge(ModItems.IRON_CHEST.get())
                .pattern("MMM")
                .pattern("MCM")
                .pattern("MMM")
                .define('M', Items.IRON_INGOT)
                .define('C', ModItems.COPPER_CHEST.get())
                .time(300)
                .mirror(true)
                .toJson();
        Path ironChestPath = pathProvider.json(ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "iron_chest"));
        CompletableFuture<?> ironChestRecipe = DataProvider.saveStable(cache, ironChest, ironChestPath);

        // Gold Chest
        JsonObject goldChest = ChestForgeRecipeBuilder.chestForge(ModItems.GOLD_CHEST.get())
                .pattern("MMM")
                .pattern("MCM")
                .pattern("MMM")
                .define('M', Items.GOLD_INGOT)
                .define('C', ModItems.IRON_CHEST.get())
                .time(300)
                .mirror(true)
                .toJson();
        Path goldChestPath = pathProvider.json(ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "gold_chest"));
        CompletableFuture<?> goldChestRecipe = DataProvider.saveStable(cache, goldChest, goldChestPath);

        // Diamond Chest
        JsonObject diamondChest = ChestForgeRecipeBuilder.chestForge(ModItems.DIAMOND_CHEST.get())
                .pattern("MMM")
                .pattern("MCM")
                .pattern("MMM")
                .define('M', Items.DIAMOND)
                .define('C', ModItems.GOLD_CHEST.get())
                .time(300)
                .mirror(true)
                .toJson();
        Path diamondChestPath = pathProvider.json(ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "diamond_chest"));
        CompletableFuture<?> diamondChestRecipe = DataProvider.saveStable(cache, diamondChest, diamondChestPath);

        // Emerald Chest
        JsonObject emeraldChest = ChestForgeRecipeBuilder.chestForge(ModItems.EMERALD_CHEST.get())
                .pattern("MMM")
                .pattern("MCM")
                .pattern("MMM")
                .define('M', Items.EMERALD)
                .define('C', ModItems.DIAMOND_CHEST.get())
                .time(300)
                .mirror(true)
                .toJson();
        Path emeraldChestPath = pathProvider.json(ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "emerald_chest"));
        CompletableFuture<?> emeraldChestRecipe = DataProvider.saveStable(cache, emeraldChest, emeraldChestPath);

        // Obsidian Chest
        JsonObject obsidianChest = ChestForgeRecipeBuilder.chestForge(ModItems.OBSIDIAN_CHEST.get())
                .pattern("MMM")
                .pattern("MCM")
                .pattern("MMM")
                .define('M', Items.OBSIDIAN)
                .define('C', ModItems.EMERALD_CHEST.get())
                .time(300)
                .mirror(true)
                .toJson();
        Path obsidianChestPath = pathProvider.json(ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "obsidian_chest"));
        CompletableFuture<?> obsidianChestRecipe = DataProvider.saveStable(cache, obsidianChest, obsidianChestPath);

        // Netherite Chest
        JsonObject netheriteChest = ChestForgeRecipeBuilder.chestForge(ModItems.NETHERITE_CHEST.get())
                .pattern("MMM")
                .pattern("MCM")
                .pattern("MMM")
                .define('M', Items.NETHERITE_INGOT)
                .define('C', ModItems.OBSIDIAN_CHEST.get())
                .time(300)
                .mirror(true)
                .toJson();
        Path netheriteChestPath = pathProvider.json(ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "netherite_chest"));
        CompletableFuture<?> netheriteChestRecipe = DataProvider.saveStable(cache, netheriteChest, netheriteChestPath);

        /* ==================================================
         * UPGRADE ITEM RECIPES
         * ================================================== */

        // Each upgrade follows the same pattern:
        // - Outer ring made of upgrade material
        // - Center is the previous tier's upgrade item
        // - 300 ticks crafting time
        // - Mirrored pattern allowed

        JsonObject dirtToStoneUpgrade = ChestForgeRecipeBuilder.chestForge(ModItems.DIRT_TO_STONE_UPGRADE.get())
                .pattern("SSS")
                .pattern("SCS")
                .pattern("SSS")
                .define('S', Items.STONE)
                .define('C', ModItems.BASE_CHEST_UPGRADE.get())
                .time(300)
                .mirror(true)
                .toJson();
        Path dirtToStoneUpgradePath = pathProvider.json(ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "dirt_to_stone_upgrade"));
        CompletableFuture<?> dirtToStoneUpgradeRecipe = DataProvider.saveStable(cache, dirtToStoneUpgrade, dirtToStoneUpgradePath);

        JsonObject stoneToCopperUpgrade = ChestForgeRecipeBuilder.chestForge(ModItems.STONE_TO_COPPER_UPGRADE.get())
                .pattern("CCC")
                .pattern("CSC")
                .pattern("CCC")
                .define('C', Items.COPPER_INGOT)
                .define('S', ModItems.DIRT_TO_STONE_UPGRADE.get())
                .time(300)
                .mirror(true)
                .toJson();
        Path stoneToCopperUpgradePath = pathProvider.json(ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "stone_to_copper_upgrade"));
        CompletableFuture<?> stoneToCopperUpgradeRecipe = DataProvider.saveStable(cache, stoneToCopperUpgrade, stoneToCopperUpgradePath);

        JsonObject copperToIronUpgrade = ChestForgeRecipeBuilder.chestForge(ModItems.COPPER_TO_IRON_UPGRADE.get())
                .pattern("III")
                .pattern("ICI")
                .pattern("III")
                .define('I', Items.IRON_INGOT)
                .define('C', ModItems.STONE_TO_COPPER_UPGRADE.get())
                .time(300)
                .mirror(true)
                .toJson();
        Path copperToIronUpgradePath = pathProvider.json(ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "copper_to_iron_upgrade"));
        CompletableFuture<?> copperToIronUpgradeRecipe = DataProvider.saveStable(cache, copperToIronUpgrade, copperToIronUpgradePath);

        JsonObject ironToGoldUpgrade = ChestForgeRecipeBuilder.chestForge(ModItems.IRON_TO_GOLD_UPGRADE.get())
                .pattern("GGG")
                .pattern("GIG")
                .pattern("GGG")
                .define('G', Items.GOLD_INGOT)
                .define('I', ModItems.COPPER_TO_IRON_UPGRADE.get())
                .time(300)
                .mirror(true)
                .toJson();
        Path ironToGoldUpgradePath = pathProvider.json(ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "iron_to_gold_upgrade"));
        CompletableFuture<?> ironToGoldUpgradeRecipe = DataProvider.saveStable(cache, ironToGoldUpgrade, ironToGoldUpgradePath);

        JsonObject goldToDiamondUpgrade = ChestForgeRecipeBuilder.chestForge(ModItems.GOLD_TO_DIAMOND_UPGRADE.get())
                .pattern("DDD")
                .pattern("DGD")
                .pattern("DDD")
                .define('D', Items.DIAMOND)
                .define('G', ModItems.IRON_TO_GOLD_UPGRADE.get())
                .time(300)
                .mirror(true)
                .toJson();
        Path goldToDiamondUpgradePath = pathProvider.json(ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "gold_to_diamond_upgrade"));
        CompletableFuture<?> goldToDiamondUpgradeRecipe = DataProvider.saveStable(cache, goldToDiamondUpgrade, goldToDiamondUpgradePath);

        JsonObject diamondToEmeraldUpgrade = ChestForgeRecipeBuilder.chestForge(ModItems.DIAMOND_TO_EMERALD_UPGRADE.get())
                .pattern("EEE")
                .pattern("EDE")
                .pattern("EEE")
                .define('E', Items.EMERALD)
                .define('D', ModItems.GOLD_TO_DIAMOND_UPGRADE.get())
                .time(300)
                .mirror(true)
                .toJson();
        Path diamondToEmeraldUpgradePath = pathProvider.json(ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "diamond_to_emerald_upgrade"));
        CompletableFuture<?> diamondToEmeraldUpgradeRecipe = DataProvider.saveStable(cache, diamondToEmeraldUpgrade, diamondToEmeraldUpgradePath);

        JsonObject emeraldToObsidianUpgrade = ChestForgeRecipeBuilder.chestForge(ModItems.EMERALD_TO_OBSIDIAN_UPGRADE.get())
                .pattern("OOO")
                .pattern("OEO")
                .pattern("OOO")
                .define('O', Items.OBSIDIAN)
                .define('E', ModItems.DIAMOND_TO_EMERALD_UPGRADE.get())
                .time(300)
                .mirror(true)
                .toJson();
        Path emeraldToObsidianUpgradePath = pathProvider.json(ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "emerald_to_obsidian_upgrade"));
        CompletableFuture<?> emeraldToObsidianUpgradeRecipe = DataProvider.saveStable(cache, emeraldToObsidianUpgrade, emeraldToObsidianUpgradePath);

        JsonObject obsidianToNetheriteUpgrade = ChestForgeRecipeBuilder.chestForge(ModItems.OBSIDIAN_TO_NETHERITE_UPGRADE.get())
                .pattern("NNN")
                .pattern("NON")
                .pattern("NNN")
                .define('N', Items.NETHERITE_INGOT)
                .define('O', ModItems.EMERALD_TO_OBSIDIAN_UPGRADE.get())
                .time(300)
                .mirror(true)
                .toJson();
        Path obsidianToNetheriteUpgradePath = pathProvider.json(ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "obsidian_to_netherite_upgrade"));
        CompletableFuture<?> obsidianToNetheriteUpgradeRecipe = DataProvider.saveStable(cache, obsidianToNetheriteUpgrade, obsidianToNetheriteUpgradePath);

        // Return all futures combined
        return CompletableFuture.allOf(
                stoneChestRecipe, copperChestRecipe, ironChestRecipe, goldChestRecipe,
                diamondChestRecipe, emeraldChestRecipe, obsidianChestRecipe, netheriteChestRecipe,
                dirtToStoneUpgradeRecipe, stoneToCopperUpgradeRecipe, copperToIronUpgradeRecipe,
                ironToGoldUpgradeRecipe, goldToDiamondUpgradeRecipe, diamondToEmeraldUpgradeRecipe,
                emeraldToObsidianUpgradeRecipe, obsidianToNetheriteUpgradeRecipe
        );
    }

    @Override
    public @NotNull String getName() {
        return "FullChest - chest_forge JSON recipes";
    }
}
