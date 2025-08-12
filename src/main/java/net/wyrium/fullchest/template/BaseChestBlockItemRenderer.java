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

// BaseChestBlockItemRenderer.java
public class BaseChestBlockItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final BlockEntityRenderDispatcher dispatcher;
    private final BaseChestBlockEntity fakeChest;

    public BaseChestBlockItemRenderer(BlockEntityRenderDispatcher disp, EntityModelSet models, ChestSpec spec, Block block) {
        super(disp, models);
        this.dispatcher = disp;

        BlockState state = block.defaultBlockState()
                .setValue(ChestBlock.FACING, Direction.SOUTH)
                .setValue(ChestBlock.TYPE, ChestType.SINGLE)
                .setValue(ChestBlock.WATERLOGGED, false);

        this.fakeChest = new BaseChestBlockEntity(BlockPos.ZERO, state, spec);
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext ctx, @NotNull PoseStack pose,
                             @NotNull MultiBufferSource buffers, int packedLight, int packedOverlay) {
        dispatcher.renderItem(fakeChest, pose, buffers, packedLight, packedOverlay);
    }
}
