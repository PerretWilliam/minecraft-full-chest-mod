package net.wyrium.fullchest.template;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.state.properties.ChestType;

import javax.annotation.Nonnull;

// BaseChestEntityRenderer.java
public class BaseChestEntityRenderer extends ChestRenderer<BaseChestBlockEntity> {
    public BaseChestEntityRenderer(BlockEntityRendererProvider.Context ctx) { super(ctx); }

    @Override
    protected Material getMaterial(@Nonnull BaseChestBlockEntity be, ChestType type) {
        ChestSpec s = be.spec();
        return switch (type) {
            case LEFT -> new Material(Sheets.CHEST_SHEET, s.texLeft());
            case RIGHT -> new Material(Sheets.CHEST_SHEET, s.texRight());
            default -> new Material(Sheets.CHEST_SHEET, s.texSingle());
        };
    }
}
