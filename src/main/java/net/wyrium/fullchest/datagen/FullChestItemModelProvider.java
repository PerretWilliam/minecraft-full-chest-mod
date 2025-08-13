package net.wyrium.fullchest.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.block.ModBlocks;
import net.wyrium.fullchest.item.ModItems;
import net.wyrium.fullchest.template.BaseChestBlock;
import net.wyrium.fullchest.template.ChestSpec;

/**
 * Generates all item model JSON files for the FullChest mod.
 * <p>
 * This includes:
 * <ul>
 *   <li>Chest Forge item model</li>
 *   <li>All custom chest items (with builtin/entity rendering)</li>
 *   <li>Flat 2D item models for upgrade items</li>
 * </ul>
 */
public class FullChestItemModelProvider extends ItemModelProvider {

    public FullChestItemModelProvider(PackOutput output, ExistingFileHelper efh) {
        super(output, FullChest.MODID, efh);
    }

    @Override
    protected void registerModels() {

        // Chest Forge item model (uses block model as parent)
        withExistingParent("chest_forge", modLoc("block/chest_forge"));

        // Generate models for all registered chest blocks
        ModBlocks.ALL_CHESTS.forEach(holder -> {
            BaseChestBlock block = (BaseChestBlock) holder.get();
            ChestSpec spec = block.spec();

            String name = spec.id() + "_chest";

            // Use the explicit particle texture from the chest spec, or fallback to main texture if null
            ResourceLocation particle = spec.particle() != null ? spec.particle() : spec.texSingle();

            // Create a chest item model using builtin/entity
            chestItemBuiltinEntity(name, particle);
        });

        // Base chest upgrade item (flat 2D model)
        String baseChestId = ModItems.BASE_CHEST_UPGRADE.getId().getPath();
        flatItem(baseChestId);

        // All upgrade items (flat 2D models)
        ModItems.ALL_UPGRADES.forEach(roh -> {
            String id = roh.getId().getPath();
            flatItem(id);
        });
    }

    /**
     * Generates a standard flat item model:
     * <ul>
     *   <li>Parent: item/generated (vanilla flat item model)</li>
     *   <li>Layer0 texture: fullchest:item/&lt;name&gt;</li>
     * </ul>
     */
    private void flatItem(String name) {
        getBuilder(name).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", modLoc("item/" + name));
    }

    /**
     * Generates a chest item model rendered as a 3D entity (builtin/entity).
     * <p>
     * This is required for chests so they appear as full 3D blocks in inventory,
     * held in hand, on the ground, etc.
     *
     * @param name     The model name (usually &lt;spec.id&gt;_chest).
     * @param particle The particle texture for breaking particles and GUI icon.
     */
    private void chestItemBuiltinEntity(String name, ResourceLocation particle) {
        ItemModelBuilder b = getBuilder(name)
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                .texture("particle", particle);

        // Define display transformations for different perspectives
        b.transforms()
                .transform(ItemDisplayContext.GUI)
                .rotation(30, 45, 0)
                .translation(0, 0, 0)
                .scale(0.625f).end()
                .transform(ItemDisplayContext.GROUND)
                .rotation(0, 0, 0)
                .translation(0, 3, 0)
                .scale(0.25f).end()
                .transform(ItemDisplayContext.HEAD)
                .rotation(0, 180, 0)
                .translation(0, 0, 0)
                .scale(1.0f).end()
                .transform(ItemDisplayContext.FIXED)
                .rotation(0, 180, 0)
                .translation(0, 0, 0)
                .scale(0.5f).end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                .rotation(75, 315, 0)
                .translation(0, 2.5f, 0)
                .scale(0.375f).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                .rotation(0, 315, 0)
                .translation(0, 0, 0)
                .scale(0.4f).end();
    }
}
