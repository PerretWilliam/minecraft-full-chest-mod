package net.wyrium.fullchest.datagen;

import net.minecraft.core.HolderLookup;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.wyrium.fullchest.FullChest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Registers all data generation providers for the FullChest mod.
 * <p>
 * This is called automatically when running the <code>data</code> run configuration.
 * It wires up the providers for:
 * <ul>
 *   <li>Blockstates + Block models</li>
 *   <li>Item models</li>
 *   <li>Recipes (vanilla + custom forge recipes)</li>
 *   <li>Advancements</li>
 * </ul>
 */
@EventBusSubscriber(modid = FullChest.MODID)
public class FullChestDatagen {

    /**
     * Event handler for the data generation event.
     * Registers all the different datagen providers for client and server data.
     */
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var packOutput = gen.getPackOutput();
        var existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // --- Client-side data providers ---

        // Blockstates + Block models
        gen.addProvider(event.includeClient(), new FullChestBlockStateProvider(packOutput, existingFileHelper));

        // Item models
        gen.addProvider(event.includeClient(), new FullChestItemModelProvider(packOutput, existingFileHelper));

        // --- Server-side data providers ---

        // Vanilla-style recipes
        gen.addProvider(event.includeServer(), new FullChestRecipeProvider(packOutput, lookupProvider));

        // Custom Chest Forge recipes (JSON)
        gen.addProvider(event.includeServer(), new ChestForgeJsonProvider(packOutput));

        // Advancements
        gen.addProvider(
                event.includeServer(),
                new AdvancementProvider(packOutput, lookupProvider, existingFileHelper, List.of(new FullChestAdvancementGenerator()))
        );
    }
}
