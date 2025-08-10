package net.wyrium.fullchest.template;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

// BaseChestBlock.java
public class BaseChestBlock extends ChestBlock {
    private final ChestSpec spec;

    public BaseChestBlock(Properties props,
                          Supplier<BlockEntityType<? extends ChestBlockEntity>> beType,
                          ChestSpec spec) {
        super(props, beType);
        this.spec = spec;
    }
    public ChestSpec spec() { return spec; }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new BaseChestBlockEntity(pos, state, spec);
    }

    @Override
    protected void onRemove(BlockState state, @Nonnull Level level, @Nonnull BlockPos pos,
                            BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof Container container) {
                Containers.dropContents(level, pos, container);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction dir = ctx.getHorizontalDirection().getOpposite();
        boolean water = ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER;
        return this.defaultBlockState()
                .setValue(FACING, dir)
                .setValue(TYPE, ChestType.SINGLE)
                .setValue(WATERLOGGED, water);
    }

    @Override
    protected BlockState updateShape(BlockState state, @Nonnull Direction face, @Nonnull BlockState neighbor,
                                     @Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state;
    }
}
