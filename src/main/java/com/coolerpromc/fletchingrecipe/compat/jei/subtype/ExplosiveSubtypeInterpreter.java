package com.coolerpromc.fletchingrecipe.compat.jei.subtype;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ExplosiveSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    public static final ExplosiveSubtypeInterpreter INSTANCE = new ExplosiveSubtypeInterpreter();

    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
        return ingredient.get(FletchingRecipe.EXPLOSIVE);
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        if (getSubtypeData(ingredient, context) != null){
            Holder<Item> holder = ingredient.get(FletchingRecipe.EXPLOSIVE);
            return holder.unwrapKey().get().location().toString();
        }
        return "";
    }
}
