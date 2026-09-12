package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import com.coolerpromc.fletchingrecipe.platform.util.FabricPayloadContext;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.recipe.v1.sync.ClientRecipeSynchronizedEvent;
import net.fabricmc.fabric.api.client.rendering.v1.ExtractItemDecorationsCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;

import com.mojang.serialization.Lifecycle;

import java.util.LinkedList;

public class FletchingRecipeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(CommonClass.FLETCHING_TABLE_MENU.get(), FletchingTableScreen::new);

        ClientRecipeSynchronizedEvent.EVENT.register((client, recipes) -> {
            MappedRegistry<Recipe<?>> synchronizedRecipes = new MappedRegistry<>(Registries.RECIPE, Lifecycle.stable());
            for (RecipeHolder<?> recipe : recipes.recipes()) {
                synchronizedRecipes.register(recipe.id(), recipe.value(), RegistrationInfo.BUILT_IN);
            }
            CommonClientClass.recipeMap = RecipeMap.create(synchronizedRecipes.freeze());
        });

        ClientPlayNetworking.registerGlobalReceiver(ClientBoundConfigSyncPacket.TYPE, (payload, context) -> ClientBoundConfigSyncPacket.handle(payload, new FabricPayloadContext(context)));
        ItemTooltipCallback.EVENT.register((stack, _, _, lines) -> CommonClientClass.itemTooltipEvent(stack, lines));
        ExtractItemDecorationsCallback.EVENT.register((context, _, stack, x, y) -> CommonClientClass.itemDecorationEvent(context, stack, x, y));
    }
}
