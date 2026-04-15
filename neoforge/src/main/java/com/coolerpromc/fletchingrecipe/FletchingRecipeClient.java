package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@Mod(value = Constants.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Constants.MODID, value = Dist.CLIENT)
public class FletchingRecipeClient {
    public FletchingRecipeClient(ModContainer container) {

    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(CommonClass.FLETCHING_TABLE_MENU.get(), FletchingTableScreen::new);
    }

    @SubscribeEvent
    public static void onRecipesReceived(RecipesReceivedEvent event) {
        CommonClientClass.recipeMap = event.getRecipeMap();
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        CommonClientClass.itemTooltipEvent(event.getItemStack(), event.getToolTip());
    }

    @SubscribeEvent
    public static void onRegisterItemDecorations(RegisterItemDecorationsEvent event) {
        IItemDecorator decorator = (guiGraphics, font, itemStack, i, i1) -> {
            CommonClientClass.itemDecorationEvent(guiGraphics, itemStack, i, i1);
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
