package net.wyrium.fullchest.template;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Renderer for {@link BaseChestBlockEntity} that selects the correct texture
 * (single, left, right) based on {@link ChestType}.
 * <p>
 * Implementation details:
 * <ul>
 *   <li>Extends vanilla {@link ChestRenderer} to reuse lid animation, model binding, and lighting logic.</li>
 *   <li>All texture materials are sourced from {@link ChestSpec}, ensuring the BE and item renderers stay consistent.</li>
 *   <li>Materials are created on demand with the standard chest atlas ({@link Sheets#CHEST_SHEET}).</li>
 * </ul>
 */
public class BaseChestEntityRenderer extends ChestRenderer<BaseChestBlockEntity> {

    /**
     * Standard constructor; context provides model layers, font, and other client resources.
     */
    public BaseChestEntityRenderer(BlockEntityRendererProvider.Context ctx) { super(ctx); }

    /**
     * Returns the appropriate {@link Material} for the current chest piece.
     * <p>
     * Uses the vanilla chest texture atlas to ensure proper stitching and batching. The
     * exact texture locations (single/left/right) are delegated to {@link ChestSpec}.
     */
    @Override
    protected @NotNull Material getMaterial(@Nonnull BaseChestBlockEntity be, ChestType type) {
        ChestSpec s = be.spec();
        return switch (type) {
            case LEFT  -> new Material(Sheets.CHEST_SHEET, s.texLeft());
            case RIGHT -> new Material(Sheets.CHEST_SHEET, s.texRight());
            default    -> new Material(Sheets.CHEST_SHEET, s.texSingle());
        };
    }
}
