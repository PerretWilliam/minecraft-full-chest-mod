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

/**
 * JEI category for the Chest Forge.
 * <p>
 * This category mirrors the in-game screen layout (coords, textures, slot positions),
 * so what players see in JEI matches the real GUI. The background is a cropped slice
 * of your forge GUI, plus we draw the animated-looking icons (flame & arrow) at the
 * exact same coordinates used by {@code ChestForgeScreen}.
 */
public class ChestForgeRecipeCategory implements IRecipeCategory<ChestForgeRecipe> {

    /* -----------------------------
     *  IDs & Texture resources
     * ----------------------------- */

    /** Unique JEI category id: fullchest:chest_forge */
    public static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "chest_forge");

    /** Cropped background used by JEI (top part of your forge GUI). */
    private static final ResourceLocation GUI =
            ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "textures/jei/gui/jei_forge_table_gui.png");

    /** 14×14 standalone texture for the flame indicator. */
    private static final ResourceLocation FLAME_T =
            ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "textures/gui/lit_progress.png"); // 14x14

    /** 24×16 standalone texture for the progress arrow. */
    private static final ResourceLocation ARROW_T =
            ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "textures/gui/burn_progress.png"); // 24x16

    /**
     * JEI recipe type for this category. (Separate from Minecraft's RecipeType)
     * Expose this constant if you need to reference it from your plugin/registrations.
     */
    public static final RecipeType<ChestForgeRecipe> CHEST_FORGE_RECIPE_RECIPE_TYPE =
            new RecipeType<>(UID, ChestForgeRecipe.class);

    /* -----------------------------
     *  Background crop dimensions
     * ----------------------------- */

    /**
     * Background size supplied to JEI. We reuse the top 176x85 area of your GUI,
     * which contains all slots and decorative icons.
     */
    private static final int BG_W = 176, BG_H = 85;

    /* -----------------------------
     *  Slot & Icon coordinates
     *  (kept in sync with ChestForgeScreen/ChestForgeMenu)
     * ----------------------------- */

    /** Top-left of the 3x3 input grid. Each slot is spaced by STEP (18 px). */
    private static final int GRID_X = 50;  // input grid: x = 50 + c*18
    private static final int GRID_Y = 15;  // input grid: y = 15 + r*18
    private static final int STEP   = 18;

    /** Fuel slot (lava bucket), same position as the screen. */
    private static final int FUEL_X = 26;
    private static final int FUEL_Y = 33;

    /** Output slot position, same as the screen. */
    private static final int OUT_X  = 144;
    private static final int OUT_Y  = 33;

    /** Decorative flame icon (14x14) position. */
    private static final int FLAME_X = 8;
    private static final int FLAME_Y = 33;

    /** Decorative arrow icon (24x16) position. */
    private static final int ARROW_X = 110;
    private static final int ARROW_Y = 33;

    /* -----------------------------
     *  JEI drawables
     * ----------------------------- */

    /** Background cropped out of your GUI texture. */
    private final IDrawable background;

    /** Small icon shown in the JEI category tab (uses your forge block item). */
    private final IDrawable icon;

    /** Small 14×14 flame drawable (explicit texture size avoids UV sampling issues). */
    private final IDrawableStatic flame;

    /** Small 24×16 arrow drawable (explicit texture size avoids UV sampling issues). */
    private final IDrawableStatic arrow;

    public ChestForgeRecipeCategory(IGuiHelper guiHelper) {
        // Background: use the top part of your GUI (0,0)-(176x85)
        this.background = guiHelper.createDrawable(GUI, 0, 0, BG_W, BG_H);

        // Category icon: the forge block item (JEI shows this on the left)
        this.icon = guiHelper.createDrawableIngredient(
                VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CHEST_FORGE.get())
        );

        // Tiny standalone textures must declare their real texture size,
        // otherwise JEI may sample the wrong UVs and they won't render.
        this.flame = guiHelper.drawableBuilder(FLAME_T, 0, 0, 14, 14)
                .setTextureSize(14, 14)
                .build();
        this.arrow = guiHelper.drawableBuilder(ARROW_T, 0, 0, 24, 16)
                .setTextureSize(24, 16)
                .build();
    }

    @Override
    public @NotNull RecipeType<ChestForgeRecipe> getRecipeType() {
        return CHEST_FORGE_RECIPE_RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        // Localized title; you can swap this for a dedicated JEI key if you prefer
        return Component.translatable("block." + FullChest.MODID + ".chest_forge");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public @Nullable IDrawable getBackground() {
        // JEI uses this for the base backdrop before we draw icons
        return background;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder,
                          @NotNull ChestForgeRecipe recipe,
                          @NotNull IFocusGroup focuses) {
        // Place shaped ingredients exactly over the 3x3 grid area

        int w = recipe.width();
        int h = recipe.height();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int idx = x + y * w;
                builder.addSlot(RecipeIngredientRole.INPUT,
                                GRID_X + x * STEP,
                                GRID_Y + y * STEP)
                        .addIngredients(recipe.ingredients().get(idx));
            }
        }

        // Fuel: always show the lava bucket input, matching server logic
        builder.addSlot(RecipeIngredientRole.INPUT, FUEL_X, FUEL_Y)
                .addItemStack(new ItemStack(Items.LAVA_BUCKET));

        // Output: single result stack placed over the output slot
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUT_X, OUT_Y)
                .addItemStack(recipe.output());
    }

    @Override
    public void draw(@NotNull ChestForgeRecipe recipe,
                     @NotNull IRecipeSlotsView recipeSlotsView,
                     @NotNull GuiGraphics guiGraphics,
                     double mouseX, double mouseY) {
        // Draw order matters: background first, then decorative icons
        background.draw(guiGraphics);
        flame.draw(guiGraphics, FLAME_X, FLAME_Y);
        arrow.draw(guiGraphics, ARROW_X, ARROW_Y);

        // Optional: draw the craft time if you want a tiny label
        // guiGraphics.drawString(Minecraft.getInstance().font, recipe.time() + " t", ARROW_X, ARROW_Y - 10, 0x404040, false);
    }
}
