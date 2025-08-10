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
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.wyrium.fullchest.block.ModBlocks;
import net.wyrium.fullchest.block.entity.ModBlockEntities;
import net.wyrium.fullchest.screen.ModMenuTypes;
import net.wyrium.fullchest.screen.PagedChestScreen;
import net.wyrium.fullchest.template.BaseChestBlock;
import net.wyrium.fullchest.template.BaseChestBlockItemRenderer;
import net.wyrium.fullchest.template.BaseChestEntityRenderer;
import net.wyrium.fullchest.template.ChestSpecs;

import javax.annotation.Nonnull;
import java.util.IdentityHashMap;
import java.util.Map;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = FullChest.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = FullChest.MODID, value = Dist.CLIENT)
public class FullChestClient {
    public FullChestClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.BASE_CHEST_BE.get(), BaseChestEntityRenderer::new);
    }

    private static final Map<Item, BlockEntityWithoutLevelRenderer> CACHE = new IdentityHashMap<>();

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        FullChest.LOGGER.info("HELLO FROM CLIENT SETUP");
        FullChest.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.PAGED_CHEST.get(), PagedChestScreen::new);
    }

    @SubscribeEvent
    public static void registerChestItemRenderer(RegisterClientExtensionsEvent event) {
        // Iterate over actually-registered chest blocks/items
        ModBlocks.ALL_CHESTS.forEach(blockHolder -> {
            Item item = blockHolder.get().asItem();

            event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions() {
                @Override
                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    // Lazy-create & cache one renderer per item
                    return CACHE.computeIfAbsent(item, it -> {
                        var mc = Minecraft.getInstance();

                        // Read spec from the block behind this item
                        var block = ((BlockItem) it).getBlock();
                        var spec  = ((BaseChestBlock) block).spec();

                        return new BaseChestBlockItemRenderer(
                                mc.getBlockEntityRenderDispatcher(),
                                mc.getEntityModels(),
                                spec,
                                block
                        );
                    });
                }
            }, item);
        });
    }
}
