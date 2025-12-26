package com.coolerpromc.fletchingrecipe.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class FletchingRecipeConfig {
    public static final FletchingRecipeConfig CONFIG;
    public static final ForgeConfigSpec CONFIG_SPEC;

    public final ForgeConfigSpec.BooleanValue allowExplosiveCrafting;
    public final ForgeConfigSpec.IntValue tippedArrowCraftingAmount;
    public final ForgeConfigSpec.IntValue explosiveArrowCraftingAmount;

    static {
        Pair<FletchingRecipeConfig, ForgeConfigSpec> config = new ForgeConfigSpec.Builder().configure(FletchingRecipeConfig::new);

        CONFIG = config.getLeft();
        CONFIG_SPEC = config.getRight();
    }

    public FletchingRecipeConfig(ForgeConfigSpec.Builder builder){
        builder.push("Fletching Recipe");
        allowExplosiveCrafting = builder.comment("Allow fletching table to craft explosive arrow").define("allowExplosiveCrafting", true);
        tippedArrowCraftingAmount = builder.comment("Amount of tipped arrow it can craft with 1 lingering potion, if json recipe for particular tipped arrow are defined, it will not be controlled by this config")
                .defineInRange("tippedArrowCraftingAmount", 16, 1, 64);
        explosiveArrowCraftingAmount = builder.comment("Amount of explosive arrow it can craft with 1 explosive ingredient")
                .defineInRange("explosiveArrowCraftingAmount", 4, 1, 64);
        builder.pop();
    }
}