package com.coolerpromc.fletchingrecipe.compat.jei.subtype;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;

public class ExplosiveSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
    public static final ExplosiveSubtypeInterpreter INSTANCE = new ExplosiveSubtypeInterpreter();

    @Override
    public String apply(ItemStack stack, UidContext uidContext) {
        if (stack.hasTag() && stack.getTag().contains("explosive")){
            return stack.getTag().getString("explosive");
        }
        return "";
    }
}
