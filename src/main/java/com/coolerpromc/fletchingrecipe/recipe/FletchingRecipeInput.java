package com.coolerpromc.fletchingrecipe.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record FletchingRecipeInput(ItemStack top, ItemStack middle, ItemStack bottom) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        return switch (i) {
            case 0 -> top;
            case 1 -> middle;
            case 2 -> bottom;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 3;
    }
}
