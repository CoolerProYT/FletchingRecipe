package com.coolerpromc.fletchingrecipe.compat.jei.recipe;

import java.util.List;
import net.minecraft.world.item.ItemStack;

public record JeiFletchingRecipe(List<ItemStack> top, List<ItemStack> middle, List<ItemStack> bottom, ItemStack output) {
}