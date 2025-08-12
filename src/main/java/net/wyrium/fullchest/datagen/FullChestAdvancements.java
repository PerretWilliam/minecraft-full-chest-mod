package net.wyrium.fullchest.datagen;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.item.ModItems;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class FullChestAdvancements implements AdvancementSubProvider {

    @Override
    public void generate(HolderLookup.@NotNull Provider provider, Consumer<AdvancementHolder> consumer) {

        // ROOT
        Advancement.Builder forgeBuilder = Advancement.Builder.advancement()
                .display(
                        ModItems.CHEST_FORGE.get(),
                        Component.translatable("advancement.fullchest.forge.title"),
                        Component.translatable("advancement.fullchest.forge.desc"),
                        ResourceLocation.fromNamespaceAndPath("minecraft","textures/gui/advancements/backgrounds/stone.png"),
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                ).addCriterion("has_forge", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.CHEST_FORGE.get()));

        AdvancementHolder FORGE = forgeBuilder.build(id("forge"));
        consumer.accept(FORGE);

        AdvancementHolder DIRT = chestTier("dirt", ModItems.DIRT_CHEST.get(), FORGE, AdvancementType.TASK, consumer);
        AdvancementHolder STONE = chestTier("stone", ModItems.STONE_CHEST.get(), DIRT, AdvancementType.TASK, consumer);
        AdvancementHolder COPPER = chestTier("copper", ModItems.COPPER_CHEST.get(), STONE, AdvancementType.TASK, consumer);
        AdvancementHolder IRON = chestTier("iron", ModItems.IRON_CHEST.get(), COPPER, AdvancementType.TASK, consumer);
        AdvancementHolder GOLD = chestTier("gold", ModItems.GOLD_CHEST.get(), IRON, AdvancementType.TASK, consumer);
        AdvancementHolder DIAMOND = chestTier("diamond", ModItems.DIAMOND_CHEST.get(), GOLD, AdvancementType.GOAL, consumer);
        AdvancementHolder EMERALD = chestTier("emerald", ModItems.EMERALD_CHEST.get(), DIAMOND, AdvancementType.GOAL, consumer);
        AdvancementHolder OBSIDIAN = chestTier("obsidian", ModItems.OBSIDIAN_CHEST.get(), EMERALD, AdvancementType.GOAL, consumer);
        AdvancementHolder NETHERITE = chestTier("netherite", ModItems.NETHERITE_CHEST.get(), OBSIDIAN, AdvancementType.CHALLENGE, consumer);

        AdvancementHolder ALL_CHESTS = Advancement.Builder.advancement()
                .parent(NETHERITE)
                .display(
                        ModItems.STONE_CHEST.get(),
                        Component.translatable("advancement.fullchest.all_chests.title"),
                        Component.translatable("advancement.fullchest.all_chests.desc"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion("has_dirt", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.DIRT_CHEST.get()))
                .addCriterion("has_stone", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.STONE_CHEST.get()))
                .addCriterion("has_copper", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.COPPER_CHEST.get()))
                .addCriterion("has_iron", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.IRON_CHEST.get()))
                .addCriterion("has_gold", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.GOLD_CHEST.get()))
                .addCriterion("has_diamond", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.DIAMOND_CHEST.get()))
                .addCriterion("has_emerald", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.EMERALD_CHEST.get()))
                .addCriterion("has_obsidian", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.OBSIDIAN_CHEST.get()))
                .addCriterion("has_netherite", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.NETHERITE_CHEST.get()))
                .build(id("all_chests"));

        consumer.accept(ALL_CHESTS);

        /* UPGRADE ADVANCEMENTS */
        AdvancementHolder UPG_DIRT_TO_STONE = upgradeAdvManualAward(
                "upgrade/dirt_to_stone",
                ModItems.DIRT_TO_STONE_UPGRADE.get(),
                DIRT,
                AdvancementType.TASK,
                consumer);

        AdvancementHolder UPG_STONE_TO_COPPER = upgradeAdvManualAward(
                "upgrade/stone_to_copper",
                ModItems.STONE_TO_COPPER_UPGRADE.get(),
                UPG_DIRT_TO_STONE,
                AdvancementType.TASK,
                consumer);
        AdvancementHolder UPG_COPPER_TO_IRON = upgradeAdvManualAward(
                "upgrade/copper_to_iron",
                ModItems.COPPER_TO_IRON_UPGRADE.get(),
                UPG_STONE_TO_COPPER,
                AdvancementType.TASK,
                consumer);
        AdvancementHolder UPG_IRON_TO_GOLD = upgradeAdvManualAward(
                "upgrade/iron_to_gold",
                ModItems.IRON_TO_GOLD_UPGRADE.get(),
                UPG_COPPER_TO_IRON,
                AdvancementType.TASK,
                consumer);
        AdvancementHolder UPG_GOLD_TO_DIAMOND = upgradeAdvManualAward(
                "upgrade/gold_to_diamond",
                ModItems.GOLD_TO_DIAMOND_UPGRADE.get(),
                UPG_IRON_TO_GOLD,
                AdvancementType.GOAL,
                consumer);
        AdvancementHolder UPG_DIAMOND_TO_EMERALD = upgradeAdvManualAward(
                "upgrade/diamond_to_emerald",
                ModItems.DIAMOND_TO_EMERALD_UPGRADE.get(),
                UPG_GOLD_TO_DIAMOND,
                AdvancementType.GOAL,
                consumer);
        AdvancementHolder UPG_EMERALD_TO_OBSIDIAN = upgradeAdvManualAward(
                "upgrade/emerald_to_obsidian",
                ModItems.EMERALD_TO_OBSIDIAN_UPGRADE.get(),
                UPG_DIAMOND_TO_EMERALD,
                AdvancementType.GOAL,
                consumer);
        AdvancementHolder UPG_OBSIDIAN_TO_NETHERITE = upgradeAdvManualAward(
                "upgrade/obsidian_to_netherite",
                ModItems.OBSIDIAN_TO_NETHERITE_UPGRADE.get(),
                UPG_EMERALD_TO_OBSIDIAN,
                AdvancementType.CHALLENGE,
                consumer);

        AdvancementHolder ALL_CHESTS_UPGRADE = Advancement.Builder.advancement()
                .parent(UPG_OBSIDIAN_TO_NETHERITE)
                .display(
                        ModItems.BASE_CHEST_UPGRADE.get(),
                        Component.translatable("advancement." + FullChest.MODID + ".all_chests_upgrade.title"),
                        Component.translatable("advancement." + FullChest.MODID + ".all_chests_upgrade.desc"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion("has_obsidian_to_netherite",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.OBSIDIAN_TO_NETHERITE_UPGRADE.get()))
                .build(id("all_chests_upgrade"));

        consumer.accept(ALL_CHESTS_UPGRADE);
    }


    private static AdvancementHolder chestTier(
            String name,
            ItemLike icon,
            AdvancementHolder parent,
            AdvancementType frame,
            java.util.function.Consumer<AdvancementHolder> consumer
    ) {
        var builder = Advancement.Builder.advancement()
                .parent(parent)
                .display(
                        icon,
                        Component.translatable("advancement." + FullChest.MODID + ".chest." + name + ".title"),
                        Component.translatable("advancement." + FullChest.MODID + ".chest." + name + ".desc"),
                        null,
                        frame,
                        true,
                        false,
                        false
                )
                .addCriterion(
                        "has_" + name,
                        InventoryChangeTrigger.TriggerInstance.hasItems(icon)
                );

        var holder = builder.build(id("chests/" + name));
        consumer.accept(holder);
        return holder;
    }

    private static AdvancementHolder upgradeAdvManualAward(
            String idPath,
            Item icon,
            AdvancementHolder parent,
            AdvancementType frame,
            java.util.function.Consumer<AdvancementHolder> out
    ) {
        Advancement.Builder b = Advancement.Builder.advancement()
                .parent(parent)
                .display(
                        icon,
                        Component.translatable("advancement." + FullChest.MODID + "." + idPath.replace('/', '.') + ".title"),
                        Component.translatable("advancement." + FullChest.MODID + "." + idPath.replace('/', '.') + ".desc"),
                        null, frame,
                        true,
                        true,
                        false
                )
                // "Impossible" in-game: inventory never contains AIR
                .addCriterion("performed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.AIR));

        AdvancementHolder holder = b.build(id(idPath));
        out.accept(holder);
        return holder;
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(FullChest.MODID, path);
    }
}
