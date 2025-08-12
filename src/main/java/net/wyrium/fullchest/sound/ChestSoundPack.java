package net.wyrium.fullchest.sound;

import net.minecraft.sounds.SoundEvent;

/** Block sound set used by SoundType (place/break/step/hit/fall). */
public record ChestSoundPack(
        SoundEvent breakSnd,
        SoundEvent stepSnd,
        SoundEvent placeSnd,
        SoundEvent hitSnd,
        SoundEvent fallSnd
) {}