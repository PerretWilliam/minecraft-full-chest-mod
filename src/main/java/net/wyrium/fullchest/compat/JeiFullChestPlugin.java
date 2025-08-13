package net.wyrium.fullchest.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.recipe.ChestForgeRecipe;
import net.wyrium.fullchest.recipe.ModRecipeTypes;
import net.wyrium.fullchest.screen.ChestForgeScreen;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * JEI integration plugin for the FullChest mod.
 * <p>
 * This class registers:
 * <ul>
 *   <li>The custom recipe category (Chest Forge)</li>
 *   <li>All Chest Forge recipes loaded by the game</li>
 *   <li>The clickable area in the Chest Forge screen that opens JEI</li>
 * </ul>
 */
@JeiPlugin
public class JeiFullChestPlugin implements IModPlugin {

    /**
     * Unique ID for this JEI plugin.
     * Used internally by JEI to differentiate plugins from multiple mods.
     */
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "jei_plugin");
    }

    /**
     * Registers the recipe categories for this plugin.
     * <p>
     * Here we create and add our custom Chest Forge category so JEI knows how to display it.
     */
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new ChestForgeRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    /**
     * Registers all recipes that should be displayed in JEI for our categories.
     * <p>
     * We fetch the {@link ChestForgeRecipe} list from the server's RecipeManager
     * (only if the Minecraft client has a world loaded) and pass it to JEI.
     */
    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        RecipeManager recipeManager = null;

        // Get the recipe manager from the current world (null if not in a world)
        if (Minecraft.getInstance().level != null) {
            recipeManager = Minecraft.getInstance().level.getRecipeManager();
        }

        // Fetch all recipes for our Chest Forge recipe type
        List<ChestForgeRecipe> chestForgeRecipes = null;
        if (recipeManager != null) {
            chestForgeRecipes = recipeManager
                    .getAllRecipesFor(ModRecipeTypes.CHEST_FORGE_TYPE.get())
                    .stream()
                    .map(RecipeHolder::value) // Extract the actual recipe from the holder
                    .toList();
        }

        // Add them to JEI if any were found
        if (chestForgeRecipes != null) {
            registration.addRecipes(
                    ChestForgeRecipeCategory.CHEST_FORGE_RECIPE_RECIPE_TYPE,
                    chestForgeRecipes
            );
        }
    }

    /**
     * Registers clickable areas in custom GUIs that open JEI recipes.
     * <p>
     * Here we link the arrow area in {@link ChestForgeScreen} so clicking it in-game
     * will open the JEI view for Chest Forge recipes.
     *
     * @param registration JEI GUI handler registration object
     */
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(
                ChestForgeScreen.class, // Target GUI class
                110, 33,                // X, Y position of the clickable area
                24, 16,                 // Width & height of the clickable area
                ChestForgeRecipeCategory.CHEST_FORGE_RECIPE_RECIPE_TYPE // Recipes to display
        );
    }
}
