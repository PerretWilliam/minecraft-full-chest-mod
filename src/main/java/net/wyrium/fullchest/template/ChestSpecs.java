package net.wyrium.fullchest.template;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.sound.ChestSoundPack;

public final class ChestSpecs {

    // --- Specs (your progression) ---
    public static final ChestSpec DIRT_SPEC = new ChestSpec(
            "dirt", 18,
            "block." + FullChest.MODID + ".dirt_chest",
            FullChest.rl("entity/chest/dirt_chest_normal"),
            FullChest.rl("entity/chest/normal_chest_left"),
            FullChest.rl("entity/chest/normal_chest_right"),
            "material.fullchest.dirt",
            0x8B5A2B,
            ResourceLocation.parse("minecraft:block/dirt"),
            new ChestSoundPack(
                    SoundEvents.GRASS_BREAK,
                    SoundEvents.GRASS_STEP,
                    SoundEvents.GRASS_PLACE,
                    SoundEvents.GRASS_HIT,
                    SoundEvents.GRASS_FALL
            )
    );

    public static final ChestSpec STONE_SPEC = new ChestSpec(
            "stone", 27,
            "block." + FullChest.MODID + ".stone_chest",
            FullChest.rl("entity/chest/stone_chest_normal"),
            FullChest.rl("entity/chest/normal_chest_left"),
            FullChest.rl("entity/chest/normal_chest_right"),
            "material.fullchest.stone",
            0xBFBFBF,
            ResourceLocation.parse("minecraft:block/stone"),
            new ChestSoundPack(
                    SoundEvents.STONE_BREAK,
                    SoundEvents.STONE_STEP,
                    SoundEvents.STONE_PLACE,
                    SoundEvents.STONE_HIT,
                    SoundEvents.STONE_FALL
            )
    );

    public static final ChestSpec COPPER_SPEC = new ChestSpec(
            "copper", 36,
            "block." + FullChest.MODID + ".copper_chest",
            FullChest.rl("entity/chest/copper_chest_normal"),
            FullChest.rl("entity/chest/normal_chest_left"),
            FullChest.rl("entity/chest/normal_chest_right"),
            "material.fullchest.copper",
            0xc87456,
            ResourceLocation.parse("minecraft:block/copper_block"),
            new ChestSoundPack(
                    SoundEvents.COPPER_BREAK,
                    SoundEvents.COPPER_STEP,
                    SoundEvents.COPPER_PLACE,
                    SoundEvents.COPPER_HIT,
                    SoundEvents.COPPER_FALL
            )
    );

    public static final ChestSpec IRON_SPEC = new ChestSpec(
            "iron", 54,
            "block." + FullChest.MODID + ".iron_chest",
            FullChest.rl("entity/chest/iron_chest_normal"),
            FullChest.rl("entity/chest/normal_chest_left"),
            FullChest.rl("entity/chest/normal_chest_right"),
            "material.fullchest.iron",
            0xC0C0C0,
            ResourceLocation.parse("minecraft:block/iron_block"),
            new ChestSoundPack(
                    SoundEvents.METAL_BREAK,
                    SoundEvents.METAL_STEP,
                    SoundEvents.METAL_PLACE,
                    SoundEvents.METAL_HIT,
                    SoundEvents.METAL_FALL
            )
    );

    public static final ChestSpec GOLD_SPEC = new ChestSpec(
            "gold", 72,
            "block." + FullChest.MODID + ".gold_chest",
            FullChest.rl("entity/chest/gold_chest_normal"),
            FullChest.rl("entity/chest/normal_chest_left"),
            FullChest.rl("entity/chest/normal_chest_right"),
            "material.fullchest.gold",
            0xFFD700,
            ResourceLocation.parse("minecraft:block/gold_block"),
            new ChestSoundPack(
                    SoundEvents.METAL_BREAK,
                    SoundEvents.METAL_STEP,
                    SoundEvents.METAL_PLACE,
                    SoundEvents.METAL_HIT,
                    SoundEvents.METAL_FALL
            )
    );

    public static final ChestSpec EMERALD_SPEC = new ChestSpec(
            "emerald", 90,
            "block." + FullChest.MODID + ".emerald_chest",
            FullChest.rl("entity/chest/emerald_chest_normal"),
            FullChest.rl("entity/chest/normal_chest_left"),
            FullChest.rl("entity/chest/normal_chest_right"),
            "material.fullchest.emerald",
            0x00C853,
            ResourceLocation.parse("minecraft:block/emerald_block"),
            new ChestSoundPack(
                    SoundEvents.METAL_BREAK,
                    SoundEvents.METAL_STEP,
                    SoundEvents.METAL_PLACE,
                    SoundEvents.METAL_HIT,
                    SoundEvents.METAL_FALL
            )
    );

    public static final ChestSpec DIAMOND_SPEC = new ChestSpec(
            "diamond", 108,
            "block." + FullChest.MODID + ".diamond_chest",
            FullChest.rl("entity/chest/diamond_chest_normal"),
            FullChest.rl("entity/chest/normal_chest_left"),
            FullChest.rl("entity/chest/normal_chest_right"),
            "material.fullchest.diamond",
            0x44E5FF,
            ResourceLocation.parse("minecraft:block/diamond_block"),
            new ChestSoundPack(
                    SoundEvents.METAL_BREAK,
                    SoundEvents.METAL_STEP,
                    SoundEvents.METAL_PLACE,
                    SoundEvents.METAL_HIT,
                    SoundEvents.METAL_FALL
            )
    );

    public static final ChestSpec OBSIDIAN_SPEC = new ChestSpec(
            "obsidian", 135,
            "block." + FullChest.MODID + ".obsidian_chest",
            FullChest.rl("entity/chest/obsidian_chest_normal"),
            FullChest.rl("entity/chest/normal_chest_left"),
            FullChest.rl("entity/chest/normal_chest_right"),
            "material.fullchest.obsidian",
            0x3b2754,
            ResourceLocation.parse("minecraft:block/obsidian"),
            new ChestSoundPack(
                    SoundEvents.STONE_BREAK,
                    SoundEvents.STONE_STEP,
                    SoundEvents.STONE_PLACE,
                    SoundEvents.STONE_HIT,
                    SoundEvents.STONE_FALL
            )
    );

    public static final ChestSpec NETHERITE_SPEC = new ChestSpec(
            "netherite", 162,
            "block." + FullChest.MODID + ".netherite_chest",
            FullChest.rl("entity/chest/netherite_chest_normal"),
            FullChest.rl("entity/chest/normal_chest_left"),
            FullChest.rl("entity/chest/normal_chest_right"),
            "material.fullchest.netherite",
            0x2f2829,
            ResourceLocation.parse("minecraft:block/netherite_block"),
            new ChestSoundPack(
                    SoundEvents.METAL_BREAK,
                    SoundEvents.METAL_STEP,
                    SoundEvents.METAL_PLACE,
                    SoundEvents.METAL_HIT,
                    SoundEvents.METAL_FALL
            )
    );

    // Central registry of specs for iteration (datagen, etc.)
    public static final List<ChestSpec> ALL = List.of(
            DIRT_SPEC, STONE_SPEC, COPPER_SPEC, IRON_SPEC, GOLD_SPEC,
            EMERALD_SPEC, DIAMOND_SPEC, OBSIDIAN_SPEC, NETHERITE_SPEC
    );

    public static final Map<String, ChestSpec> BY_ID = ALL.stream().collect(Collectors.toUnmodifiableMap(ChestSpec::id, s -> s));

    public static ChestSpec[] values() { return ALL.toArray(ChestSpec[]::new); }

    private ChestSpecs() {}
}
