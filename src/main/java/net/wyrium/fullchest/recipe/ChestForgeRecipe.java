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

/**
 * Shaped, paged-forge-style recipe used by the Chest Forge.
 * <p>
 * Characteristics:
 * <ul>
 *   <li>Shaped grid defined by {@code width}×{@code height}.</li>
 *   <li>Ingredients stored row-major in a fixed-size {@link NonNullList} of length {@code width*height}.</li>
 *   <li>Optional horizontal mirroring (left-right).</li>
 *   <li>Custom processing time in ticks.</li>
 * </ul>
 *
 * @param ingredients size = width * height
 * @param time        ticks
 */
public record ChestForgeRecipe(int width, int height, NonNullList<Ingredient> ingredients, ItemStack result, int time,
                               boolean mirror) implements Recipe<CraftingInput> {

    /**
     * @param width       recipe width
     * @param height      recipe height
     * @param ingredients row-major list sized to width*height
     * @param result      output stack (copied for safety)
     * @param time        craft time in ticks (clamped to ≥ 1)
     * @param mirror      whether horizontal mirroring is allowed
     */
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

    /**
     * Craft time in ticks.
     */
    @Override
    public int time() {
        return time;
    }

    /**
     * Defensive copy of the result.
     */
    public ItemStack output() {
        return result.copy();
    }

    /**
     * Server-only match check against a {@link CraftingInput} of the exact same dimensions.
     * Tries normal orientation first, then mirrored if enabled.
     */
    @Override
    public boolean matches(@NotNull CraftingInput input, Level level) {
        if (level.isClientSide) return false;
        if (input.width() != width || input.height() != height) return false;

        // Direct pattern
        if (checkMatch(input, false)) return true;
        return mirror && checkMatch(input, true);
    }

    /**
     * Compares each slot against the ingredient grid, optionally mirrored horizontally.
     */
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

    /**
     * Vanilla helper: can this recipe fit inside a w×h crafting area?
     */
    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return w >= width && h >= height;
    }

    @NotNull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CHEST_FORGE_SERIALIZER.get();
    }

    @NotNull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CHEST_FORGE_TYPE.get();
    }

    /**
     * Marked special so it won't show in the vanilla recipe book by default.
     */
    @Override
    public boolean isSpecial() {
        return true;
    }

    /* ===== Serializer ===== */

    /**
     * Serializer/codec for {@link ChestForgeRecipe}.
     * <p>
     * JSON format (example):
     * <pre>
     * {
     *   "type": "fullchest:chest_forge",
     *   "pattern": [
     *     "A A",
     *     " B ",
     *     "A A"
     *   ],
     *   "key": {
     *     "A": { "item": "minecraft:iron_ingot" },
     *     "B": { "item": "minecraft:chest" }
     *   },
     *   "result": { "item": "fullchest:iron_chest" },
     *   "time": 200,
     *   "mirror": true
     * }
     * </pre>
     */
    public static class Serializer implements RecipeSerializer<ChestForgeRecipe> {

        /**
         * JSON → recipe codec.
         * <p>
         * Notes:
         * <ul>
         *   <li>{@code pattern} is consumed only for reading (getter returns an empty list).</li>
         *   <li>{@code key} maps single-character symbols to {@link Ingredient}s.</li>
         *   <li>{@code result}, {@code time} (default 200), and {@code mirror} (default true) are standard fields.</li>
         * </ul>
         */
        public static final MapCodec<ChestForgeRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.STRING.listOf().fieldOf("pattern").forGetter(r -> List.of()), // read-only
                Codec.unboundedMap(Codec.STRING, Ingredient.CODEC).fieldOf("key").forGetter(r -> Map.of()),
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result),
                Codec.INT.optionalFieldOf("time", 200).forGetter(r -> r.time),
                Codec.BOOL.optionalFieldOf("mirror", true).forGetter(r -> r.mirror)
        ).apply(inst, (patternList, keyMap, result, time, mirror) -> {
            Parsed p = parsePatternAndKey(patternList, keyMap);
            return new ChestForgeRecipe(p.width, p.height, p.ingredients, result, time, mirror);
        }));

        /**
         * Network (de)serialization codec.
         * Writes: width, height, ingredient list, result, time, mirror.
         * Rebuilds a {@link NonNullList} sized to {@code width*height} and copies ingredients in order.
         */
        public static final StreamCodec<RegistryFriendlyByteBuf, ChestForgeRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.VAR_INT, ChestForgeRecipe::width,
                        ByteBufCodecs.VAR_INT, ChestForgeRecipe::height,

                        // Ingredient list (row-major), using the built-in contents stream codec
                        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                        ChestForgeRecipe::ingredients,

                        // Result stack
                        ItemStack.STREAM_CODEC, r -> r.result,

                        // Time
                        ByteBufCodecs.VAR_INT, ChestForgeRecipe::time,

                        // Mirror flag
                        ByteBufCodecs.BOOL, r -> r.mirror,

                        // Constructor
                        (Integer w, Integer h, List<Ingredient> list, ItemStack res, Integer t, Boolean m) -> {
                            NonNullList<Ingredient> ingredients = NonNullList.withSize(w * h, Ingredient.EMPTY);
                            for (int i = 0; i < Math.min(ingredients.size(), list.size()); i++) {
                                ingredients.set(i, list.get(i));
                            }
                            return new ChestForgeRecipe(w, h, ingredients, res, t, m);
                        }
                );

        @Override
        public @NotNull MapCodec<ChestForgeRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ChestForgeRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        /* -------- utilities -------- */

        private record Parsed(int width, int height, NonNullList<Ingredient> ingredients) {
        }

        /**
         * Parses a shaped pattern and key into a width/height and a row‑major ingredient list.
         * Trims empty rows/columns around the pattern (like vanilla shaped recipes).
         */
        private static Parsed parsePatternAndKey(List<String> pattern, Map<String, Ingredient> key) {
            pattern = shrink(pattern);
            int h = pattern.size();
            int w = h == 0 ? 0 : pattern.getFirst().length();

            // dictionary: 'A' -> ingredient
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

        /**
         * Trims fully-empty rows/columns from the outer edges of the pattern.
         * Uses space (' ') as the empty cell character.
         */
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
