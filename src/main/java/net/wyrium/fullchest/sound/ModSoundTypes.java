package net.wyrium.fullchest.sound;

import net.minecraft.world.level.block.SoundType;
import net.wyrium.fullchest.template.ChestSpec;

/**
 * Factory for creating {@link SoundType} instances from a given {@link ChestSpec}.
 * <p>
 * This ensures that each custom chest uses its own {@link ChestSpec#sounds()}
 * for all five block interaction events (break, step, place, hit, fall),
 * matching the sounds defined in {@link net.wyrium.fullchest.sound.ChestSoundPack}.
 * </p>
 *
 * <h3>Notes</h3>
 * <ul>
 *   <li>Volume and pitch are fixed to {@code 1.0F} by default; adjust here if you need variation.</li>
 *   <li>Even if the {@link SoundType} constructor shows as deprecated in your mappings,
 *       it is still valid to use for this purpose.</li>
 *   <li>This helper centralizes sound setup to avoid repetitive {@code new SoundType(...)} calls.</li>
 * </ul>
 */
public final class ModSoundTypes {

    /** Utility class; prevent instantiation. */
    private ModSoundTypes() {}

    /**
     * Creates a {@link SoundType} from the provided {@link ChestSpec}.
     *
     * @param spec the chest specification containing the sound pack
     * @return a {@link SoundType} using the spec's sounds for all block interactions
     */
    public static SoundType forSpec(ChestSpec spec) {
        var specSounds = spec.sounds();

        // Even if constructor shows deprecated in mappings, it's fine here.
        return new SoundType(
                1.0F, 1.0F, // volume, pitch
                specSounds.breakSnd(), // break
                specSounds.stepSnd(), // step (walking on)
                specSounds.placeSnd(), // place
                specSounds.hitSnd(), // hit
                specSounds.fallSnd() // fall
        );
    }
}
