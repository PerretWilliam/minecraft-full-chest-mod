package net.wyrium.fullchest.template;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.wyrium.fullchest.sound.ChestSoundPack;

// ChestSpec.java
public record ChestSpec(
        String id,                 // "dirt", "iron", ...
        int totalSlots,            // ex: 160
        String titleKey,           // "block.fullchest.dirt_chest"
        ResourceLocation texSingle,
        ResourceLocation texLeft,
        ResourceLocation texRight,
        String materialKey,  // ex: "material.fullchest.iron"
        int materialColor,    // ex: 0xC0C0C0 (silver), 0xFFD700 (gold), 0xB9F2FF (diamond)
        ResourceLocation particle, // ex: "minecraft:block/iron_block"
        ChestSoundPack sounds // // Per‑spec sound set
) {
    // Vanilla Grid 6x9
    public static final int ROWS = 6;
    public static final int COLS = 9;
    public static final int PAGE_SIZE = ROWS * COLS; // 54

    // Helpers (compat avec ton ancien design)
    public int rows()     { return ROWS; }
    public int cols()     { return COLS; }
    public int pageSize() { return PAGE_SIZE; }
    public int pages()    { return Math.max(1, (int)Math.ceil(totalSlots / (double) PAGE_SIZE)); }
}
