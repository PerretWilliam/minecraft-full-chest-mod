package net.wyrium.fullchest;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wyrium.fullchest.item.ModItems;

import java.util.function.Supplier;

public class FullChestCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FullChest.MODID);

    public static final Supplier<CreativeModeTab> FULLCHEST_BLOCKS = CREATIVE_MODE_TAB.register("fullchest_blocks",
            () -> CreativeModeTab.builder()
                    // Icon of the tab
                    .icon(() -> new ItemStack(ModItems.DIRT_CHEST.get()))
                    // Title of the tab
                    .title(Component.translatable("creativetab." + FullChest.MODID + ".fullchest_blocks"))
                    // Put your item in the tab here...
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.DIRT_CHEST.get());
                        output.accept(ModItems.STONE_CHEST.get());
                        output.accept(ModItems.COPPER_CHEST.get());
                        output.accept(ModItems.IRON_CHEST.get());
                        output.accept(ModItems.GOLD_CHEST.get());
                        output.accept(ModItems.EMERALD_CHEST.get());
                        output.accept(ModItems.DIAMOND_CHEST.get());
                        output.accept(ModItems.OBSIDIAN_CHEST.get());
                        output.accept(ModItems.NETHERITE_CHEST.get());
            }).build());

    // Register method
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
