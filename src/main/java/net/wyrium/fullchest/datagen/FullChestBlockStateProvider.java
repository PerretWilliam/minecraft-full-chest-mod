package net.wyrium.fullchest.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.block.ModBlocks;
import net.wyrium.fullchest.template.BaseChestBlock;
import net.wyrium.fullchest.template.ChestSpec;

public class FullChestBlockStateProvider extends BlockStateProvider {

    public FullChestBlockStateProvider(PackOutput output, ExistingFileHelper efh) {
        super(output, FullChest.MODID, efh);
    }

    @Override
    protected void registerStatesAndModels() {
        // Iterate all registered chest blocks
        for (var holder : ModBlocks.ALL_CHESTS) {
            BaseChestBlock block = (BaseChestBlock) holder.get();
            ChestSpec spec = block.spec();
            chest(block, spec.id() + "_chest", spec.particle());
        }
    }

    private void chest(Block block, String name, ResourceLocation particle) {
        // Fallback if particle is null
        ResourceLocation particleTex = particle != null ? particle : ResourceLocation.parse("minecraft:block/stone");

        // particle-only block model (BER handles the visual)
        ModelFile blockModel = models()
                .withExistingParent(name, mcLoc("block/block"))
                .texture("particle", particleTex);

        // all states -> same model
        getVariantBuilder(block).forAllStates(s -> ConfiguredModel.builder().modelFile(blockModel).build());
    }
}
