package com.coolerpromc.fletchingrecipe.compat.rei.fletching;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.Arrays;
import java.util.List;

public record FletchingDisplay(FletchingTableRecipe recipe) implements Display {
    public static CategoryIdentifier<FletchingDisplay> CATEGORY_IDENTIFIER = CategoryIdentifier.of(FletchingRecipe.MODID, "fletching");

    @Override
    public List<EntryIngredient> getInputEntries() {
        EntryIngredient top = EntryIngredients.ofItemStacks(Arrays.stream(recipe.top().getItems()).map(stack -> stack.copyWithCount(recipe.top().count())).toList());
        EntryIngredient middle = EntryIngredients.ofItemStacks(Arrays.stream(recipe.middle().getItems()).map(stack -> stack.copyWithCount(recipe.middle().count())).toList());
        if (recipe.bottom().isPresent()){
            EntryIngredient bottom = EntryIngredients.ofItemStacks(Arrays.stream(recipe.bottom().get().getItems()).map(stack -> stack.copyWithCount(recipe.bottom().get().count())).toList());
            return List.of(top, middle, bottom);
        }
        return List.of(top, middle);
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredients.of(recipe.output()));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CATEGORY_IDENTIFIER;
    }
}
