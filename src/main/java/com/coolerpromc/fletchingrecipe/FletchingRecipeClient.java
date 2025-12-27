package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.IItemDecorator;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = FletchingRecipe.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FletchingRecipeClient {
    @SubscribeEvent
    public static void onRegisterMenuScreens(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(FletchingRecipe.FLETCHING_TABLE_MENU.get(), FletchingTableScreen::new);
        });
    }

    @SubscribeEvent
    public static void onRegisterItemDecorations(RegisterItemDecorationsEvent event) {
        IItemDecorator decorator = (guiGraphics, font, itemStack, i, i1) -> {
            if (itemStack.hasTag() && itemStack.getTag().contains("explosive")) {
                Holder<Item> explosiveItemHolder = BuiltInRegistries.ITEM.get(new ResourceLocation(itemStack.getTag().getString("explosive"))).builtInRegistryHolder();
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
            event.register(com.coolerpromc.arrowplus.item.ModItems.ARROW_PLUS.get(), decorator);
        }
    }
}
