package net.wyrium.fullchest.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.wyrium.fullchest.FullChest;
import org.jetbrains.annotations.NotNull;

/**
 * Client-side screen for {@link ChestForgeMenu}, rendering the GUI background,
 * burn flame, and crafting progress arrow.
 * <p>
 * Textures:
 * <ul>
 *   <li>Main GUI background: 176×166</li>
 *   <li>Flame icon: 14×14 (animated vertically)</li>
 *   <li>Progress arrow: 24×16 (animated horizontally)</li>
 * </ul>
 * <p>
 * Animation is scaled using {@link ChestForgeMenu#getScaledFlame()} and
 * {@link ChestForgeMenu#getScaledProgress()}, based on values synced from the server.
 */
public class ChestForgeScreen extends AbstractContainerScreen<ChestForgeMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "textures/gui/forge_table_gui.png");

    // Separate PNGs for animated parts
    private static final ResourceLocation FLAME_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "textures/gui/lit_progress.png"); // 14×14
    private static final ResourceLocation ARROW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "textures/gui/burn_progress.png"); // 24×16

    // Positions relative to GUI top-left
    private static final int FLAME_X = 8,   FLAME_Y = 33;
    private static final int ARROW_X = 110, ARROW_Y = 33;

    /**
     * @param menu the container menu backing this screen
     * @param inv the player's inventory
     * @param title localized title component
     */
    public ChestForgeScreen(ChestForgeMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelY = 6;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    /**
     * Renders the background layer: GUI frame, burn flame, and progress arrow.
     */
    @Override
    protected void renderBg(@NotNull GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        // Prepare render pipeline
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Draw main background
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        g.blit(GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Draw animated flame (vertical shrink from top to bottom)
        int h = menu.getScaledFlame(); // 0..14
        if (h > 0) {
            g.blit(FLAME_TEXTURE,
                    x + FLAME_X,y + FLAME_Y + (14 - h), // Shift Y upward as height decreases
                    0,
                    (14 - h), // Source Y in texture
                    14, h, // Width, height drawn
                    14, 14 // Texture width/height
            );
        }

        // Draw animated progress arrow (horizontal fill)
        int w = menu.getScaledProgress(); // 0..24
        if (w > 0) {
            g.blit(ARROW_TEXTURE,
                    x + ARROW_X, y + ARROW_Y,
                    0, 0,
                    w, 16, // Width, height drawn
                    24, 16 // Texture width/height
            );
        }
    }

    /**
     * Full render pipeline: background, slots, tooltips.
     */
    @Override
    public void render(@NotNull GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        renderBackground(gfx, mouseX, mouseY, partialTick);
        super.render(gfx, mouseX, mouseY, partialTick);
        renderTooltip(gfx, mouseX, mouseY);
    }
}
