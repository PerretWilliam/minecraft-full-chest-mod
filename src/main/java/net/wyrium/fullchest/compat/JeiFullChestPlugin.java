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

@JeiPlugin
public class JeiFullChestPlugin implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ChestForgeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        RecipeManager recipeManager = null;
        if (Minecraft.getInstance().level != null) {
            recipeManager = Minecraft.getInstance().level.getRecipeManager();
        }

        List<ChestForgeRecipe> chestForgeRecipes = null;
        if (recipeManager != null) chestForgeRecipes = recipeManager.getAllRecipesFor(ModRecipeTypes.CHEST_FORGE_TYPE.get()).stream().map(RecipeHolder::value).toList();

        if (chestForgeRecipes != null) registration.addRecipes(ChestForgeRecipeCategory.CHEST_FORGE_RECIPE_RECIPE_TYPE, chestForgeRecipes);

    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ChestForgeScreen.class, 110, 33, 24, 16, ChestForgeRecipeCategory.CHEST_FORGE_RECIPE_RECIPE_TYPE);
    }
}
