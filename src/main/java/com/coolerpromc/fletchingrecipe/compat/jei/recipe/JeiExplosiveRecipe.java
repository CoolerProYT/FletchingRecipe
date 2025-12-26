package com.coolerpromc.fletchingrecipe.compat.jei.recipe;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public record JeiExplosiveRecipe(ItemStack explosiveIngredient, ItemStack arrow, ItemStack output) {
}
