package net.wyrium.fullchest.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ChestForgeScreen extends AbstractContainerScreen<ChestForgeMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("fullchest","textures/gui/forge_table_gui.png");

    // PNG séparés (dimensions réelles)
    private static final ResourceLocation FLAME_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("fullchest","textures/gui/lit_progress.png");   // 14x14
    private static final ResourceLocation ARROW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("fullchest","textures/gui/burn_progress.png");  // 24x16

    private static final int FLAME_X = 8, FLAME_Y = 33;
    private static final int ARROW_X = 110, ARROW_Y = 33;

    private static boolean LOG_ONCE = false;


    public ChestForgeScreen(ChestForgeMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelY = 6;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        // pipeline comme l’exemple
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // fond
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        g.blit(GUI_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // --- LOG CLIENT une fois pour vérifier les valeurs reçues ---
        if (!LOG_ONCE) {
            net.wyrium.fullchest.FullChest.LOGGER.info("[Screen] burn={} / {}, prog={} / {}",
                    menu.burnTime(), menu.burnTimeTotal(), menu.progress(), menu.maxProgress());
            LOG_ONCE = true;
        }

        int h = menu.getScaledFlame();     // 0..14
        if (h > 0) {
            g.blit(FLAME_TEXTURE, x + FLAME_X, y + FLAME_Y + (14 - h),
                    0, (14 - h), 14, h, 14, 14);
        }

        int w = menu.getScaledProgress();  // 0..24
        if (w > 0) {
            g.blit(ARROW_TEXTURE, x + ARROW_X, y + ARROW_Y,
                    0, 0, w, 16, 24, 16);
        }
    }



    @Override
    public void render(@NotNull GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        renderBackground(gfx, mouseX, mouseY, partialTick);
        super.render(gfx, mouseX, mouseY, partialTick);
        renderTooltip(gfx, mouseX, mouseY);
    }
}
