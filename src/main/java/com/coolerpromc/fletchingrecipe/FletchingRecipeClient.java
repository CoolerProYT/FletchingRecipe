package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = FletchingRecipe.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = FletchingRecipe.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class FletchingRecipeClient {
    public FletchingRecipeClient(ModContainer container) {

    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(FletchingRecipe.FLETCHING_TABLE_MENU.get(), FletchingTableScreen::new);
    }

    @SubscribeEvent
    public static void onRegisterItemDecorations(RegisterItemDecorationsEvent event) {
        IItemDecorator decorator = (guiGraphics, font, itemStack, i, i1) -> {
            if (itemStack.has(FletchingRecipe.EXPLOSIVE)) {
                Holder<Item> explosiveItemHolder = itemStack.get(FletchingRecipe.EXPLOSIVE);
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0, 0, 200);
                guiGraphics.pose().scale(0.5f, 0.5f, 0.5f);
                guiGraphics.renderFakeItem(explosiveItemHolder.value().getDefaultInstance(), i * 2, i1 * 2);
                guiGraphics.pose().popPose();
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
