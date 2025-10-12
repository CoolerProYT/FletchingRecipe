package com.coolerpromc.fletchingrecipe.datagen.recipebuilder;

import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class FletchingRecipeBuilder implements RecipeBuilder {
    private SizedIngredient top;
    private SizedIngredient middle;
    private SizedIngredient bottom;
    private ItemStack output;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
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
    public FletchingRecipeBuilder unlockedBy(String s, Criterion<?> criterion) {
        this.criteria.put(s, criterion);
        return this;
    }

    @Override
    public FletchingRecipeBuilder group(@Nullable String s) {
        this.group = s;
        return this;
    }

    @Override
    public Item getResult() {
        return output.getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> resourceKey) {
        Advancement.Builder advancement = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
                .rewards(AdvancementRewards.Builder.recipe(resourceKey))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);

        FletchingTableRecipe recipe = new FletchingTableRecipe(top, middle, bottom != null ? Optional.of(bottom) : Optional.empty(), output);
        recipeOutput.accept(resourceKey, recipe, advancement.build(resourceKey.location()));
    }
}
