package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.recipe.v1.sync.ClientRecipeSynchronizedEvent;
import net.fabricmc.fabric.api.client.rendering.v1.ExtractItemDecorationsCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeMap;
import java.util.LinkedList;

public class FletchingRecipeClient implements ClientModInitializer {
    public static RecipeMap recipeMap = RecipeMap.EMPTY;

    @Override
    public void onInitializeClient() {
        MenuScreens.register(FletchingRecipe.FLETCHING_TABLE_MENU, FletchingTableScreen::new);

        ClientRecipeSynchronizedEvent.EVENT.register((client, recipes) -> {
            recipeMap = RecipeMap.create(recipes.recipes());
        });

        ClientPlayNetworking.registerGlobalReceiver(ClientBoundConfigSyncPacket.TYPE, ClientBoundConfigSyncPacket::handle);

        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
            LinkedList<Component> tooltips = new LinkedList<>(lines);
            if (stack.has(FletchingRecipe.EXPLOSIVE)){
                Holder<Item> explosiveItemHolder = stack.get(FletchingRecipe.EXPLOSIVE);
                tooltips.add(1, Component.translatable("tooltip.fletchingrecipe.explosive_material").append(Component.translatable(explosiveItemHolder.value().getDescriptionId())).withStyle(ChatFormatting.DARK_RED));
            }
            lines.clear();
            lines.addAll(tooltips);
        });

        ExtractItemDecorationsCallback.EVENT.register((context, textRenderer, stack, x, y) -> {
            if (stack.has(FletchingRecipe.EXPLOSIVE)) {
                Holder<Item> explosiveItemHolder = stack.get(FletchingRecipe.EXPLOSIVE);
                context.pose().pushMatrix();
                context.pose().scale(0.5f);
                context.fakeItem(explosiveItemHolder.value().getDefaultInstance(), x * 2, y * 2);
                context.pose().popMatrix();
            }
        });
    }
}
