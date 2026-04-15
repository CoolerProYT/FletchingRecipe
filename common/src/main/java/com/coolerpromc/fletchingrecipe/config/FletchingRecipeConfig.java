package com.coolerpromc.fletchingrecipe.config;

public class FletchingRecipeConfig {
    private static boolean allowExplosiveCrafting = true;
    private static int tippedArrowCraftingAmount = 16;
    private static int explosiveArrowCraftingAmount = 4;

    public static void sync(boolean allowExplosive, int tippedAmount, int explosiveAmount) {
        allowExplosiveCrafting = allowExplosive;
        tippedArrowCraftingAmount = tippedAmount;
        explosiveArrowCraftingAmount = explosiveAmount;
    }

    public static boolean allowExplosiveCrafting() {
        return allowExplosiveCrafting;
    }

    public static int tippedArrowCraftingAmount() {
        return tippedArrowCraftingAmount;
    }

    public static int explosiveArrowCraftingAmount() {
        return explosiveArrowCraftingAmount;
    }
}