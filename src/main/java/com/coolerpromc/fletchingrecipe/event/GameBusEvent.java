package com.coolerpromc.fletchingrecipe.event;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.LinkedList;

@EventBusSubscriber(modid = FletchingRecipe.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class GameBusEvent {
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        LinkedList<Component> tooltips = new LinkedList<>(event.getToolTip());
        if (event.getItemStack().has(FletchingRecipe.EXPLOSIVE)){
            Holder<Item> explosiveItemHolder = event.getItemStack().get(FletchingRecipe.EXPLOSIVE);
            tooltips.add(1, Component.translatable("tooltip.fletchingrecipe.explosive_material").append(Component.translatable(explosiveItemHolder.value().getDescriptionId())).withStyle(ChatFormatting.DARK_RED));
        }
        event.getToolTip().clear();
        event.getToolTip().addAll(tooltips);
    }
}
