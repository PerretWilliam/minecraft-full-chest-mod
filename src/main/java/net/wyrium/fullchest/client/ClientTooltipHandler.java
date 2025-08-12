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

@EventBusSubscriber(modid = FullChest.MODID, value = Dist.CLIENT)
public class ClientTooltipHandler {
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent e) {
        ItemStack stack = e.getItemStack();
        if (!(stack.getItem() instanceof BlockItem bi)) return;
        if (!(bi.getBlock() instanceof BaseChestBlock chest)) return;

        ChestSpec s = chest.spec();
        int pageSize = 54;
        int pages = Math.max(1, (int)Math.ceil(s.totalSlots() / (double)pageSize));

        // Capacity
        e.getToolTip().add(Component.translatable("tooltip." + FullChest.MODID + ".capacity",
                        Component.literal(String.valueOf(s.totalSlots())))
                .withStyle(ChatFormatting.GRAY));

        // Pages × pageSize (utile si total > 54)
        if (pages > 1) {
            e.getToolTip().add(Component.translatable("tooltip." + FullChest.MODID + ".pages",
                            Component.literal(String.valueOf(pages)),
                            Component.literal(String.valueOf(pageSize)))
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        String materialKey = s.materialKey(); // e.g. "material.fullchest.copper"
        int materialRgb = s.materialColor(); // e.g. 0xB87333

        if (materialKey != null && !materialKey.isEmpty()) {
            Component matName = Component.translatable(materialKey)
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(materialRgb)));

            // Combine: "Material: " (gray) + matName (colored)
            Component labelAndName = Component.translatable("tooltip." + FullChest.MODID + ".material")
                    .withStyle(ChatFormatting.GRAY)
                    .copy()
                    .append(" ")
                    .append(matName);

            e.getToolTip().add(labelAndName);
        }
    }
}
