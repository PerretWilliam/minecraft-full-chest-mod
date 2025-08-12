package net.wyrium.fullchest.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.recipe.ChestForgeRecipe;
import net.wyrium.fullchest.recipe.ModRecipeTypes;

import java.util.*;

public class ChestForgeRecipeBuilder {

    private final ItemStack result;
    private final List<String> pattern = new ArrayList<>();
    private final Map<Character, Object> key = new LinkedHashMap<>(); // ItemLike ou TagKey<Item>
    private int time = 200;
    private boolean mirror = true;

    private ChestForgeRecipeBuilder(ItemStack result) {
        this.result = result.copy();
    }

    public static ChestForgeRecipeBuilder chestForge(ItemLike result) {
        return new ChestForgeRecipeBuilder(new ItemStack(result));
    }

    public static ChestForgeRecipeBuilder chestForge(ItemLike result, int count) {
        return new ChestForgeRecipeBuilder(new ItemStack(result, count));
    }

    public ChestForgeRecipeBuilder pattern(String line) {
        this.pattern.add(line);
        return this;
    }

    public ChestForgeRecipeBuilder define(char symbol, ItemLike item) {
        if (symbol == ' ') throw new IllegalArgumentException("Space not allowed key");
        this.key.put(symbol, item);
        return this;
    }

    public ChestForgeRecipeBuilder define(char symbol, TagKey<Item> tag) {
        if (symbol == ' ') throw new IllegalArgumentException("Space not allowed key");
        this.key.put(symbol, tag);
        return this;
    }

    public ChestForgeRecipeBuilder time(int ticks) {
        this.time = Math.max(1, ticks);
        return this;
    }

    public ChestForgeRecipeBuilder mirror(boolean mirror) {
        this.mirror = mirror;
        return this;
    }

    private void validate() {
        if (pattern.isEmpty())
            throw new IllegalStateException("Pattern vide");
        int w = pattern.get(0).length();
        if (w == 0) throw new IllegalStateException("Largeur de pattern = 0");

        // largeur homogène
        for (String row : pattern) {
            if (row.length() != w)
                throw new IllegalStateException("Toutes les lignes du pattern doivent avoir la même longueur");
        }

        // symboles utilisés définis
        Set<Character> used = new HashSet<>();
        for (String row : pattern) {
            for (int i = 0; i < row.length(); i++) {
                char c = row.charAt(i);
                if (c == ' ') continue;
                used.add(c);
                if (!key.containsKey(c)) {
                    throw new IllegalStateException("Symbole '"+c+"' utilisé dans le pattern mais non défini");
                }
            }
        }

        // pas de clés inutilisées
        for (Character c : key.keySet()) {
            if (!used.contains(c))
                throw new IllegalStateException("Clé '"+c+"' définie mais jamais utilisée dans le pattern");
        }
    }

    /** Construit le JSON conforme à ton serializer (public & auto-type) */
    public JsonObject toJson() {
        ResourceLocation typeId = BuiltInRegistries.RECIPE_SERIALIZER.getKey(
                ModRecipeTypes.CHEST_FORGE_SERIALIZER.get()
        );
        return toJson(typeId);
    }

    /** Construit le JSON conforme à ton serializer (type passé en param) */
    public JsonObject toJson(ResourceLocation typeId) {
        JsonObject root = new JsonObject();
        root.addProperty("type", typeId.toString());

        // pattern
        JsonArray patt = new JsonArray();
        for (String row : pattern) patt.add(row);
        root.add("pattern", patt);

        // key
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

        // result { id, count? }
        JsonObject res = new JsonObject();
        ResourceLocation rid = BuiltInRegistries.ITEM.getKey(result.getItem());
        res.addProperty("id", rid.toString());
        if (result.getCount() > 1) res.addProperty("count", result.getCount());
        root.add("result", res);

        // time + mirror
        if (time != 200) root.addProperty("time", time);
        root.addProperty("mirror", mirror); // toujours écrit

        return root;
    }

    public void save(RecipeOutput out, ResourceLocation id) {
        validate();

        int height = pattern.size();
        int width = pattern.get(0).length();

        NonNullList<Ingredient> ings = NonNullList.withSize(width * height, Ingredient.EMPTY);

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

        for (int y = 0; y < height; y++) {
            String row = pattern.get(y);
            for (int x = 0; x < width; x++) {
                char ch = row.charAt(x);
                Ingredient ing = dict.getOrDefault(ch, Ingredient.EMPTY);
                ings.set(x + y * width, ing);
            }
        }

        var recipe = new ChestForgeRecipe(width, height, ings, result, time, mirror);
        out.accept(id, recipe, (AdvancementHolder) null);
    }
}
