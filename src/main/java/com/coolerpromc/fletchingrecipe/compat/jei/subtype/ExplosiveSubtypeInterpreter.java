package com.coolerpromc.fletchingrecipe.compat.jei.subtype;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
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
            RegistryEntry<Item> holder = ingredient.get(FletchingRecipe.EXPLOSIVE);
            return holder.getKey().get().getValue().toString();
        }
        return "";
    }
}