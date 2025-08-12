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
 * An item that upgrades a chest block in-place (shift + right-click),
 * preserving inventory and key block properties (facing, waterlogged, single type).
 * <p>
 * Notes:
 * - Currently supports SINGLE chests only (keeps behavior simple).
 * - Old inventory is cleared before block replacement to prevent onRemove() from dropping items.
 * - Any overflow (if target chest has fewer slots) will be dropped at the block position.
 */
public class ChestUpgradeItem extends Item {
    private final Supplier<Block> fromChest; // Required source chest block
    private final Supplier<Block> toChest;   // Target chest block after upgrade

    public ChestUpgradeItem(Properties props, Supplier<Block> fromChest, Supplier<Block> toChest) {
        super(props);
        this.fromChest = fromChest;
        this.toChest = toChest;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        Player player = ctx.getPlayer();
        ItemStack stack = ctx.getItemInHand();

        if (player == null) return InteractionResult.PASS;

        // Require shift-right-click (sneaking) to avoid accidental upgrades.
        if (!player.isCrouching()) return InteractionResult.PASS;

        BlockState state = level.getBlockState(pos);
        Block current = state.getBlock();

        // Only act on the exact source chest block we expect.
        if (current != fromChest.get()) return InteractionResult.PASS;

        // Support SINGLE chests only for now (explicitly reject double chests).
        if (state.hasProperty(ChestBlock.TYPE) && state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide) {
            // --- 1) Read and cache the old inventory contents.
            BlockEntity be = level.getBlockEntity(pos);
            net.minecraft.world.Container oldInv = (be instanceof net.minecraft.world.Container c) ? c : null;
            if (oldInv == null) return InteractionResult.FAIL;

            int oldSize = oldInv.getContainerSize();
            NonNullList<ItemStack> saved = NonNullList.withSize(oldSize, ItemStack.EMPTY);
            for (int i = 0; i < oldSize; i++) {
                saved.set(i, oldInv.getItem(i).copy());
            }

            // --- 2) Clear the old inventory BEFORE replacing the block.
            // This avoids BaseChestBlock#onRemove() dropping items to the ground.
            oldInv.clearContent();
            be.setChanged();

            // --- 3) Build the new block state while preserving useful properties.
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

            // --- 4) Replace the block (flag 3 updates neighbors and rendering).
            level.setBlock(pos, toState, 3);

            // --- 5) Restore items into the new inventory; drop overflow if needed.
            BlockEntity newBe = level.getBlockEntity(pos);
            net.minecraft.world.Container newInv = (newBe instanceof net.minecraft.world.Container c) ? c : null;

            if (newInv != null) {
                int newSize = newInv.getContainerSize();

                int i = 0;
                // Fill as much as the new inventory allows.
                for (; i < Math.min(oldSize, newSize); i++) {
                    newInv.setItem(i, saved.get(i));
                }
                // If the new chest is smaller (unlikely), drop remaining items safely.
                for (; i < oldSize; i++) {
                    ItemStack rest = saved.get(i);
                    if (!rest.isEmpty()) {
                        Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, rest);
                    }
                }
                newBe.setChanged();
            } else {
                // Fallback: if the new BE is missing for any reason, don't lose items.
                for (ItemStack s : saved) {
                    if (!s.isEmpty()) {
                        Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, s);
                    }
                }
            }

            // --- 6) Consume one upgrade item unless the player is in creative, then play a feedback sound.
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.6F, 1.1F);
        }

        // --- 7) Award the player an advancement for upgrading a chest.
        if (player instanceof ServerPlayer sp) {
            ResourceLocation advId = computeAdvancementId();
            if (advId != null) {
                award(sp, advId);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull TooltipContext ctx,
                                List<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip." + FullChest.MODID + ".upgrade.sneak_use")
                .withStyle(ChatFormatting.GRAY));
    }

    private static void award(ServerPlayer sp, ResourceLocation advId) {
        var server = sp.server;
        AdvancementHolder adv = server.getAdvancements().get(advId);
        if (adv != null) {
            sp.getAdvancements().award(adv, "performed");
        }
    }


    /**
     * Compute the advancement id from this item's registry key.
     * Example: "stone_chest_to_copper_chest" -> "fullchest:upgrade/stone_to_copper"
     */
    @Nullable
    private ResourceLocation computeAdvancementId() {

        ResourceLocation key = BuiltInRegistries.ITEM.getKey(this);

        if (!key.getNamespace().equals(FullChest.MODID)) return null;

        String path = key.getPath();           // "stone_chest_to_copper_chest"
        String base = path.replace("_chest", ""); // "stone_to_copper"
        return ResourceLocation.fromNamespaceAndPath(FullChest.MODID, "upgrade/" + base);
    }

    public Block getFrom() { return fromChest.get(); }
    public Block getTo() { return toChest.get(); }
}
