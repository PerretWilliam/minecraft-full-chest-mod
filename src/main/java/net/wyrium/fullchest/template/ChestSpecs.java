package net.wyrium.fullchest.template;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.sound.ChestSoundPack;

/**
 * Central registry of all predefined {@link ChestSpec} instances for the mod.
 * <p>
 * Each spec defines:
 * <ul>
 *   <li>An internal id used for lookups and upgrades.</li>
 *   <li>Total slot capacity for the chest inventory.</li>
 *   <li>Localization key for the block name.</li>
 *   <li>Textures for single/left/right chest states.</li>
 *   <li>Material key, representative color, and particle texture.</li>
 *   <li>A {@link ChestSoundPack} providing break/place/step/hit/fall sounds.</li>
 * </ul>
 * <p>
 * All specs are gathered into {@link #ALL} and indexed by id in {@link #BY_ID}.
 * Use these collections for registration, lookups, or iteration.
 */
public final class ChestSpecs {

    /** Dirt chest — minimal capacity, uses grass/dirt sounds and brownish tint. */
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

    /** Stone chest — small capacity, uses stone sounds and gray tint. */
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

    /** Copper chest — moderate capacity, copper block particles, warm tint. */
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

    /** Iron chest — large capacity, silver tint, metal sounds. */
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

    /** Gold chest — higher capacity, gold tint, reuses generic metal sounds. */
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

    /** Emerald chest — very high capacity, emerald tint, uses metal sounds. */
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

    /** Diamond chest — huge capacity, light blue tint, uses metal sounds. */
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

    /** Obsidian chest — massive capacity, dark purple tint, stone sounds. */
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

    /** Netherite chest — maximum capacity, dark gray tint, metal sounds. */
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

    /** Immutable list of all registered chest specs in this mod. */
    public static final List<ChestSpec> ALL = List.of(
            DIRT_SPEC, STONE_SPEC, COPPER_SPEC, IRON_SPEC, GOLD_SPEC,
            EMERALD_SPEC, DIAMOND_SPEC, OBSIDIAN_SPEC, NETHERITE_SPEC
    );

    /** Immutable map of chest specs by their {@link ChestSpec#id() id}. */
    public static final Map<String, ChestSpec> BY_ID =
            ALL.stream().collect(Collectors.toUnmodifiableMap(ChestSpec::id, s -> s));

    /** Convenience method to get all specs as an array. */
    public static ChestSpec[] values() { return ALL.toArray(ChestSpec[]::new); }

    /** Utility class; prevent instantiation. */
    private ChestSpecs() {}
}
