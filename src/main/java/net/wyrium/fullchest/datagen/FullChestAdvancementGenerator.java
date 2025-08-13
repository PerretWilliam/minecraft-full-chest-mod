package net.wyrium.fullchest.datagen;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Wires the mod's advancement definitions into NeoForge's data generation.
 * <p>
 * This class delegates the actual advancement creation to {@link FullChestAdvancements}
 * and simply adapts it to the {@link AdvancementProvider.AdvancementGenerator} interface.
 */
public final class FullChestAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {

    /** Sub-generator that owns the concrete advancement definitions. */
    private final FullChestAdvancements sub = new FullChestAdvancements();

    /**
     * Entry point invoked by the data generator. Emits all advancements to the provided consumer.
     *
     * @param provider            registry/lookup access for resolving holders during generation
     * @param consumer            sink for produced {@link AdvancementHolder} instances
     * @param existingFileHelper  helper for checking pre-existing files (unused here but required by the API)
     */
    @Override
    public void generate(HolderLookup.@NotNull Provider provider, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper existingFileHelper) {
        // Delegate to the mod's concrete advancement builder
        sub.generate(provider, consumer);
    }
}
