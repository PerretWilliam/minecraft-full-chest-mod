package net.wyrium.fullchest.template;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.wyrium.fullchest.FullChest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/**
 * An upgrade item that transforms one chest block into another in-place
 * (sneak + right-click), while preserving inventory and core state.
 * <p>
 * Behavior highlights:
 * <ul>
 *   <li>Only upgrades the expected source block ({@code fromChest}).</li>
 *   <li>Restricts upgrades to {@link ChestType#SINGLE} to avoid double-chest edge cases.</li>
 *   <li>Copies inventory into memory, clears the old container to prevent drops, then restores into the new chest.</li>
 *   <li>Drops overflow if the target chest has fewer slots (safety-first).</li>
 *   <li>Consumes one upgrade item unless the player is in creative mode.</li>
 *   <li>Optionally grants an advancement derived from the item registry key.</li>
 * </ul>
 */
public class ChestUpgradeItem extends Item {
    /** Required source chest block (must match the block at the target position). */
    private final Supplier<Block> fromChest;

    /** Target chest block to replace the source with. */
    private final Supplier<Block> toChest;

    /**
     * @param props standard item properties
     * @param fromChest supplier for the expected source chest block
     * @param toChest supplier for the target chest block
     */
    public ChestUpgradeItem(Properties props, Supplier<Block> fromChest, Supplier<Block> toChest) {
        super(props);
        this.fromChest = fromChest;
        this.toChest = toChest;
    }

    /**
     * Handles sneak + use on a chest to perform the upgrade.
     * <p>Server-side flow:</p>
     * <ol>
     *   <li>Validate source block and chest type.</li>
     *   <li>Read and cache items.</li>
     *   <li>Clear old inventory to prevent {@code onRemove()} from dropping contents.</li>
     *   <li>Build a new state preserving FACING/WATERLOGGED; enforce SINGLE.</li>
     *   <li>Replace the block and restore items, dropping any overflow.</li>
     *   <li>Consume upgrade item (unless creative) and play a feedback sound.</li>
     * </ol>
     * After replacement, if run by a {@link ServerPlayer}, compute and award an advancement id.
     */
    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        Player player = ctx.getPlayer();
        ItemStack stack = ctx.getItemInHand();

        if (player == null) return InteractionResult.PASS;

        // Require shift-right-click to avoid accidental upgrades
        if (!player.isCrouching()) return InteractionResult.PASS;

        BlockState state = level.getBlockState(pos);
        Block current = state.getBlock();

        // Only upgrade the exact expected source chest
        if (current != fromChest.get()) return InteractionResult.PASS;

        // Reject double chests explicitly (keep logic simple/safe)
        if (state.hasProperty(ChestBlock.TYPE) && state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide) {
            // 1) Read old inventory
            BlockEntity be = level.getBlockEntity(pos);
            net.minecraft.world.Container oldInv = (be instanceof net.minecraft.world.Container c) ? c : null;
            if (oldInv == null) return InteractionResult.FAIL;

            int oldSize = oldInv.getContainerSize();
            NonNullList<ItemStack> saved = NonNullList.withSize(oldSize, ItemStack.EMPTY);
            for (int i = 0; i < oldSize; i++) {
                saved.set(i, oldInv.getItem(i).copy());
            }

            // 2) Clear BEFORE replacement to prevent BaseChestBlock#onRemove from dropping items
            oldInv.clearContent();
            be.setChanged();

            // 3) Build the new state, preserving facing/waterlogged; enforce SINGLE
            BlockState toState = toChest.get().defaultBlockState();

            if (state.hasProperty(ChestBlock.FACING) && toState.hasProperty(ChestBlock.FACING)) {
                toState = toState.setValue(ChestBlock.FACING, state.getValue(ChestBlock.FACING));
            }
            if (state.hasProperty(ChestBlock.WATERLOGGED) && toState.hasProperty(ChestBlock.WATERLOGGED)) {
                toState = toState.setValue(ChestBlock.WATERLOGGED, state.getValue(ChestBlock.WATERLOGGED));
            }
            if (toState.hasProperty(ChestBlock.TYPE)) {
                toState = toState.setValue(ChestBlock.TYPE, ChestType.SINGLE);
            }

            // 4) Replace the block (flag 3 = update neighbors + client)
            level.setBlock(pos, toState, 3);

            // 5) Restore inventory; drop any overflow to the world
            BlockEntity newBe = level.getBlockEntity(pos);
            net.minecraft.world.Container newInv = (newBe instanceof net.minecraft.world.Container c) ? c : null;

            if (newInv != null) {
                int newSize = newInv.getContainerSize();

                int i = 0;
                for (; i < Math.min(oldSize, newSize); i++) {
                    newInv.setItem(i, saved.get(i));
                }
                for (; i < oldSize; i++) {
                    ItemStack rest = saved.get(i);
                    if (!rest.isEmpty()) {
                        Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, rest);
                    }
                }
                newBe.setChanged();
            } else {
                // Fallback: if the new BE could not be resolved, drop everything to avoid item loss
                for (ItemStack s : saved) {
                    if (!s.isEmpty()) {
                        Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, s);
                    }
                }
            }

            // 6) Consume one upgrade item unless the player has instabuild (creative)
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            // Audio feedback
            level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.6F, 1.1F);
        }

        // 7) Server-side: award advancement if it exists
        if (player instanceof ServerPlayer sp) {
            ResourceLocation advId = computeAdvancementId();
            if (advId != null) {
                award(sp, advId);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /**
     * Adds a concise tooltip hinting the required action (sneak + use).
     * Keep text in localization files under {@code tooltip.fullchest.upgrade.sneak_use}.
     */
    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull TooltipContext ctx,
                                List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip." + FullChest.MODID + ".upgrade.sneak_use")
                .withStyle(ChatFormatting.GRAY));
    }

    /** Grant the advancement if it’s registered on the server. */
    private static void award(ServerPlayer sp, ResourceLocation advId) {
        var server = sp.server;
        AdvancementHolder adv = server.getAdvancements().get(advId);
        if (adv != null) {
            sp.getAdvancements().award(adv, "performed");
        }
    }

    /**
     * Derives the advancement id from the item registry path.
     * <p>Example:</p>
     * <pre>
     *   item id: fullchest:stone_chest_to_copper_chest
     *   result: fullchest:upgrade/stone_to_copper
     * </pre>
     *
     * @return advancement id under this mod namespace, or {@code null} if not in our namespace
     */
    @Nullable
    private ResourceLocation computeAdvancementId() {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(this);
        if (!key.getNamespace().equals(FullChest.MODID)) return null;

        String path = key.getPath();              // "stone_chest_to_copper_chest"
        String base = path.replace("_chest", ""); // "stone_to_copper"
        return ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "upgrade/" + base);
    }

    /** Returns the expected source block for this upgrade. */
    public Block getFrom() { return fromChest.get(); }

    /** Returns the target block produced by this upgrade. */
    public Block getTo() { return toChest.get(); }
}
