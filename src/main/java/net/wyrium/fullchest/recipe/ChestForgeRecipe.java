package net.wyrium.fullchest.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ChestForgeRecipe implements Recipe<CraftingInput> {

    private final int width;
    private final int height;
    private final NonNullList<Ingredient> ingredients; // size = width * height
    private final ItemStack result;
    private final int time;      // ticks
    private final boolean mirror;

    public ChestForgeRecipe(int width, int height,
                            NonNullList<Ingredient> ingredients,
                            ItemStack result, int time, boolean mirror) {
        this.width = width;
        this.height = height;
        this.ingredients = ingredients;
        this.result = result.copy();
        this.time = Math.max(1, time);
        this.mirror = mirror;
    }

    public int time() { return time; }
    public ItemStack output() { return result.copy(); }
    public int width() { return width; }
    public int height() { return height; }
    public NonNullList<Ingredient> ingredients() { return ingredients; }

    @Override
    public boolean matches(@NotNull CraftingInput input, Level level) {
        if (level.isClientSide) return false;

        if (input.width() != width || input.height() != height) return false;

        // Test motif direct
        if (checkMatch(input, false)) return true;
        return mirror && checkMatch(input, true);
    }

    private boolean checkMatch(CraftingInput input, boolean mirrored) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rx = mirrored ? (width - 1 - x) : x;
                Ingredient need = ingredients.get(rx + y * width);
                ItemStack got = input.getItem(x + y * input.width());
                if (!need.test(got)) return false;
            }
        }
        return true;
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull CraftingInput input, @NotNull HolderLookup.Provider registries) {
        return result.copy();
    }

    @NotNull
    @Override
    public ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override public boolean canCraftInDimensions(int w, int h) { return w >= width && h >= height; }

    @NotNull @Override
    public NonNullList<Ingredient> getIngredients() { return ingredients; }

    @NotNull @Override public RecipeSerializer<?> getSerializer() { return ModRecipeTypes.CHEST_FORGE_SERIALIZER.get(); }
    @NotNull @Override public RecipeType<?> getType() { return ModRecipeTypes.CHEST_FORGE_TYPE.get(); }
    @Override public boolean isSpecial() { return true; }

    /* ===== Serializer ===== */

    public static class Serializer implements RecipeSerializer<ChestForgeRecipe> {

        public static final MapCodec<ChestForgeRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.STRING.listOf().fieldOf("pattern").forGetter(r -> List.of()), // lecture seule
                Codec.unboundedMap(Codec.STRING, Ingredient.CODEC).fieldOf("key").forGetter(r -> Map.of()),
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result),
                Codec.INT.optionalFieldOf("time", 200).forGetter(r -> r.time),
                Codec.BOOL.optionalFieldOf("mirror", true).forGetter(r -> r.mirror)
        ).apply(inst, (patternList, keyMap, result, time, mirror) -> {
            Parsed p = parsePatternAndKey(patternList, keyMap);
            return new ChestForgeRecipe(p.width, p.height, p.ingredients, result, time, mirror);
        }));

        public static final StreamCodec<RegistryFriendlyByteBuf, ChestForgeRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.VAR_INT, ChestForgeRecipe::width,
                        ByteBufCodecs.VAR_INT, ChestForgeRecipe::height,

                        // LISTE D’INGRÉDIENTS (codec + getter manquant AVANT)
                        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                        (ChestForgeRecipe r) -> r.ingredients(),

                        // RESULT
                        ItemStack.STREAM_CODEC, r -> r.result,

                        // TIME
                        ByteBufCodecs.VAR_INT, ChestForgeRecipe::time,

                        // MIRROR
                        ByteBufCodecs.BOOL, r -> r.mirror,

                        // CONSTRUCTEUR
                        (Integer w, Integer h, java.util.List<Ingredient> list, ItemStack res, Integer t, Boolean m) -> {
                            NonNullList<Ingredient> ings = NonNullList.withSize(w * h, Ingredient.EMPTY);
                            for (int i = 0; i < Math.min(ings.size(), list.size()); i++) {
                                ings.set(i, list.get(i));
                            }
                            return new ChestForgeRecipe(w, h, ings, res, t, m);
                        }
                );


        @Override public MapCodec<ChestForgeRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, ChestForgeRecipe> streamCodec() { return STREAM_CODEC; }

        /* -------- util -------- */

        private static record Parsed(int width, int height, NonNullList<Ingredient> ingredients) {}

        private static Parsed parsePatternAndKey(List<String> pattern, Map<String, Ingredient> key) {
            pattern = shrink(pattern);
            int h = pattern.size();
            int w = h == 0 ? 0 : pattern.get(0).length();

            // dictionnaire: 'A' -> ingredient
            Map<Character, Ingredient> dict = new HashMap<>();
            dict.put(' ', Ingredient.EMPTY);
            for (var e : key.entrySet()) {
                String k = e.getKey();
                if (k.length() != 1) throw new IllegalArgumentException("Key must be 1 char: " + k);
                dict.put(k.charAt(0), e.getValue());
            }

            NonNullList<Ingredient> list = NonNullList.withSize(w * h, Ingredient.EMPTY);
            for (int y = 0; y < h; y++) {
                String row = pattern.get(y);
                if (row.length() != w) throw new IllegalArgumentException("Inconsistent row width");
                for (int x = 0; x < w; x++) {
                    char c = row.charAt(x);
                    list.set(x + y * w, dict.getOrDefault(c, Ingredient.EMPTY));
                }
            }
            return new Parsed(w, h, list);
        }

        private static List<String> shrink(List<String> raw) {
            if (raw.isEmpty()) return raw;

            int top = 0;
            while (top < raw.size() && raw.get(top).trim().isEmpty()) top++;
            int bottom = raw.size() - 1;
            while (bottom >= top && raw.get(bottom).trim().isEmpty()) bottom--;

            if (top > bottom) return List.of();

            int left = Integer.MAX_VALUE, right = -1;
            for (int i = top; i <= bottom; i++) {
                String s = raw.get(i);
                int first = firstNonSpace(s);
                int last = lastNonSpace(s);
                left = Math.min(left, first);
                right = Math.max(right, last);
            }
            if (left > right) return List.of();

            ArrayList<String> out = new ArrayList<>();
            for (int i = top; i <= bottom; i++) {
                out.add(raw.get(i).substring(left, right + 1));
            }
            return out;
        }

        private static int firstNonSpace(String s) {
            for (int i = 0; i < s.length(); i++) if (s.charAt(i) != ' ') return i;
            return s.length();
        }
        private static int lastNonSpace(String s) {
            for (int i = s.length() - 1; i >= 0; i--) if (s.charAt(i) != ' ') return i;
            return -1;
        }
    }
}
