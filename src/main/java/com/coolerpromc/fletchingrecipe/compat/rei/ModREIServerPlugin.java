package com.coolerpromc.fletchingrecipe.compat.rei;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.compat.rei.fletching.FletchingDisplay;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.context.ContextType;

import java.util.List;

public class ModREIServerPlugin implements REICommonPlugin {
    @Override
    public void registerDisplays(ServerDisplayRegistry registry) {
        registry.beginRecipeFiller(FletchingTableRecipe.class).filterType(FletchingRecipe.FLETCHING_RECIPE_TYPE).fill(fletchingTableRecipeRecipeHolder -> {
            FletchingTableRecipe recipe = fletchingTableRecipeRecipeHolder.value();
            List<EntryIngredient> input;
            EntryIngredient top = EntryIngredients.ofItemStacks(recipe.top().ingredient().toDisplay().getStacks(new ContextParameterMap.Builder().build(new ContextType.Builder().build())).stream().map(stack -> stack.copyWithCount(recipe.top().count())).toList());
            EntryIngredient middle = EntryIngredients.ofItemStacks(recipe.middle().ingredient().toDisplay().getStacks(new ContextParameterMap.Builder().build(new ContextType.Builder().build())).stream().map(stack -> stack.copyWithCount(recipe.middle().count())).toList());
            if (recipe.bottom().isPresent()){
                EntryIngredient bottom = EntryIngredients.ofItemStacks(recipe.bottom().get().ingredient().toDisplay().getStacks(new ContextParameterMap.Builder().build(new ContextType.Builder().build())).stream().map(stack -> stack.copyWithCount(recipe.bottom().get().count())).toList());
                input = List.of(top, middle, bottom);
            }
            else {
                input = List.of(top, middle);
            }

            List<EntryIngredient> output = List.of(EntryIngredients.of(recipe.output()));

            return new FletchingDisplay(input, output);
        });
    }

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(FletchingDisplay.CATEGORY_IDENTIFIER.getIdentifier(), FletchingDisplay.SERIALIZER);
    }
}
