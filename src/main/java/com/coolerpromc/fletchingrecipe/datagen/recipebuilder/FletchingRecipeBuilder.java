package com.coolerpromc.fletchingrecipe.datagen.recipebuilder;

import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.util.SizedIngredient;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class FletchingRecipeBuilder implements CraftingRecipeJsonBuilder {
    private SizedIngredient top;
    private SizedIngredient middle;
    private SizedIngredient bottom;
    private ItemStack output;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;

    public static FletchingRecipeBuilder builder(){
        return new FletchingRecipeBuilder();
    }

    private FletchingRecipeBuilder(){}

    public FletchingRecipeBuilder top(SizedIngredient top){
        this.top = top;
        return this;
    }

    public FletchingRecipeBuilder middle(SizedIngredient middle){
        this.middle = middle;
        return this;
    }

    public FletchingRecipeBuilder bottom(SizedIngredient bottom){
        this.bottom = bottom;
        return this;
    }

    public FletchingRecipeBuilder output(ItemStack output){
        this.output = output;
        return this;
    }

    @Override
    public FletchingRecipeBuilder criterion(String s, AdvancementCriterion<?> criterion) {
        this.criteria.put(s, criterion);
        return this;
    }

    @Override
    public FletchingRecipeBuilder group(@Nullable String s) {
        this.group = s;
        return this;
    }

    @Override
    public Item getOutputItem() {
        return output.getItem();
    }

    @Override
    public void offerTo(RecipeExporter exporter, RegistryKey<Recipe<?>> recipeKey) {
        Advancement.Builder advancement = exporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey))
                .rewards(AdvancementRewards.Builder.recipe(recipeKey))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        this.criteria.forEach(advancement::criterion);

        FletchingTableRecipe recipe = new FletchingTableRecipe(top, middle, bottom != null ? Optional.of(bottom) : Optional.empty(), output);
        exporter.accept(recipeKey, recipe, advancement.build(recipeKey.getValue()));
    }
}
