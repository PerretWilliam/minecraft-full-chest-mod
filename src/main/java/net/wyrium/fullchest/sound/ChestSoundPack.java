package net.wyrium.fullchest.sound;

import net.minecraft.sounds.SoundEvent;

/**
 * Immutable set of sound events representing the standard 5-sound
 * block interaction set used by {@link net.minecraft.world.level.block.SoundType}.
 * <p>
 * Order and meaning:
 * <ul>
 *   <li>{@code breakSnd} — Played when the block is broken/destroyed.</li>
 *   <li>{@code stepSnd} — Played when an entity walks over the block.</li>
 *   <li>{@code placeSnd} — Played when the block is placed.</li>
 *   <li>{@code hitSnd} — Played when the block is hit but not broken (e.g., punching it in survival).</li>
 *   <li>{@code fallSnd} — Played when an entity lands on the block from a fall.</li>
 * </ul>
 * <p>
 * Typical usage:
 * <pre>
 * ChestSoundPack metalSounds = new ChestSoundPack(
 *     SoundEvents.METAL_BREAK,
 *     SoundEvents.METAL_STEP,
 *     SoundEvents.METAL_PLACE,
 *     SoundEvents.METAL_HIT,
 *     SoundEvents.METAL_FALL
 * );
 * </pre>
 *
 * @param breakSnd sound for breaking the block
 * @param stepSnd sound for stepping on the block
 * @param placeSnd sound for placing the block
 * @param hitSnd sound for hitting the block without breaking it
 * @param fallSnd sound for landing on the block after a fall
 */
public record ChestSoundPack(
        SoundEvent breakSnd,
        SoundEvent stepSnd,
        SoundEvent placeSnd,
        SoundEvent hitSnd,
        SoundEvent fallSnd
) {}
