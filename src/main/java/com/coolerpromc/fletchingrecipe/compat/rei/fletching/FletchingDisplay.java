
package com.coolerpromc.fletchingrecipe.compat.rei.fletching;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public record FletchingDisplay(List<EntryIngredient> input, List<EntryIngredient> output) implements Display {
    public static final CategoryIdentifier<FletchingDisplay> CATEGORY_IDENTIFIER = CategoryIdentifier.of(FletchingRecipe.MOD_ID, "fletching");

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

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.empty();
    }
}