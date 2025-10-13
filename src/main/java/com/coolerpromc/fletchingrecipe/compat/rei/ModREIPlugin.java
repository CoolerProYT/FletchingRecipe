package com.coolerpromc.fletchingrecipe.compat.rei;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.compat.rei.fletching.FletchingCategory;
import com.coolerpromc.fletchingrecipe.compat.rei.fletching.FletchingDisplay;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.block.Blocks;

import java.util.Arrays;
import java.util.List;

public class ModREIPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new FletchingCategory(), config -> config.addWorkstations(EntryStacks.of(Blocks.FLETCHING_TABLE)));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(screen -> new Rectangle(((screen.width - 176) / 2) + 89, ((screen.height - 166) / 2) + 34, 22, 16), FletchingTableScreen.class, FletchingDisplay.CATEGORY_IDENTIFIER);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(FletchingTableRecipe.class, FletchingRecipe.FLETCHING_RECIPE_TYPE, recipe -> {
            List<EntryIngredient> input;
            EntryIngredient top = EntryIngredients.ofItemStacks(Arrays.stream(recipe.top().ingredient().getMatchingStacks()).map(stack -> stack.copyWithCount(recipe.top().count())).toList());
            EntryIngredient middle = EntryIngredients.ofItemStacks(Arrays.stream(recipe.middle().ingredient().getMatchingStacks()).map(stack -> stack.copyWithCount(recipe.middle().count())).toList());
            if (recipe.bottom().isPresent()){
                EntryIngredient bottom = EntryIngredients.ofItemStacks(Arrays.stream(recipe.bottom().get().ingredient().getMatchingStacks()).map(stack -> stack.copyWithCount(recipe.bottom().get().count())).toList());
                input = List.of(top, middle, bottom);
            }
            else {
                input = List.of(top, middle);
            }

            List<EntryIngredient> output = List.of(EntryIngredients.of(recipe.output()));

            return new FletchingDisplay(input, output);
        });
    }
}