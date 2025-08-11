package net.wyrium.fullchest.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.recipe.ChestForgeRecipe;
import net.wyrium.fullchest.recipe.ModRecipeTypes;
import net.wyrium.fullchest.screen.ChestForgeMenu;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChestForgeBlockEntity extends BlockEntity
        implements MenuProvider, net.minecraft.world.Container {

    public static final int SLOT_FUEL = 0;
    public static final int SLOT_OUT  = 1;
    public static final int SLOT_IN_START = 2;
    public static final int INPUT_COUNT   = 9;
    public static final int CONTAINER_SIZE = SLOT_IN_START + INPUT_COUNT; // = 11

    private int burnTime;        // ticks remaining
    private int burnTimeTotal;   // ticks total (fuel)
    private int progress;        // craft progress
    private int maxProgress = 200; // craft duration for current recipe (ticks)

    private final ContainerData data = new ContainerData() {
        private final int[] vals = new int[4]; // 0=burn, 1=burnTotal, 2=progress, 3=maxProgress
        @Override public int get(int i) { return vals[i]; }
        @Override public void set(int i, int v) { vals[i] = v; }
        @Override public int getCount() { return 4; }
    };
    public ContainerData data() { return data; }

    private final NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);

    public ChestForgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHEST_FORGE_BE.get(), pos, state);
    }

    // Container impl
    @Override public int getContainerSize() { return items.size(); }
    @Override public boolean isEmpty() { for (var s: items) if (!s.isEmpty()) return false; return true; }
    @NotNull @Override public ItemStack getItem(int i) { return items.get(i); }
    @NotNull @Override public ItemStack removeItem(int i, int count) { ItemStack r = ContainerHelper.removeItem(items, i, count); setChanged(); return r; }
    @NotNull @Override public ItemStack removeItemNoUpdate(int i) { return ContainerHelper.takeItem(items, i); }
    @Override public void setItem(int i, @NotNull ItemStack stack) { items.set(i, stack); setChanged(); }
    @Override public void clearContent() { items.clear(); setChanged(); }
    @Override public boolean stillValid(Player p) { return p.distanceToSqr(worldPosition.getCenter()) <= 64.0D; }

    // Save/Load
    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putInt("BurnTime", burnTime);
        tag.putInt("BurnTotal", burnTimeTotal);
        tag.putInt("Progress", progress);
        tag.putInt("MaxProg", maxProgress);
    }

    @Override
    protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, items, registries);
        burnTime = tag.getInt("BurnTime");
        burnTimeTotal = tag.getInt("BurnTotal");
        progress = tag.getInt("Progress");
        maxProgress = tag.contains("MaxProg") ? tag.getInt("MaxProg") : 200;
    }

    // MenuProvider
    @NotNull
    @Override public Component getDisplayName() { return Component.translatable("block." + FullChest.MODID + ".chest_forge"); }
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new ChestForgeMenu(id, inv, this, this, this.data());
    }

    // Exposition
    public boolean isBurning() { return burnTime > 0; }
    public int burnTime() { return burnTime; }
    public int burnTimeTotal() { return burnTimeTotal; }
    public int progress() { return progress; }
    public int maxProgress() { return maxProgress; }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ChestForgeBlockEntity be) {
        if (level.isClientSide) return;

        boolean wasBurning = be.isBurning();
        if (be.burnTime > 0) be.burnTime--;

        Optional<ChestForgeRecipe> match = be.findMatchingRecipe();

        if (match.isPresent() && be.canOutput(match.get())) {
            int recipeTime = Math.max(1, match.get().time());
            if (be.maxProgress != recipeTime) {
                be.maxProgress = recipeTime;
                if (be.progress > be.maxProgress) be.progress = be.maxProgress - 1;
            }

            if (!be.isBurning()) {
                be.tryStartFuel();
            }

            if (be.isBurning()) {
                be.progress++;
                if (be.progress >= be.maxProgress) {
                    be.craft(match.get());
                    be.progress = 0;
                }
            } else {
                be.progress = 0;
            }
        } else {
            be.progress = 0;
            be.maxProgress = 0; // UI arrow empty
        }

        if (wasBurning != be.isBurning()) {
            level.setBlock(pos, state, 3);
        }

        be.data.set(0, be.burnTime);
        be.data.set(1, be.burnTimeTotal);
        be.data.set(2, be.progress);
        be.data.set(3, be.maxProgress());

        be.setChanged();
    }

    private boolean tryStartFuel() {
        ItemStack fuel = items.get(SLOT_FUEL);
        if (fuel.isEmpty() || !fuel.is(Items.LAVA_BUCKET)) return false;

        this.burnTimeTotal = this.burnTime = Math.max(1, this.maxProgress);

        fuel.shrink(1);
        ItemStack remainder = new ItemStack(Items.BUCKET);
        if (fuel.isEmpty()) {
            items.set(SLOT_FUEL, remainder);
        }
        setChanged();
        return true;
    }

    private Optional<ChestForgeRecipe> findMatchingRecipe() {
        if (this.level == null) return Optional.empty();

        List<ItemStack> list = new ArrayList<>(INPUT_COUNT);
        for (int i = 0; i < INPUT_COUNT; i++) list.add(items.get(SLOT_IN_START + i));

        CraftingInput input = CraftingInput.of(3, 3, list);

        return this.level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.CHEST_FORGE_TYPE.get(), input, this.level)
                .map(rh -> rh.value());
    }

    private boolean canOutput(ChestForgeRecipe r) {
        ItemStack out = items.get(SLOT_OUT);
        ItemStack res = r.output();
        if (out.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(out, res)) return false;
        return out.getCount() + res.getCount() <= out.getMaxStackSize();
    }

    private void craft(ChestForgeRecipe r) {
        // Consomme 1 item par case de pattern (classique shaped)
        int w = r.width(), h = r.height();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                var ing = r.ingredients().get(x + y * w);
                if (ing == Ingredient.EMPTY) continue;
                int slot = SLOT_IN_START + (x + y * 3); // grille 3x3
                ItemStack st = items.get(slot);
                if (!st.isEmpty()) st.shrink(1);
            }
        }

        ItemStack res = r.output();
        ItemStack out = items.get(SLOT_OUT);
        if (out.isEmpty()) {
            items.set(SLOT_OUT, res.copy());
        } else {
            out.grow(res.getCount());
        }
        setChanged();
    }
}
