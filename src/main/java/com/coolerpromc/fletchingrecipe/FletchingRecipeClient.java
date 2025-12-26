package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.IItemDecorator;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.LinkedList;

@Mod.EventBusSubscriber(modid = FletchingRecipe.MODID, value = Dist.CLIENT)
public class FletchingRecipeClient {
    @SubscribeEvent
    public static void onRegisterMenuScreens(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(FletchingRecipe.FLETCHING_TABLE_MENU.get(), FletchingTableScreen::new));
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        LinkedList<Component> tooltips = new LinkedList<>(event.getToolTip());
        if (event.getItemStack().has(FletchingRecipe.EXPLOSIVE.get())){
            Holder<Item> explosiveItemHolder = event.getItemStack().get(FletchingRecipe.EXPLOSIVE.get());
            tooltips.add(1, Component.translatable("tooltip.fletchingrecipe.explosive_material").append(Component.translatable(explosiveItemHolder.value().getDescriptionId())).withStyle(ChatFormatting.DARK_RED));
        }
        event.getToolTip().clear();
        event.getToolTip().addAll(tooltips);
    }

    @SubscribeEvent
    public static void onRegisterItemDecorations(RegisterItemDecorationsEvent event) {
        IItemDecorator decorator = (guiGraphics, font, itemStack, i, i1) -> {
            if (itemStack.has(FletchingRecipe.EXPLOSIVE.get())) {
                Holder<Item> explosiveItemHolder = itemStack.get(FletchingRecipe.EXPLOSIVE.get());
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().scale(0.5f);
                guiGraphics.renderFakeItem(explosiveItemHolder.value().getDefaultInstance(), i * 2, i1 * 2);
                guiGraphics.pose().popMatrix();
            }
            return true;
        };

        event.register(Items.ARROW, decorator);
        event.register(Items.SPECTRAL_ARROW, decorator);
        event.register(Items.TIPPED_ARROW, decorator);
        if (ModList.get().isLoaded("arrowplus")){
            event.register(com.coolerpromc.arrowplus.item.ModItems.ARROW_PLUS.get(), decorator);
        }
    }
}
