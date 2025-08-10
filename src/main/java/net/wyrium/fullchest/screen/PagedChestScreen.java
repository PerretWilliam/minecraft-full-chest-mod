    package net.wyrium.fullchest.screen;

    import com.mojang.blaze3d.systems.RenderSystem;
    import net.minecraft.client.gui.components.Button;
    import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
    import net.minecraft.client.gui.GuiGraphics;
    import net.minecraft.network.chat.Component;
    import net.minecraft.resources.ResourceLocation;
    import net.minecraft.world.entity.player.Inventory;
    import net.minecraft.world.inventory.Slot;
    import net.wyrium.fullchest.FullChest;
    import org.jetbrains.annotations.NotNull;

    public class PagedChestScreen extends AbstractContainerScreen<PagedChestMenu> {
        private static final ResourceLocation BG =
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/generic_54.png");
        private static final ResourceLocation DISABLED_SLOT =
                ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "textures/gui/slot_disabled.png");

        private Button prevBtn, nextBtn;
        private int lastTotal = -1, lastPage = -1, lastMax = -1;

        public PagedChestScreen(PagedChestMenu menu, Inventory inv, Component title) {
            super(menu, inv, title);
            this.imageWidth = 176;
            this.imageHeight = 222; // 6 rows vanilla
            this.inventoryLabelY = this.imageHeight - 94;
        }

        @Override
        protected void init() {
            super.init();
            int x = leftPos + 8, y = topPos + 6;

            prevBtn = addRenderableWidget(Button.builder(Component.literal("<"), b -> {
                if (minecraft != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
            }).pos(x + 75, y - 1).size(10, 10).build());

            nextBtn = addRenderableWidget(Button.builder(Component.literal(">"), b -> {
                if (minecraft != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 1);
            }).pos(x + 95, y - 1).size(10, 10).build());

            updateArrowsIfNeeded(); // premier état
        }

        @Override
        protected void containerTick() {
            super.containerTick();
            updateArrowsIfNeeded(); // check ultra cheap (3 int compares)
        }

        private void updateArrowsIfNeeded() {
            int total = menu.getData().get(1);           // 0 si pas encore sync
            int page  = menu.getPage();
            int max   = menu.getMaxPages();

            if (total == lastTotal && page == lastPage && max == lastMax) return; // rien à faire

            lastTotal = total; lastPage = page; lastMax = max;

            if (total <= 0 || max <= 1) { // pas d’info ou une seule page
                prevBtn.visible = false;
                nextBtn.visible = false;
                return;
            }

            prevBtn.visible = true;
            nextBtn.visible = true;

            prevBtn.active = page > 0;
            nextBtn.active = page < max - 1;
        }

        @Override
        public void render(@NotNull GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
            renderBackground(gfx, mouseX, mouseY, partialTick);
            super.render(gfx, mouseX, mouseY, partialTick);

            drawDisabledOverlays(gfx);
            renderTooltip(gfx, mouseX, mouseY);
        }

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

        @Override
        protected void renderBg(@NotNull GuiGraphics g, float partialTick, int mouseX, int mouseY) {
            RenderSystem.enableBlend();
            g.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
            RenderSystem.disableBlend();

            if(menu.getMaxPages() > 1) {
                String txt = "Page " + (menu.getPage() + 1) + "/" + menu.getMaxPages();
                g.drawString(this.font, txt, leftPos + imageWidth - 8 - this.font.width(txt), topPos + 6, 0x404040, false);
            }
        }
    }
