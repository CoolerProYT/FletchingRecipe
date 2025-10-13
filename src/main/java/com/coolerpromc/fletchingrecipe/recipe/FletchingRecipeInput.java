package com.coolerpromc.fletchingrecipe.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record FletchingRecipeInput(ItemStack top, ItemStack middle, ItemStack bottom) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int i) {
        return switch (i) {
            case 0 -> top;
            case 1 -> middle;
            case 2 -> bottom;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int getSize() {
        return 3;
    }
}
