package net.wyrium.fullchest.datagen;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class FullChestAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
    private final FullChestAdvancements sub = new FullChestAdvancements();

    @Override
    public void generate(HolderLookup.@NotNull Provider provider,
                         @NotNull Consumer<AdvancementHolder> consumer,
                         @NotNull ExistingFileHelper existingFileHelper) {
        sub.generate(provider, consumer);
    }
}
