package net.wyrium.fullchest.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.screen.slot.PagedSlot;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Client screen for {@link PagedChestMenu}. Renders a vanilla-like 6×9 container
 * with previous/next page controls and disabled-slot overlays for slots that are
 * not active on the current page.
 * <p>
 * UI elements:
 * <ul>
 *   <li>Background: vanilla generic_54.png (176×222)</li>
 *   <li>Overlay for inactive chest slots (18×18)</li>
 *   <li>Page indicator (top-right) when multiple pages exist</li>
 *   <li>&lt; / &gt; buttons wired to {@link PagedChestMenu#clickMenuButton} ids 0/1</li>
 * </ul>
 */
public class PagedChestScreen extends AbstractContainerScreen<PagedChestMenu> {
    private static final ResourceLocation BG =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/generic_54.png");
    private static final ResourceLocation DISABLED_SLOT =
            ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "textures/gui/slot_disabled.png");

    /** Page navigation buttons (created in {@link #init()}). */
    private Button prevBtn, nextBtn;

    /** Cache last-seen sync values to avoid redundant UI updates each tick. */
    private int lastTotal = -1, lastPage = -1, lastMax = -1;

    /**
     * @param menu backing paged menu (server-synced)
     * @param inv player inventory
     * @param title localized title
     */
    public PagedChestScreen(PagedChestMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 222; // 6 rows vanilla container
        this.inventoryLabelY = this.imageHeight - 94;
    }

    /**
     * Builds navigation buttons and applies the initial enable/visibility state.
     * Uses {@code gameMode.handleInventoryButtonClick(containerId, id)} to send
     * button actions to the server (id 0 = prev, id 1 = next).
     */
    @Override
    protected void init() {
        super.init();
        int x = leftPos + 8, y = topPos + 6;

        prevBtn = addRenderableWidget(Button.builder(Component.literal("<"), b -> {
            if (minecraft != null) Objects.requireNonNull(minecraft.gameMode)
                    .handleInventoryButtonClick(menu.containerId, 0);
        }).pos(x + 75, y - 1).size(10, 10).build());

        nextBtn = addRenderableWidget(Button.builder(Component.literal(">"), b -> {
            if (minecraft != null) Objects.requireNonNull(minecraft.gameMode)
                    .handleInventoryButtonClick(menu.containerId, 1);
        }).pos(x + 95, y - 1).size(10, 10).build());

        updateArrowsIfNeeded(); // initial state
    }

    /**
     * Periodically reconciles button visibility/enablement against server-synced data.
     * The check is very cheap (three int comparisons).
     */
    @Override
    protected void containerTick() {
        super.containerTick();
        updateArrowsIfNeeded();
    }

    /**
     * Shows/hides and enables/disables arrows depending on total slots, page index,
     * and max page count. Avoids UI churn by caching last-seen values.
     */
    private void updateArrowsIfNeeded() {
        int total = menu.getData().get(1); // may be 0 before sync
        int page  = menu.getPage();
        int max   = menu.getMaxPages();

        if (total == lastTotal && page == lastPage && max == lastMax) return;

        lastTotal = total; lastPage = page; lastMax = max;

        if (total <= 0 || max <= 1) {
            // No info yet or single page only: hide arrows
            prevBtn.visible = false;
            nextBtn.visible = false;
            return;
        }

        prevBtn.visible = true;
        nextBtn.visible = true;

        prevBtn.active = page > 0;
        nextBtn.active = page < max - 1;
    }

    /**
     * Full render pipeline: background, slots, disabled overlays, tooltips.
     */
    @Override
    public void render(@NotNull GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        renderBackground(gfx, mouseX, mouseY, partialTick);
        super.render(gfx, mouseX, mouseY, partialTick);

        drawDisabledOverlays(gfx);
        renderTooltip(gfx, mouseX, mouseY);
    }

    /**
     * Draws a semi-opaque overlay over any slot in the visible 54 that is marked inactive
     * by {@link PagedSlot} (e.g., when the final page is partially filled).
     */
    private void drawDisabledOverlays(GuiGraphics gfx) {
        for (int i = 0; i < PagedChestMenu.VISIBLE; i++) {
            Slot slot = this.menu.slots.get(i);
            if (!slot.isActive()) {
                int sx = this.leftPos + slot.x;
                int sy = this.topPos  + slot.y;
                gfx.blit(DISABLED_SLOT, sx - 1, sy - 1, 0, 0, 18, 18, 18, 18);
            }
        }
    }

    /**
     * Renders the vanilla background and a right-aligned "Page X/Y" label when multiple pages exist.
     */
    @Override
    protected void renderBg(@NotNull GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        g.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        RenderSystem.disableBlend();

        if (menu.getMaxPages() > 1) {
            String txt = "Page " + (menu.getPage() + 1) + "/" + menu.getMaxPages();
            g.drawString(this.font, txt,
                    leftPos + imageWidth - 8 - this.font.width(txt),
                    topPos + 6,
                    0x404040, false);
        }
    }

    /**
     * Plays a chest-close sound on closing the screen for better UX feedback.
     */
    @Override
    public void onClose() {
        super.onClose();
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.playSound(SoundEvents.CHEST_CLOSE, 1.0F, 1.0F);
        }
    }
}
