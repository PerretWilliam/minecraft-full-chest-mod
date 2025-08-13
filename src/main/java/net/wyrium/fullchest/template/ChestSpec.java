package net.wyrium.fullchest.template;

import net.minecraft.resources.ResourceLocation;
import net.wyrium.fullchest.sound.ChestSoundPack;

/**
 * Immutable specification that drives the visuals, capacity, and audio for a custom chest.
 * <p>
 * What it defines:
 * <ul>
 *   <li><b>Identity & UI</b>: an internal id and a translatable title key.</li>
 *   <li><b>Capacity</b>: total number of slots; pagination is derived from a fixed 6×9 grid per page.</li>
 *   <li><b>Textures</b>: three texture locations (single, left, right) for the chest renderer.</li>
 *   <li><b>Material tag</b>: a key and a representative color used by UI themes or tooltips.</li>
 *   <li><b>Particles</b>: particle texture shown for effects related to this chest.</li>
 *   <li><b>Sounds</b>: a per‑spec sound pack (open/close/lock etc.).</li>
 * </ul>
 *
 * <h3>Notes</h3>
 * <ul>
 *   <li><b>Color</b>: {@code materialColor} is an RGB integer (0xRRGGBB). If you pass ARGB, ignore alpha.</li>
 *   <li><b>Textures</b>: All texture paths must be <i>atlas-local</i> (e.g. on {@code Sheets.CHEST_SHEET}).</li>
 *   <li><b>Capacity</b>: UI pages are fixed to 54 slots (6 rows × 9 cols). {@link #pages()} derives from {@link #totalSlots}.</li>
 *   <li><b>Validation</b>: Ensure {@code totalSlots >= 0}. Negative values will yield at least 1 page due to {@link Math#max} in {@link #pages()}.</li>
 * </ul>
 */
public record ChestSpec(
        String id, // Internal identifier (e.g., "dirt", "iron")
        int totalSlots, // Total inventory capacity (e.g., 160)
        String titleKey, // Translatable UI title (e.g., "block.fullchest.dirt_chest")
        ResourceLocation texSingle,
        ResourceLocation texLeft,
        ResourceLocation texRight,
        String materialKey, // UI/theming key (e.g., "material.fullchest.iron")
        int materialColor, // 0xRRGGBB (e.g., 0xC0C0C0 silver, 0xFFD700 gold)
        ResourceLocation particle, // Particle texture (e.g., "minecraft:block/iron_block")
        ChestSoundPack sounds // Per-spec sound set (open/close, etc.)
) {
    /** Vanilla-style grid per page: 6 rows × 9 columns. */
    public static final int ROWS = 6;
    public static final int COLS = 9;

    /** UI page size (inventory slots per page). */
    public static final int PAGE_SIZE = ROWS * COLS; // 54

    // Helper methods for convenience
    public int rows()     { return ROWS; }
    public int cols()     { return COLS; }
    public int pageSize() { return PAGE_SIZE; }

    /**
     * Number of UI pages required to display {@link #totalSlots} with 54 slots per page.
     * Always returns at least 1.
     */
    public int pages() {
        return Math.max(1, (int) Math.ceil(totalSlots / (double) PAGE_SIZE));
    }
}
