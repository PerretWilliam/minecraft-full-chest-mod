package net.wyrium.fullchest.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.block.ModBlocks;
import net.wyrium.fullchest.recipe.ChestForgeRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChestForgeRecipeCategory implements IRecipeCategory<ChestForgeRecipe> {

    // IDs & textures
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "chest_forge");
    private static final ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "textures/jei/gui/jei_forge_table_gui.png");
    private static final ResourceLocation FLAME_T = ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "textures/gui/lit_progress.png"); // 14x14
    private static final ResourceLocation ARROW_T = ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "textures/gui/burn_progress.png"); // 24x16

    // JEI recipe type (à exporter si tu veux le réutiliser ailleurs)
    public static final RecipeType<ChestForgeRecipe> CHEST_FORGE_RECIPE_RECIPE_TYPE =
            new RecipeType<>(UID, ChestForgeRecipe.class);

    // Background JEI : on recadre la moitié supérieure de ton GUI (176x85) qui contient tout (slots + icônes)
    private static final int BG_W = 176, BG_H = 85;

    // Coordonnées EXACTES reprises de ton écran (ChestForgeScreen & ChestForgeMenu)
    private static final int GRID_X = 50;  // grille 3x3 : x = 50 + c*18
    private static final int GRID_Y = 15;  // grille 3x3 : y = 15 + r*18
    private static final int STEP   = 18;

    private static final int FUEL_X = 26;  // slot fuel (lava bucket)
    private static final int FUEL_Y = 33;

    private static final int OUT_X  = 144; // slot sortie
    private static final int OUT_Y  = 33;

    private static final int FLAME_X = 8;   // icône flamme (14x14)
    private static final int FLAME_Y = 33;

    private static final int ARROW_X = 110; // icône flèche (24x16)
    private static final int ARROW_Y = 33;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic flame;
    private final IDrawableStatic arrow;

    public ChestForgeRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(GUI, 0, 0, BG_W, BG_H);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CHEST_FORGE.get()));
        this.flame  = guiHelper.drawableBuilder(FLAME_T, 0, 0, 14, 14).setTextureSize(14,14).build();
        this.arrow = guiHelper.drawableBuilder(ARROW_T, 0, 0, 24, 16).setTextureSize(24,16).build();
    }

    @Override
    public @NotNull RecipeType<ChestForgeRecipe> getRecipeType() {
        return CHEST_FORGE_RECIPE_RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("block." + FullChest.MODID + ".chest_forge");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull ChestForgeRecipe recipe, @NotNull IFocusGroup focuses) {
        // Place les ingrédients du motif "shaped" aux positions de ta grille 3x3
        int w = recipe.width();
        int h = recipe.height();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int idx = x + y * w;
                builder.addSlot(RecipeIngredientRole.INPUT, GRID_X + x * STEP, GRID_Y + y * STEP)
                        .addIngredients(recipe.ingredients().get(idx));
            }
        }

        // Fuel (on montre le seau de lave ; optionnel: ajouter le seau vide si tu veux visualiser le reste)
        builder.addSlot(RecipeIngredientRole.INPUT, FUEL_X, FUEL_Y)
                .addItemStack(new ItemStack(Items.LAVA_BUCKET));

        // Sortie
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUT_X, OUT_Y)
                .addItemStack(recipe.output());
    }

    @Override
    public void draw(@NotNull ChestForgeRecipe recipe,
                     @NotNull IRecipeSlotsView recipeSlotsView,
                     @NotNull GuiGraphics guiGraphics,
                     double mouseX, double mouseY) {
        // JEI dessine déjà le background via getBackground(), on ajoute juste la déco
        background.draw(guiGraphics);
        flame.draw(guiGraphics, FLAME_X, FLAME_Y);
        arrow.draw(guiGraphics, ARROW_X, ARROW_Y);

        // (Optionnel) petit texte de durée si tu veux
        // gg.drawString(Minecraft.getInstance().font, recipe.time() + " t", ARROW_X, ARROW_Y - 10, 0x404040, false);
    }
}
