package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.recipe.v1.sync.ClientRecipeSynchronizedEvent;
import net.fabricmc.fabric.api.client.rendering.v1.DrawItemStackOverlayCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.item.Item;
import net.minecraft.recipe.PreparedRecipes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.LinkedList;

public class FletchingRecipeClient implements ClientModInitializer {
    public static PreparedRecipes recipeMap = PreparedRecipes.EMPTY;

    @Override
    public void onInitializeClient() {
        HandledScreens.register(FletchingRecipe.FLETCHING_TABLE_MENU, FletchingTableScreen::new);

        ClientRecipeSynchronizedEvent.EVENT.register((client, recipes) -> {
            recipeMap = PreparedRecipes.of(recipes.recipes());
        });

        ClientPlayNetworking.registerGlobalReceiver(ClientBoundConfigSyncPacket.TYPE, ClientBoundConfigSyncPacket::handle);

        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
            LinkedList<Text> tooltips = new LinkedList<>(lines);
            if (stack.contains(FletchingRecipe.EXPLOSIVE)){
                RegistryEntry<Item> explosiveItemHolder = stack.get(FletchingRecipe.EXPLOSIVE);
                tooltips.add(1, Text.translatable("tooltip.fletchingrecipe.explosive_material").append(Text.translatable(explosiveItemHolder.value().getTranslationKey())).formatted(Formatting.DARK_RED));
            }
            lines.clear();
            lines.addAll(tooltips);
        });

        DrawItemStackOverlayCallback.EVENT.register((context, textRenderer, stack, x, y) -> {
            if (stack.contains(FletchingRecipe.EXPLOSIVE)) {
                RegistryEntry<Item> explosiveItemHolder = stack.get(FletchingRecipe.EXPLOSIVE);
                context.getMatrices().pushMatrix();
                context.getMatrices().scale(0.5f);
                context.drawItemWithoutEntity(explosiveItemHolder.value().getDefaultStack(), x * 2, y * 2);
                context.getMatrices().popMatrix();
            }
        });
    }
}
