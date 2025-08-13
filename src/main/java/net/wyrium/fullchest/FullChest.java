package net.wyrium.fullchest;

import net.minecraft.resources.ResourceLocation;
import net.wyrium.fullchest.block.ModBlocks;
import net.wyrium.fullchest.block.entity.ModBlockEntities;
import net.wyrium.fullchest.item.ModItems;
import net.wyrium.fullchest.recipe.ModRecipeTypes;
import net.wyrium.fullchest.screen.ModMenuTypes;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

/**
 * Main entry point for the FullChest mod.
 * <p>
 * Wires up all registries (blocks, items, menus, recipes, BEs), config, and common setup.
 * This class also demonstrates how to subscribe to global game events (server start).
 */
@Mod(FullChest.MODID)
public class FullChest {

    /** Mod ID used across resources, registries, and namespace helpers. */
    public static final String MODID = "fullchest";

    /** Convenience helper for creating mod-scoped resource locations. */
    public static ResourceLocation rl(String path) { return ResourceLocation.fromNamespaceAndPath(MODID, path); }

    /** SLF4J logger for this mod. */
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Constructed by NeoForge when the mod loads.
     *
     * @param modEventBus  the mod-specific event bus (lifecycle + registry events)
     * @param modContainer metadata + utilities for this mod (e.g., config registration)
     */
    public FullChest(IEventBus modEventBus, ModContainer modContainer) {
        /* ===== Lifecycle hooks ===== */
        modEventBus.addListener(this::commonSetup);

        /* ===== Registry wiring ===== */
        // Creative tab must be registered before content to ensure proper ordering in some UIs
        FullChestCreativeTab.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipeTypes.register(modEventBus);

        /* ===== Global event bus subscriptions ===== */
        // Only required if THIS class has @SubscribeEvent handlers (see onServerStarting below).
        NeoForge.EVENT_BUS.register(this);

        /* ===== Creative tab population (vanilla tabs) ===== */
        modEventBus.addListener(this::addCreative);

        /* ===== Config registration ===== */
        // Creates/loads the mod's config file and binds it to the generated spec.
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    /* =========================
       Common setup (both sides)
       ========================= */

    /**
     * Runs during the common setup phase. Use for network setup, compat hooks, etc.
     */
    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());
        Config.ITEM_STRINGS.get().forEach(item -> LOGGER.info("ITEM >> {}", item));
    }

    /* =========================
       Creative tab injection
       ========================= */

    /**
     * Example of adding items into vanilla creative tabs.
     * Currently empty for {@link CreativeModeTabs#BUILDING_BLOCKS}; keep the hook for future use.
     */
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            // Intentionally left blank; populate if you want items in the vanilla Building Blocks tab.
        }
    }

    /* =========================
       Global game events
       ========================= */

    /**
     * Example server lifecycle hook.
     * This method is discovered via @SubscribeEvent because this class is registered to the global bus.
     */
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
