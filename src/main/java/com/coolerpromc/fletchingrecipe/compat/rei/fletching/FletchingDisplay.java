package com.coolerpromc.fletchingrecipe.compat.rei.fletching;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.Arrays;
import java.util.List;

public class FletchingDisplay implements Display {
    public static CategoryIdentifier<FletchingDisplay> CATEGORY_IDENTIFIER = CategoryIdentifier.of(FletchingRecipe.MODID, "fletching");

    private final List<EntryIngredient> input;
    private final List<EntryIngredient> output;

    public FletchingDisplay(FletchingTableRecipe recipe){
        EntryIngredient top = EntryIngredients.ofItemStacks(Arrays.stream(recipe.top().getItems()).map(stack -> stack.copyWithCount(recipe.top().count())).toList());
        EntryIngredient middle = EntryIngredients.ofItemStacks(Arrays.stream(recipe.middle().getItems()).map(stack -> stack.copyWithCount(recipe.middle().count())).toList());
        if (recipe.bottom().isPresent()){
            EntryIngredient bottom = EntryIngredients.ofItemStacks(Arrays.stream(recipe.bottom().get().getItems()).map(stack -> stack.copyWithCount(recipe.bottom().get().count())).toList());
            input = List.of(top, middle, bottom);
        }
        else{
            input = List.of(top, middle);
        }
        output = List.of(EntryIngredients.of(recipe.output()));
    }

    public FletchingDisplay(List<EntryIngredient> input, List<EntryIngredient> output){
        this.input = input;
        this.output = output;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return input;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return output;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CATEGORY_IDENTIFIER;
    }
}
