package net.wyrium.fullchest.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wyrium.fullchest.FullChest;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, FullChest.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, FullChest.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ChestForgeRecipe>> CHEST_FORGE_SERIALIZER =
            SERIALIZERS.register("chest_forge", ChestForgeRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<ChestForgeRecipe>> CHEST_FORGE_TYPE =
            TYPES.register("chest_forge", () -> new RecipeType<>() {
                @Override public String toString() { return FullChest.MODID + ":chest_forge"; }
            });

    public static void register(net.neoforged.bus.api.IEventBus bus) {
        SERIALIZERS.register(bus);
        TYPES.register(bus);
    }
}
