
package com.coolerpromc.fletchingrecipe.compat.rei.explosive;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public record ExplosiveDisplay(List<EntryIngredient> input, List<EntryIngredient> output) implements Display {
    public static final CategoryIdentifier<ExplosiveDisplay> CATEGORY_IDENTIFIER = CategoryIdentifier.of(FletchingRecipe.MODID, "explosive");

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
    public Optional<ResourceLocation> getDisplayLocation() {
        return Optional.empty();
    }
}