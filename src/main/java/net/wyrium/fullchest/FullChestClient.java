package net.wyrium.fullchest;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.wyrium.fullchest.block.ModBlocks;
import net.wyrium.fullchest.block.entity.ModBlockEntities;
import net.wyrium.fullchest.screen.ChestForgeScreen;
import net.wyrium.fullchest.screen.ModMenuTypes;
import net.wyrium.fullchest.screen.PagedChestScreen;
import net.wyrium.fullchest.template.BaseChestBlock;
import net.wyrium.fullchest.template.BaseChestBlockItemRenderer;
import net.wyrium.fullchest.template.BaseChestEntityRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Client-only bootstrap and registrations for the FullChest mod.
 * <p>
 * Safe to reference client classes here since this class is never loaded on a dedicated server.
 */
@Mod(value = FullChest.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = FullChest.MODID, value = Dist.CLIENT)
public class FullChestClient {

    /**
     * Registers a generic config screen for this mod in the Mods UI.
     * NeoForge will call this constructor on the client distribution.
     */
    public FullChestClient(ModContainer container) {
        // Mods screen → select this mod → "Config" button
        // Make sure to provide proper translations in en_us.json for your config keys.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    /* =========================
       Block Entity Renderers
       ========================= */

    /**
     * Registers the BlockEntity renderer used by all custom chest variants.
     */
    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.BASE_CHEST_BE.get(), BaseChestEntityRenderer::new);
    }

    /* =========================
       Client Setup (lifecycle)
       ========================= */

    /**
     * General client setup hook (logging, keybinds, misc init).
     */
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        FullChest.LOGGER.info("HELLO FROM CLIENT SETUP");
        FullChest.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    /* =========================
       Menus / Screens
       ========================= */

    /**
     * Connects menu types to their client-side screens.
     */
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.PAGED_CHEST.get(), PagedChestScreen::new);
        event.register(ModMenuTypes.CHEST_FORGE.get(), ChestForgeScreen::new);
    }

    /* =========================
       Item renderers for chest items
       ========================= */

    /** Per-item cache to avoid re-creating renderers. Identity map preserves item identity semantics. */
    private static final Map<Item, BlockEntityWithoutLevelRenderer> CACHE = new IdentityHashMap<>();

    /**
     * Registers a custom {@link BlockEntityWithoutLevelRenderer} for each chest item so that
     * the item renders as a full 3D chest (using the same model/animation as its block entity).
     */
    @SubscribeEvent
    public static void registerChestItemRenderer(RegisterClientExtensionsEvent event) {
        // Iterate over actually-registered chest blocks/items
        ModBlocks.ALL_CHESTS.forEach(blockHolder -> {
            Item item = blockHolder.get().asItem();

            event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions() {
                @NotNull
                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    // Lazy-create & cache one renderer per item to reduce allocations
                    return CACHE.computeIfAbsent(item, it -> {
                        var mc = Minecraft.getInstance();

                        // Retrieve the ChestSpec from the underlying block
                        var block = ((BlockItem) it).getBlock();
                        var spec  = ((BaseChestBlock) block).spec();

                        // Renderer will render the item by delegating to a fake chest BE
                        return new BaseChestBlockItemRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels(), spec, block);
                    });
                }
            }, item);
        });
    }
}
