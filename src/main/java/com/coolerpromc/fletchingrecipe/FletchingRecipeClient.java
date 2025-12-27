package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.LinkedList;

public class FletchingRecipeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(FletchingRecipe.FLETCHING_TABLE_MENU, FletchingTableScreen::new);

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
    }
}
