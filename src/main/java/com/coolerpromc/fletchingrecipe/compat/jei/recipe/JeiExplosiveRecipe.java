package com.coolerpromc.fletchingrecipe.compat.jei.recipe;

import net.minecraft.item.ItemStack;

public record JeiExplosiveRecipe(ItemStack explosiveIngredient, ItemStack arrow, ItemStack output) {
}