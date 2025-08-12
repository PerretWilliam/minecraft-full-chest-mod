package net.wyrium.fullchest.datagen;

import net.minecraft.core.HolderLookup;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.wyrium.fullchest.FullChest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = FullChest.MODID)
public class FullChestDatagen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var packOutput = gen.getPackOutput();
        var efh = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Blockstates + Block Models
        gen.addProvider(event.includeClient(), new FullChestBlockStateProvider(packOutput, efh));

        // Item models
        gen.addProvider(event.includeClient(), new FullChestItemModelProvider(packOutput, efh));

        // Recipes
        gen.addProvider(event.includeServer(), new FullChestRecipeProvider(packOutput, lookupProvider));
        gen.addProvider(event.includeServer(), new ChestForgeJsonProvider(packOutput));

        // Advancements
        gen.addProvider(
                event.includeServer(),
                new AdvancementProvider(
                        packOutput,
                        lookupProvider,
                        efh, // ExistingFileHelper
                        List.of(new FullChestAdvancementGenerator())
                )
        );
    }
}
