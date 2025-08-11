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
import net.wyrium.fullchest.template.BaseChestBlock;
import net.wyrium.fullchest.template.ChestSpec;

public class FullChestItemModelProvider extends ItemModelProvider {

    public FullChestItemModelProvider(PackOutput output, ExistingFileHelper efh) {
        super(output, FullChest.MODID, efh);
    }

    @Override
    protected void registerModels() {

        withExistingParent("chest_forge", modLoc("block/chest_forge"));

        // Iterate over actually-registered chest blocks
        ModBlocks.ALL_CHESTS.forEach(holder -> {
            BaseChestBlock block = (BaseChestBlock) holder.get();
            ChestSpec spec = block.spec();

            String name = spec.id() + "_chest";

            // Prefer explicit particle from spec; fallback to main texture if null
            ResourceLocation particle = spec.particle() != null ? spec.particle() : spec.texSingle();

            chestItemBuiltinEntity(name, particle);
        });
    }

    /**
     * Generates models/item/<name>.json with:
     * - parent: builtin/entity (unchecked parent to avoid EFH validation errors)
     * - particle: provided texture (used for breaking/particles)
     * - standard chest item display transforms
     */
    private void chestItemBuiltinEntity(String name, ResourceLocation particle) {
        ItemModelBuilder b = getBuilder(name)
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                .texture("particle", particle);

        b.transforms()
                .transform(ItemDisplayContext.GUI)
                .rotation(30, 45, 0).translation(0, 0, 0).scale(0.625f).end()
                .transform(ItemDisplayContext.GROUND)
                .rotation(0, 0, 0).translation(0, 3, 0).scale(0.25f).end()
                .transform(ItemDisplayContext.HEAD)
                .rotation(0, 180, 0).translation(0, 0, 0).scale(1.0f).end()
                .transform(ItemDisplayContext.FIXED)
                .rotation(0, 180, 0).translation(0, 0, 0).scale(0.5f).end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                .rotation(75, 315, 0).translation(0, 2.5f, 0).scale(0.375f).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                .rotation(0, 315, 0).translation(0, 0, 0).scale(0.4f).end();
    }
}
