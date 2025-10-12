package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = FletchingRecipe.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = FletchingRecipe.MODID, value = Dist.CLIENT)
public class FletchingRecipeClient {
    public static RecipeMap recipeMap = RecipeMap.EMPTY;

    public FletchingRecipeClient(ModContainer container) {

    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(FletchingRecipe.FLETCHING_TABLE_MENU.get(), FletchingTableScreen::new);
    }

    @SubscribeEvent
    public static void onRecipesReceived(RecipesReceivedEvent event) {
        recipeMap = event.getRecipeMap();
    }
}
