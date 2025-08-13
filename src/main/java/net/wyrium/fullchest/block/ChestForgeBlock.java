package net.wyrium.fullchest.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.block.entity.ChestForgeBlockEntity;
import net.wyrium.fullchest.block.entity.ModBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * Represents the Chest Forge block.
 * <p>
 * This block acts as a special crafting/upgrade station for chests.
 * It is a container block (has an associated {@link BlockEntity}) and
 * opens a custom GUI when interacted with.
 */
public class ChestForgeBlock extends Block implements EntityBlock {

    /**
     * Creates a new Chest Forge block with the specified properties.
     *
     * @param props Standard {@link Properties} for block configuration.
     */
    public ChestForgeBlock(Properties props) {
        super(props);
    }

    /* =========================
       Block Entity Handling
       ========================= */

    /**
     * Creates a new block entity instance when this block is placed in the world.
     *
     * @param pos   Position of the block.
     * @param state Current block state.
     * @return      A new {@link ChestForgeBlockEntity} instance.
     */
    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ChestForgeBlockEntity(pos, state);
    }

    /* =========================
       Player Interaction
       ========================= */

    /**
     * Called when a player right-clicks the block with an empty hand (server-side).
     * Opens the Chest Forge menu.
     *
     * @param state      The current block state.
     * @param level      The world level.
     * @param pos        The block's position.
     * @param player     The player interacting with the block.
     * @param hitResult  The ray trace hit result.
     * @return           {@link InteractionResult#SUCCESS} if the GUI was opened.
     */
    @NotNull
    @Override
    protected InteractionResult useWithoutItem(@Nonnull BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Player player,
                                               @Nonnull BlockHitResult hitResult
    ) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof ChestForgeBlockEntity chestForgeBlockEntity) {
                // Open the block's container menu with a translatable title
                player.openMenu(new SimpleMenuProvider(chestForgeBlockEntity, Component.translatable("block." + FullChest.MODID + ".chest_forge")), pos);
            } else {
                throw new IllegalStateException("ChestForgeBlock is missing its container provider!");
            }
        }
        return InteractionResult.SUCCESS;
    }

    /* =========================
       Server Ticking
       ========================= */

    /**
     * Returns a ticker for server-side updates of the block entity.
     *
     * @param level The current level.
     * @param state The block's state.
     * @param type The type of block entity.
     * @return A server-side ticker for {@link ChestForgeBlockEntity}, or null if not applicable.
     */
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            @NotNull BlockState state,
            @NotNull BlockEntityType<T> type
    ) {
        if (level.isClientSide) return null;
        return type == ModBlockEntities.CHEST_FORGE_BE.get()
                ? (lvl, pos, st, be) -> ChestForgeBlockEntity.serverTick(lvl, pos, st, (ChestForgeBlockEntity) be)
                : null;
    }
}
