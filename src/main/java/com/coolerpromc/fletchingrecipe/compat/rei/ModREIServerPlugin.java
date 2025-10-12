package com.coolerpromc.fletchingrecipe.compat.rei;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.compat.rei.fletching.FletchingDisplay;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.forge.REIPluginCommon;
import net.minecraft.util.context.ContextMap;

import java.util.List;

@REIPluginCommon
public class ModREIServerPlugin implements REICommonPlugin {
    @Override
    public void registerDisplays(ServerDisplayRegistry registry) {
        registry.beginRecipeFiller(FletchingTableRecipe.class).filterType(FletchingRecipe.FLETCHING_RECIPE_TYPE.get()).fill(fletchingTableRecipeRecipeHolder -> {
            FletchingTableRecipe recipe = fletchingTableRecipeRecipeHolder.value();
            List<EntryIngredient> input;
            EntryIngredient top = EntryIngredients.ofItemStacks(recipe.top().ingredient().display().resolveForStacks(ContextMap.EMPTY).stream().map(stack -> stack.copyWithCount(recipe.top().count())).toList());
            EntryIngredient middle = EntryIngredients.ofItemStacks(recipe.middle().ingredient().display().resolveForStacks(ContextMap.EMPTY).stream().map(stack -> stack.copyWithCount(recipe.middle().count())).toList());
            if (recipe.bottom().isPresent()){
                EntryIngredient bottom = EntryIngredients.ofItemStacks(recipe.bottom().get().ingredient().display().resolveForStacks(ContextMap.EMPTY).stream().map(stack -> stack.copyWithCount(recipe.bottom().get().count())).toList());
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
