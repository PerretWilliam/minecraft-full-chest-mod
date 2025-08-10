package net.wyrium.fullchest.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.wyrium.fullchest.FullChest;

@EventBusSubscriber(modid = FullChest.MODID)
public class FullChestDatagen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var packOutput = gen.getPackOutput();
        var efh = event.getExistingFileHelper();

        // Blockstates + block models
        gen.addProvider(event.includeClient(), new FullChestBlockStateProvider(packOutput, efh));

        // Item models
        gen.addProvider(event.includeClient(), new FullChestItemModelProvider(packOutput, efh));
    }
}
