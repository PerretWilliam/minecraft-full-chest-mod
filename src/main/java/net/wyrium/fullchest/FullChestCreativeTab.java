package net.wyrium.fullchest;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wyrium.fullchest.block.ModBlocks;
import net.wyrium.fullchest.item.ModItems;
import net.wyrium.fullchest.template.ChestUpgradeItem;

import java.util.function.Supplier;

/**
 * Registers the custom Creative Mode tab for the FullChest mod.
 * <p>
 * This tab contains all chests, the Chest Forge, and all upgrade items,
 * grouped together for easier access in creative mode.
 */
public class FullChestCreativeTab {

    // Deferred register for creative tabs
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FullChest.MODID);

    /**
     * Main creative tab containing all FullChest blocks and upgrade items.
     */
    public static final Supplier<CreativeModeTab> FULLCHEST_BLOCKS = CREATIVE_MODE_TAB.register("fullchest_creative_tab",
            () -> CreativeModeTab.builder()
                    // Tab icon
                    .icon(() -> new ItemStack(ModItems.DIRT_CHEST.get()))
                    // Tab title (localized via lang file)
                    .title(Component.translatable("creativetab." + FullChest.MODID + ".creative_tab"))
                    // Items displayed in the tab
                    .displayItems((parameters, output) -> {
                        // Chest Forge + chests by tier
                        output.accept(ModItems.CHEST_FORGE.get());
                        output.accept(ModItems.DIRT_CHEST.get());
                        output.accept(ModItems.STONE_CHEST.get());
                        output.accept(ModItems.COPPER_CHEST.get());
                        output.accept(ModItems.IRON_CHEST.get());
                        output.accept(ModItems.GOLD_CHEST.get());
                        output.accept(ModItems.EMERALD_CHEST.get());
                        output.accept(ModItems.DIAMOND_CHEST.get());
                        output.accept(ModItems.OBSIDIAN_CHEST.get());
                        output.accept(ModItems.NETHERITE_CHEST.get());

                        // Base upgrade item
                        output.accept(ModItems.BASE_CHEST_UPGRADE.get());

                        // All upgrade items (dynamic loop)
                        ModItems.ALL_UPGRADES.forEach(itemHolder -> {
                            if (itemHolder.get() instanceof ChestUpgradeItem) {
                                output.accept(itemHolder.get());
                            }
                        });
                    })
                    .build());

    /**
     * Registers the creative tab with the mod event bus.
     */
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
