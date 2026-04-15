package com.coolerpromc.fletchingrecipe.config;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class NeoForgeFletchingRecipeConfig {
    public static final NeoForgeFletchingRecipeConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public final ModConfigSpec.BooleanValue allowExplosiveCrafting;
    public final ModConfigSpec.IntValue tippedArrowCraftingAmount;
    public final ModConfigSpec.IntValue explosiveArrowCraftingAmount;

    static {
        Pair<NeoForgeFletchingRecipeConfig, ModConfigSpec> config = new ModConfigSpec.Builder().configure(NeoForgeFletchingRecipeConfig::new);

        CONFIG = config.getLeft();
        CONFIG_SPEC = config.getRight();
    }

    public NeoForgeFletchingRecipeConfig(ModConfigSpec.Builder builder){
        builder.push("Fletching Recipe");
        allowExplosiveCrafting = builder.comment("Allow fletching table to craft explosive arrow").define("allowExplosiveCrafting", true);
        tippedArrowCraftingAmount = builder.comment("Amount of tipped arrow it can craft with 1 lingering potion, if json recipe for particular tipped arrow are defined, it will not be controlled by this config")
                .defineInRange("tippedArrowCraftingAmount", 16, 1, 64);
        explosiveArrowCraftingAmount = builder.comment("Amount of explosive arrow it can craft with 1 explosive ingredient")
                .defineInRange("explosiveArrowCraftingAmount", 4, 1, 64);
        builder.pop();
    }

    public static void onConfigLoad(ModConfigEvent.Loading event) {
        syncNeoForgeConfig();
    }

    public static void onConfigReload(ModConfigEvent.Reloading event) {
        syncNeoForgeConfig();
    }

    private static void syncNeoForgeConfig() {
        FletchingRecipeConfig.sync(
                NeoForgeFletchingRecipeConfig.CONFIG.allowExplosiveCrafting.get(),
                NeoForgeFletchingRecipeConfig.CONFIG.tippedArrowCraftingAmount.get(),
                NeoForgeFletchingRecipeConfig.CONFIG.explosiveArrowCraftingAmount.get()
        );
    }
}