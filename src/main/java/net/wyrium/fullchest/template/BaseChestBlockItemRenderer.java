package net.wyrium.fullchest.template;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.jetbrains.annotations.NotNull;

/**
 * Item renderer that draws a chest item by delegating to a preconfigured chest
 * {@link BaseChestBlockEntity} (a "fake" block entity without a world).
 * <p>
 * Why this approach:
 * <ul>
 *   <li>Reuses the block-entity rendering path for visual consistency (same model/animation/textures).</li>
 *   <li>Avoids duplicating model code in an item-specific renderer.</li>
 *   <li>Maintains a single cached BE instance for performance; safe here because we never mutate state per-frame.</li>
 * </ul>
 * Usage:
 * <ul>
 *   <li>Register as the item's BEWLR (BlockEntityWithoutLevelRenderer) during client init.</li>
 *   <li>Provide the same {@link ChestSpec} used by the block so visuals remain consistent with in-world behavior.</li>
 * </ul>
 */
public class BaseChestBlockItemRenderer extends BlockEntityWithoutLevelRenderer {
    /** Dispatcher that knows how to render block entities in item contexts. */
    private final BlockEntityRenderDispatcher dispatcher;

    /** A single, reusable BE instance configured with a stable chest state. */
    private final BaseChestBlockEntity fakeChest;

    /**
     * @param blockEntityRenderDispatcher renderer dispatcher injected by the client
     * @param models model set (forwarded to super for potential model baking access)
     * @param spec chest spec to keep visuals consistent with the actual block entity
     * @param block the backing block; used to build a default, deterministic {@link BlockState}
     */
    public BaseChestBlockItemRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher,
                                      EntityModelSet models,
                                      ChestSpec spec,
                                      Block block) {
        super(blockEntityRenderDispatcher, models);
        this.dispatcher = blockEntityRenderDispatcher;

        // Use a deterministic, single chest state for item rendering.
        // SOUTH facing is conventional for item previews; no waterlogging in item form.
        BlockState state = block.defaultBlockState()
                .setValue(ChestBlock.FACING, Direction.SOUTH)
                .setValue(ChestBlock.TYPE, ChestType.SINGLE)
                .setValue(ChestBlock.WATERLOGGED, false);

        // Zero position is fine since this BE has no level; lighting comes from render args.
        this.fakeChest = new BaseChestBlockEntity(BlockPos.ZERO, state, spec);
    }

    /**
     * Renders the chest item by delegating to the dispatcher, which invokes the
     * registered block-entity renderer for {@link BaseChestBlockEntity}.
     *
     * @param stack the item stack being rendered
     * @param ctx display context (GUI, ground, first/third person, etc.)
     * @param pose pose stack to transform the model
     * @param buffers render buffer source
     * @param packedLight lightmap value provided by the caller
     * @param packedOverlay overlay UV (e.g., damage glint in GUI)
     */
    @Override
    public void renderByItem(@NotNull ItemStack stack,
                             @NotNull ItemDisplayContext ctx,
                             @NotNull PoseStack pose,
                             @NotNull MultiBufferSource buffers,
                             int packedLight,
                             int packedOverlay) {
        // No per-stack mutations needed; all visuals come from the BE renderer + provided lighting.
        dispatcher.renderItem(fakeChest, pose, buffers, packedLight, packedOverlay);
    }
}
