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

/**
 * Generates blockstate and model JSON files for all blocks in the FullChest mod.
 * <p>
 * This includes:
 * <ul>
 *   <li>The Chest Forge (custom cube model with unique face textures)</li>
 *   <li>All custom chest blocks, which rely on a particle-only model
 *       since their rendering is handled by a BlockEntityRenderer (BER)</li>
 * </ul>
 */
public class FullChestBlockStateProvider extends BlockStateProvider {

    public FullChestBlockStateProvider(PackOutput output, ExistingFileHelper efh) {
        super(output, FullChest.MODID, efh);
    }

    @Override
    protected void registerStatesAndModels() {
        // Generate the model & blockstate for the Chest Forge
        chestForge();

        // Generate blockstates & particle models for all registered custom chests
        for (var holder : ModBlocks.ALL_CHESTS) {
            BaseChestBlock block = (BaseChestBlock) holder.get();
            ChestSpec spec = block.spec();
            chest(block, spec.id() + "_chest", spec.particle());
        }
    }

    /**
     * Generates a particle-only model for a chest block.
     * <p>
     * The chest's actual 3D rendering is handled by a BlockEntityRenderer (BER),
     * so the block model itself only needs a "particle" texture for breaking
     * animations and inventory representation.
     *
     * @param block    The chest block instance.
     * @param name     The model name.
     * @param particle The particle texture (null defaults to stone).
     */
    private void chest(Block block, String name, ResourceLocation particle) {
        // Use the provided particle texture, or default to stone if null
        ResourceLocation particleTex = particle != null ? particle : ResourceLocation.parse("minecraft:block/stone");

        // Create a model that only defines a particle texture
        ModelFile blockModel = models()
                .withExistingParent(name, mcLoc("block/block"))
                .texture("particle", particleTex);

        // All block states point to the same model
        getVariantBuilder(block).forAllStates(s -> ConfiguredModel.builder().modelFile(blockModel).build());
    }

    /**
     * Generates the model and blockstate for the Chest Forge.
     * <p>
     * This uses a standard cube model with different textures for each face,
     * similar to the crafting table in vanilla Minecraft.
     */
    private void chestForge() {
        var name = "chest_forge";

        // Texture locations for each face
        var top    = modLoc("block/forge_table_top");
        var front  = modLoc("block/forge_table_front");
        var side   = modLoc("block/forge_table_side");
        var bottom = modLoc("block/forge_table_bottom");

        // Create a cube model with per-face textures
        ModelFile forgeModel = models().withExistingParent(name, mcLoc("block/cube"))
                .texture("particle", side)
                .texture("down", bottom)
                .texture("up", top)
                .texture("north", front)  // Front face
                .texture("south", side)
                .texture("east",  side)
                .texture("west",  side);

        // Simple blockstate: all states map to the same model
        simpleBlock(ModBlocks.CHEST_FORGE.get(), forgeModel);
    }
}
