package com.coolerpromc.fletchingrecipe.datagen;

import com.coolerpromc.fletchingrecipe.Constants;
import net.minecraft.core.RegistrySetBuilder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

@EventBusSubscriber(modid = Constants.MODID)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataRegistryEntriesEvent event){
        event.add(ModRecipeProvider.create());
    }
}
