package com.coolerpromc.fletchingrecipe.event;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedList;

@Mod.EventBusSubscriber(modid = FletchingRecipe.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GameBusEvent {
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        LinkedList<Component> tooltips = new LinkedList<>(event.getToolTip());
        if (event.getItemStack().hasTag() && event.getItemStack().getTag().contains("explosive")){
            Holder<Item> explosiveItemHolder = BuiltInRegistries.ITEM.get(new ResourceLocation(event.getItemStack().getTag().getString("explosive"))).builtInRegistryHolder();
            tooltips.add(1, Component.translatable("tooltip.fletchingrecipe.explosive_material").append(Component.translatable(explosiveItemHolder.value().getDescriptionId())).withStyle(ChatFormatting.DARK_RED));
        }
        event.getToolTip().clear();
        event.getToolTip().addAll(tooltips);
    }
}
