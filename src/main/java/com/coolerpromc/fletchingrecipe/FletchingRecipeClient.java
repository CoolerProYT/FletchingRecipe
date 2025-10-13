package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class FletchingRecipeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(FletchingRecipe.FLETCHING_TABLE_MENU, FletchingTableScreen::new);
    }
}
