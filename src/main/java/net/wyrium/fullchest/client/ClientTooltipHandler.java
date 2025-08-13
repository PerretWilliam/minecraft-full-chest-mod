package net.wyrium.fullchest.client;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.wyrium.fullchest.FullChest;
import net.wyrium.fullchest.template.BaseChestBlock;
import net.wyrium.fullchest.template.ChestSpec;

/**
 * Client-side event handler for adding custom tooltips to chest items.
 * <p>
 * This listens to {@link ItemTooltipEvent} and appends extra information such as:
 * - Total capacity (number of slots)
 * - Number of pages if the chest supports multiple inventories
 * - Material name and color
 */
@EventBusSubscriber(modid = FullChest.MODID, value = Dist.CLIENT)
public class ClientTooltipHandler {

    /**
     * Called when the tooltip of an item is being generated.
     * Adds capacity, page count, and material info for custom chests.
     */
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent e) {
        ItemStack stack = e.getItemStack();

        // Only proceed if the item is a BlockItem
        if (!(stack.getItem() instanceof BlockItem bi)) return;

        // Only proceed if the block is a BaseChestBlock
        if (!(bi.getBlock() instanceof BaseChestBlock chest)) return;

        // Retrieve the chest specification (capacity, color, material, etc.)
        ChestSpec s = chest.spec();

        /* ==== Capacity ==== */
        e.getToolTip().add(
                Component.translatable(
                        "tooltip." + FullChest.MODID + ".capacity",
                        Component.literal(String.valueOf(s.totalSlots()))
                ).withStyle(ChatFormatting.GRAY)
        );

        /* ==== Material Info ==== */
        String materialKey = s.materialKey(); // Translation key (e.g. "material.fullchest.copper")
        int materialRgb = s.materialColor(); // RGB color value (e.g. 0xB87333 for copper)

        if (materialKey != null && !materialKey.isEmpty()) {
            // Create the colored material name component
            Component matName = Component.translatable(materialKey)
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(materialRgb)));

            // Combine label ("Material:") in gray with colored material name
            Component labelAndName = Component.translatable("tooltip." + FullChest.MODID + ".material")
                    .withStyle(ChatFormatting.GRAY)
                    .copy()
                    .append(" ")
                    .append(matName);

            // Add to tooltip
            e.getToolTip().add(labelAndName);
        }
    }
}
