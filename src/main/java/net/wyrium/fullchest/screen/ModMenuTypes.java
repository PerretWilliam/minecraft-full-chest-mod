package net.wyrium.fullchest.screen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.block.entity.ChestForgeBlockEntity;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, FullChest.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ChestForgeMenu>> CHEST_FORGE =
            MENUS.register("chest_forge",
                    () -> IMenuTypeExtension.create((windowId, inv, buf) -> {
                        BlockPos pos = buf.readBlockPos();
                        var be = inv.player.level().getBlockEntity(pos);
                        if (!(be instanceof ChestForgeBlockEntity forge)) {
                            return null;
                        }
                        return new ChestForgeMenu(windowId, inv, forge, forge, forge.data());
                    }));

    public static final DeferredHolder<MenuType<?>, MenuType<PagedChestMenu>> PAGED_CHEST =
            MENUS.register("paged_chest",
                    () -> new MenuType<>(
                            (id, inv) -> new PagedChestMenu(id, inv, new SimpleContainer(PagedChestMenu.VISIBLE)),
                            FeatureFlags.VANILLA_SET
                    ));

    public static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    // Register Method
    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
