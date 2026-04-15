package com.coolerpromc.fletchingrecipe;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeMap;

import java.util.LinkedList;
import java.util.List;

public class CommonClientClass {
    public static RecipeMap recipeMap = RecipeMap.EMPTY;

    public static void itemTooltipEvent(ItemStack stack, List<Component> lines){
        LinkedList<Component> tooltips = new LinkedList<>(lines);
        if (stack.has(CommonClass.EXPLOSIVE.get())){
            Holder<Item> explosiveItemHolder = stack.get(CommonClass.EXPLOSIVE.get());
            tooltips.add(1, Component.translatable("tooltip.fletchingrecipe.explosive_material").append(Component.translatable(explosiveItemHolder.value().getDescriptionId())).withStyle(ChatFormatting.DARK_RED));
        }
        lines.clear();
        lines.addAll(tooltips);
    }

    public static void itemDecorationEvent(GuiGraphicsExtractor graphics, ItemStack stack, int x, int y){
        if (stack.has(CommonClass.EXPLOSIVE.get())) {
            Holder<Item> explosiveItemHolder = stack.get(CommonClass.EXPLOSIVE.get());
            graphics.pose().pushMatrix();
            graphics.pose().scale(0.5f);
            graphics.fakeItem(explosiveItemHolder.value().getDefaultInstance(), x * 2, y * 2);
            graphics.pose().popMatrix();
        }
    }
}
