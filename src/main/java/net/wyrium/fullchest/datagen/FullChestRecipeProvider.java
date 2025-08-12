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

public class FullChestRecipeProvider extends RecipeProvider {


    public FullChestRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput out) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DIRT_CHEST.get()) // Chest Dirt
                .pattern("DDD")
                .pattern("DCD")
                .pattern("DDD")
                .define('D', ItemTags.DIRT)
                .define('C', Items.CHEST)
                .unlockedBy("has_dirt", has(ItemTags.DIRT))
                .save(out);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CHEST_FORGE.get()) // Forge
                .pattern("PBP")
                .pattern("BCB")
                .pattern("PBP")
                .define('P', Items.POLISHED_BLACKSTONE)
                .define('B', Items.LAVA_BUCKET)
                .define('C', Items.CRAFTING_TABLE)
                .unlockedBy("has_lava_bucket", has(Items.LAVA_BUCKET))
                .save(out);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BASE_CHEST_UPGRADE.get()) // Base Chest Upgrade
                .pattern("WWW")
                .pattern("WCW")
                .pattern("WWW")
                .define('W', ItemTags.PLANKS)
                .define('C', Items.CHEST)
                .unlockedBy("has_chest", has(Items.CHEST))
                .save(out);
    }
}
