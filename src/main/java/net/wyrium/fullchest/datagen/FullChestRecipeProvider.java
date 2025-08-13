package net.wyrium.fullchest.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.wyrium.fullchest.item.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Generates vanilla crafting recipes for FullChest using the standard data generator.
 * <p>
 * Covers:
 * <ul>
 *   <li><b>Dirt Chest</b> (shaped recipe using the dirt tag + vanilla chest)</li>
 *   <li><b>Chest Forge</b> (polished blackstone, lava buckets, crafting table)</li>
 *   <li><b>Base Chest Upgrade</b> (planks + vanilla chest)</li>
 * </ul>
 */
public class FullChestRecipeProvider extends RecipeProvider {

    public FullChestRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    /**
     * Emits all vanilla-style shaped recipes used by the mod.
     * Custom forge recipes are generated separately via {@code ChestForgeJsonProvider}.
     */
    @Override
    protected void buildRecipes(@NotNull RecipeOutput out) {

        // --- Dirt Chest (vanilla crafting) ---
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DIRT_CHEST.get())
                .pattern("DDD")
                .pattern("DCD")
                .pattern("DDD")
                .define('D', ItemTags.DIRT)   // any dirt variant
                .define('C', Items.CHEST)    // vanilla chest
                .unlockedBy("has_dirt", has(ItemTags.DIRT))
                .save(out);

        // --- Chest Forge (workstation for custom chest forging) ---
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CHEST_FORGE.get())
                .pattern("PBP")
                .pattern("BCB")
                .pattern("PBP")
                .define('P', Items.POLISHED_BLACKSTONE)
                .define('B', Items.LAVA_BUCKET)
                .define('C', Items.CRAFTING_TABLE)
                .unlockedBy("has_lava_bucket", has(Items.LAVA_BUCKET))
                .save(out);

        // --- Base Chest Upgrade (entry upgrade item for the progression chain) ---
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BASE_CHEST_UPGRADE.get())
                .pattern("WWW")
                .pattern("WCW")
                .pattern("WWW")
                .define('W', ItemTags.PLANKS) // any wood planks
                .define('C', Items.CHEST)
                .unlockedBy("has_chest", has(Items.CHEST))
                .save(out);
    }
}
