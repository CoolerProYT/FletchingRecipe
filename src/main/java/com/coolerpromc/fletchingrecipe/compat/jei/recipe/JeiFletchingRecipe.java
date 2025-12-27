package com.coolerpromc.fletchingrecipe.compat.jei.recipe;

import net.minecraft.item.ItemStack;

import java.util.List;

public record JeiFletchingRecipe(List<ItemStack> top, List<ItemStack> middle, List<ItemStack> bottom, ItemStack output) {
}