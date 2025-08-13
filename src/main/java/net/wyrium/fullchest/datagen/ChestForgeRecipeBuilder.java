package net.wyrium.fullchest.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.wyrium.fullchest.recipe.ChestForgeRecipe;
import net.wyrium.fullchest.recipe.ModRecipeTypes;

import java.util.*;

/**
 * A builder class for creating and serializing custom Chest Forge recipes.
 * Used both for JSON recipe generation during data generation
 * and for in-memory recipe creation during runtime.
 */
public class ChestForgeRecipeBuilder {

    /** Final recipe output item (with count). */
    private final ItemStack result;

    /** List of strings representing the crafting pattern rows. */
    private final List<String> pattern = new ArrayList<>();

    /** Maps pattern characters to either an ItemLike or TagKey<Item>. */
    private final Map<Character, Object> key = new LinkedHashMap<>();

    /** Custom smelting/crafting time in ticks (default: 200). */
    private int time = 200;

    /** Whether the recipe pattern can be mirrored horizontally. */
    private boolean mirror = true;

    /**
     * Private constructor to enforce the use of static factory methods.
     * @param result ItemStack representing the recipe output.
     */
    private ChestForgeRecipeBuilder(ItemStack result) {
        this.result = result.copy();
    }

    /** Creates a new builder for a recipe outputting the given item (count = 1). */
    public static ChestForgeRecipeBuilder chestForge(ItemLike result) {
        return new ChestForgeRecipeBuilder(new ItemStack(result));
    }

    /** Creates a new builder for a recipe outputting the given item with a custom count. */
    public static ChestForgeRecipeBuilder chestForge(ItemLike result, int count) {
        return new ChestForgeRecipeBuilder(new ItemStack(result, count));
    }

    /** Adds a row to the recipe pattern. */
    public ChestForgeRecipeBuilder pattern(String line) {
        this.pattern.add(line);
        return this;
    }

    /** Maps a pattern symbol to a specific item. */
    public ChestForgeRecipeBuilder define(char symbol, ItemLike item) {
        if (symbol == ' ') throw new IllegalArgumentException("Space cannot be used as a key symbol");
        this.key.put(symbol, item);
        return this;
    }

    /** Maps a pattern symbol to a specific item tag. */
    public ChestForgeRecipeBuilder define(char symbol, TagKey<Item> tag) {
        if (symbol == ' ') throw new IllegalArgumentException("Space cannot be used as a key symbol");
        this.key.put(symbol, tag);
        return this;
    }

    /** Sets the crafting time in ticks. */
    public ChestForgeRecipeBuilder time(int ticks) {
        this.time = Math.max(1, ticks);
        return this;
    }

    /** Enables or disables horizontal mirroring for the recipe pattern. */
    public ChestForgeRecipeBuilder mirror(boolean mirror) {
        this.mirror = mirror;
        return this;
    }

    /**
     * Validates the recipe before saving or serializing.
     * Checks:
     * - Pattern is not empty.
     * - All rows have the same width.
     * - All symbols in the pattern are defined in the key map.
     * - No unused keys exist.
     */
    private void validate() {
        if (pattern.isEmpty())
            throw new IllegalStateException("Pattern is empty");
        int w = pattern.getFirst().length();
        if (w == 0) throw new IllegalStateException("Pattern width = 0");

        // Ensure all rows have the same width
        for (String row : pattern) {
            if (row.length() != w)
                throw new IllegalStateException("All rows in the pattern must have the same width");
        }

        // Ensure all used symbols are defined
        Set<Character> used = new HashSet<>();
        for (String row : pattern) {
            for (int i = 0; i < row.length(); i++) {
                char c = row.charAt(i);
                if (c == ' ') continue;
                used.add(c);
                if (!key.containsKey(c)) {
                    throw new IllegalStateException("Symbol '" + c + "' used in pattern but not defined");
                }
            }
        }

        // Ensure no unused keys are defined
        for (Character c : key.keySet()) {
            if (!used.contains(c))
                throw new IllegalStateException("Key '" + c + "' defined but not used in pattern");
        }
    }

    /** Builds the JSON for this recipe using the registered serializer type. */
    public JsonObject toJson() {
        ResourceLocation typeId = BuiltInRegistries.RECIPE_SERIALIZER.getKey(
                ModRecipeTypes.CHEST_FORGE_SERIALIZER.get()
        );
        if (typeId != null) {
            return toJson(typeId);
        } else {
            throw new IllegalStateException("Recipe serializer not found for ChestForgeRecipe");
        }
    }

    /** Builds the JSON for this recipe using the provided serializer type. */
    public JsonObject toJson(ResourceLocation typeId) {
        JsonObject root = new JsonObject();
        root.addProperty("type", typeId.toString());

        // Pattern array
        JsonArray patt = new JsonArray();
        for (String row : pattern) patt.add(row);
        root.add("pattern", patt);

        // Key mapping
        JsonObject keyObj = new JsonObject();
        for (Map.Entry<Character, Object> e : key.entrySet()) {
            char sym = e.getKey();
            Object v = e.getValue();
            JsonObject ing = new JsonObject();
            if (v instanceof TagKey<?> tagKey) {
                @SuppressWarnings("unchecked")
                TagKey<Item> tag = (TagKey<Item>) tagKey;
                ing.addProperty("tag", tag.location().toString());
            } else {
                ItemLike il = (ItemLike) v;
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(il.asItem());
                ing.addProperty("item", id.toString());
            }
            keyObj.add(String.valueOf(sym), ing);
        }
        root.add("key", keyObj);

        // Result object
        JsonObject res = new JsonObject();
        ResourceLocation rid = BuiltInRegistries.ITEM.getKey(result.getItem());
        res.addProperty("id", rid.toString());
        if (result.getCount() > 1) res.addProperty("count", result.getCount());
        root.add("result", res);

        // Custom fields
        if (time != 200) root.addProperty("time", time);
        root.addProperty("mirror", mirror);

        return root;
    }

    /**
     * Saves the recipe into the given RecipeOutput, after validating and
     * building the ingredient list.
     */
    public void save(RecipeOutput out, ResourceLocation id) {
        validate();

        int height = pattern.size();
        int width = pattern.getFirst().length();

        NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);

        // Build the ingredient dictionary from the key map
        Map<Character, Ingredient> dict = new HashMap<>();
        dict.put(' ', Ingredient.EMPTY);
        for (var e : key.entrySet()) {
            char c = e.getKey();
            Object v = e.getValue();
            if (v instanceof TagKey<?> tagKey) {
                @SuppressWarnings("unchecked")
                var itemTag = (TagKey<Item>) tagKey;
                dict.put(c, Ingredient.of(itemTag));
            } else {
                var il = (ItemLike) v;
                dict.put(c, Ingredient.of(il));
            }
        }

        // Fill the ingredient list from the pattern
        for (int y = 0; y < height; y++) {
            String row = pattern.get(y);
            for (int x = 0; x < width; x++) {
                char ch = row.charAt(x);
                Ingredient ing = dict.getOrDefault(ch, Ingredient.EMPTY);
                ingredients.set(x + y * width, ing);
            }
        }

        // Create and register the recipe
        var recipe = new ChestForgeRecipe(width, height, ingredients, result, time, mirror);
        out.accept(id, recipe, null);
    }
}
