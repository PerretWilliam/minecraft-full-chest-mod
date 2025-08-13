package net.wyrium.fullchest.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wyrium.fullchest.FullChest;

/**
 * Central registration for the Chest Forge recipe type and serializer.
 * <p>
 * Provides:
 * <ul>
 *   <li>{@link #CHEST_FORGE_SERIALIZER}: codec/stream codec for {@link ChestForgeRecipe} JSON/network IO.</li>
 *   <li>{@link #CHEST_FORGE_TYPE}: logical recipe type identifier used by lookups and managers.</li>
 * </ul>
 * Call {@link #register(net.neoforged.bus.api.IEventBus)} during mod init to attach both registries.
 */
public class ModRecipeTypes {
    /** Deferred register for all {@link RecipeSerializer} under this mod id. */
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, FullChest.MODID);

    /** Deferred register for all {@link RecipeType} under this mod id. */
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, FullChest.MODID);

    /** Serializer for {@link ChestForgeRecipe} (JSON + network). */
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ChestForgeRecipe>> CHEST_FORGE_SERIALIZER =
            SERIALIZERS.register("chest_forge", ChestForgeRecipe.Serializer::new);

    /** Recipe type token for {@code fullchest:chest_forge}. */
    public static final DeferredHolder<RecipeType<?>, RecipeType<ChestForgeRecipe>> CHEST_FORGE_TYPE =
            TYPES.register("chest_forge", () -> new RecipeType<>() {
                @Override public String toString() { return FullChest.MODID + ":chest_forge"; }
            });

    /**
     * Wires the serializers and types to the mod event bus.
     * Invoke from your common setup/init.
     */
    public static void register(net.neoforged.bus.api.IEventBus bus) {
        SERIALIZERS.register(bus);
        TYPES.register(bus);
    }

    /** Utility class; no instances. */
    private ModRecipeTypes() {}
}
