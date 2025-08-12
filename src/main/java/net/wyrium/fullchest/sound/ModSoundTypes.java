package net.wyrium.fullchest.sound;

import net.minecraft.world.level.block.SoundType;
import net.wyrium.fullchest.template.ChestSpec;

public final class ModSoundTypes {
    private ModSoundTypes() {}

    /** Use the spec's single SoundEvent for all block sounds (place/break/step/hit/fall). */
    public static SoundType forSpec(ChestSpec spec) {
        var specSounds = spec.sounds();

        // NOTE: If constructor is deprecated in your mappings, it's still valid to use.
        return new SoundType(
                1.0F, 1.0F,
                specSounds.breakSnd(), // break
                specSounds.stepSnd(), // step (walking on)
                specSounds.placeSnd(), // place
                specSounds.hitSnd(), // hit
                specSounds.fallSnd()  // fall
        );
    }
}
