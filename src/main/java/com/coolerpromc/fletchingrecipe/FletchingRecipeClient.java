package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeMap;
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

import java.util.LinkedList;

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

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        LinkedList<Component> tooltips = new LinkedList<>(event.getToolTip());
        if (event.getItemStack().has(FletchingRecipe.EXPLOSIVE)){
            Holder<Item> explosiveItemHolder = event.getItemStack().get(FletchingRecipe.EXPLOSIVE);
            tooltips.add(1, Component.translatable("tooltip.fletchingrecipe.explosive_material").append(Component.translatable(explosiveItemHolder.value().getDescriptionId())).withStyle(ChatFormatting.DARK_RED));
        }
        event.getToolTip().clear();
        event.getToolTip().addAll(tooltips);
    }

    @SubscribeEvent
    public static void onRegisterItemDecorations(RegisterItemDecorationsEvent event) {
        IItemDecorator decorator = (guiGraphics, font, itemStack, i, i1) -> {
            if (itemStack.has(FletchingRecipe.EXPLOSIVE)) {
                Holder<Item> explosiveItemHolder = itemStack.get(FletchingRecipe.EXPLOSIVE);
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().scale(0.5f);
                guiGraphics.fakeItem(explosiveItemHolder.value().getDefaultInstance(), i * 2, i1 * 2);
                guiGraphics.pose().popMatrix();
            }
            return true;
        };

        event.register(Items.ARROW, decorator);
        event.register(Items.SPECTRAL_ARROW, decorator);
        event.register(Items.TIPPED_ARROW, decorator);
        if (ModList.get().isLoaded("arrowplus")){
            event.register(com.coolerpromc.arrowplus.item.ModItems.ARROW_PLUS, decorator);
        }
    }
}
