package net.wyrium.fullchest.template;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * A base chest block that wires a {@link ChestSpec} into both the block and its block entity.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Create a {@link BaseChestBlockEntity} with the provided {@code ChestSpec}.</li>
 *   <li>Drop inventory contents when removed (e.g., when broken or replaced by another block).</li>
 *   <li>Handle initial placement state (facing, single chest type, waterlogging).</li>
 *   <li>Maintain waterlogging by scheduling fluid ticks.</li>
 *   <li>Play an open sound only when the interaction actually opens the chest.</li>
 * </ul>
 */
public class BaseChestBlock extends ChestBlock {
    /** Immutable spec describing capacity/behavior shared with the block entity. */
    private final ChestSpec spec;

    /**
     * @param props standard block properties (strength, sound type, etc.)
     * @param beType supplier for the chest block entity type
     * @param spec custom spec used by this chest and its block entity
     */
    public BaseChestBlock(Properties props,
                          Supplier<BlockEntityType<? extends ChestBlockEntity>> beType,
                          ChestSpec spec) {
        super(props, beType);
        this.spec = spec;
    }

    /** Exposes the chest specification for callers that need it. */
    public ChestSpec spec() { return spec; }

    /**
     * Creates the block entity and injects the {@link ChestSpec} so the BE knows its configuration.
     */
    @Override
    public @NotNull BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new BaseChestBlockEntity(pos, state, spec);
    }

    /**
     * When the block is replaced by a different block, drop contained items and update redstone output.
     * <p>Note: This only runs when the new state is a different block instance. Vanilla pattern.</p>
     */
    @Override
    protected void onRemove(BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof Container container) {
                // Spill inventory to the world
                Containers.dropContents(level, pos, container);
                // Notify comparators that the container value changed
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    /**
     * Sets the initial state upon placement:
     * <ul>
     *   <li>Faces the player (front toward the player).</li>
     *   <li>Starts as a SINGLE chest (no auto-merge here).</li>
     *   <li>Waterlogged if placed inside water.</li>
     * </ul>
     */
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

    /**
     * Keeps waterlogging consistent by scheduling a water tick when needed.
     * <p>Return the same state (no shape-based merging logic here).</p>
     */
    @Override
    protected @NotNull BlockState updateShape(BlockState state,
                                              @Nonnull Direction face,
                                              @Nonnull BlockState neighbor,
                                              @Nonnull LevelAccessor level,
                                              @Nonnull BlockPos pos,
                                              @Nonnull BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state;
    }

    /**
     * Handles right-click (without holding an item). Let vanilla open logic run,
     * and only play the chest-open sound if the interaction actually consumed the action.
     * <p>
     * On servers, use {@link ServerPlayer#playNotifySound} so nearby players hear it.
     * On clients (or if not a {@link ServerPlayer}), play the sound at the block position.
     * </p>
     */
    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player,
                                                        @NotNull BlockHitResult hit) {
        InteractionResult result = super.useWithoutItem(state, level, pos, player, hit);

        // Only play the sound if something actually happened (e.g., the chest opened)
        if (!level.isClientSide && result.consumesAction()) {
            if (player instanceof ServerPlayer sp) {
                sp.playNotifySound(SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            } else {
                level.playSound(null, pos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
        return result;
    }
}
