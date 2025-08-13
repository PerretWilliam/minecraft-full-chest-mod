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

/**
 * Central registration point for all {@link MenuType} instances in the mod.
 * <p>
 * Provides:
 * <ul>
 *   <li>{@link #CHEST_FORGE}: a networked container bound to a {@link ChestForgeBlockEntity} at a BlockPos read from the buffer.</li>
 *   <li>{@link #PAGED_CHEST}: a lightweight container for paged chests, backed by a temporary {@link SimpleContainer} sized to visible slots.</li>
 *   <li>{@link #registerMenuType(String, IContainerFactory)}: helper for additional menus using NeoForge {@link IMenuTypeExtension} factories.</li>
 * </ul>
 * Call {@link #register(IEventBus)} during mod init to attach to the registry event bus.
 */
public class ModMenuTypes {
    /** Deferred register for menu types under this mod id. */
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, FullChest.MODID);

    /**
     * Chest Forge menu: server ↔ client sync via a BlockPos read from the network buffer.
     * <p>
     * Factory flow:
     * <ol>
     *   <li>Read the {@link BlockPos} from the buffer.</li>
     *   <li>Resolve the BE from the player's level.</li>
     *   <li>If it's a {@link ChestForgeBlockEntity}, construct {@link ChestForgeMenu} passing the BE and its {@code ContainerData}.</li>
     * </ol>
     * Returns {@code null} if the BE is missing or of the wrong type, which cancels opening.
     */
    public static final DeferredHolder<MenuType<?>, MenuType<ChestForgeMenu>> CHEST_FORGE =
            MENUS.register("chest_forge",
                    () -> IMenuTypeExtension.create((windowId, inv, buf) -> {
                        BlockPos pos = buf.readBlockPos();
                        var be = inv.player.level().getBlockEntity(pos);
                        if (!(be instanceof ChestForgeBlockEntity forge)) {
                            return null; // fail-safe: do not open if BE is wrong/missing
                        }
                        return new ChestForgeMenu(windowId, inv, forge, forge, forge.data());
                    }));

    /**
     * Paged Chest menu: client-only init path using a simple container sized to the number of visible slots.
     * Uses {@link FeatureFlags#VANILLA_SET} to remain compatible with vanilla features only.
     */
    public static final DeferredHolder<MenuType<?>, MenuType<PagedChestMenu>> PAGED_CHEST =
            MENUS.register("paged_chest",
                    () -> new MenuType<>(
                            (id, inv) -> new PagedChestMenu(id, inv, new SimpleContainer(PagedChestMenu.VISIBLE)),
                            FeatureFlags.VANILLA_SET
                    ));

    /**
     * Convenience method to register arbitrary menu types using an {@link IContainerFactory}.
     *
     * @param name    registry path (e.g., "my_menu")
     * @param factory container factory used both client and server side
     * @param <T>     menu class
     * @return a {@link DeferredHolder} for the registered {@link MenuType}
     */
    public static <T extends AbstractContainerMenu>
    DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    /**
     * Attaches this mod's menu registry to the event bus. Invoke during common setup.
     */
    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }

    /** Utility class; prevent instantiation. */
    private ModMenuTypes() {}
}
