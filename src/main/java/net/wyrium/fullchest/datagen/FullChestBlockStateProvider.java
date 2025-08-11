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
        chestForge();

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

    private void chestForge() {
        var name = "chest_forge";

        // Create a cube model with per-face textures (like crafting table style)
        var top    = modLoc("block/forge_table_top");
        var front  = modLoc("block/forge_table_front");
        var side   = modLoc("block/forge_table_side");
        var bottom = modLoc("block/forge_table_bottom");

        // Parent: block/cube and specify each face texture explicitly
        ModelFile forgeModel = models().withExistingParent(name, mcLoc("block/cube"))
                .texture("particle", side)
                .texture("down", bottom)
                .texture("up", top)
                .texture("north", front)  // front face
                .texture("south", side)
                .texture("east",  side)
                .texture("west",  side);

        simpleBlock(ModBlocks.CHEST_FORGE.get(), forgeModel);
    }
}
