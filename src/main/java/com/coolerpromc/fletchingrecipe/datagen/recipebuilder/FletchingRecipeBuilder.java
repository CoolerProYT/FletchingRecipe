package com.coolerpromc.fletchingrecipe.datagen.recipebuilder;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.util.SizedIngredient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtOps;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class FletchingRecipeBuilder implements CraftingRecipeJsonBuilder {
    private SizedIngredient top;
    private SizedIngredient middle;
    private SizedIngredient bottom;
    private ItemStack output;
    private final Map<String, CriterionConditions> criteria = new LinkedHashMap<>();
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
    public FletchingRecipeBuilder criterion(String s, CriterionConditions criterion) {
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
    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        exporter.accept(new Result(top, middle, bottom != null ? Optional.of(bottom) : Optional.empty(), output, recipeId));
    }

    public static JsonElement itemToJson(ItemStack stack){
        JsonObject json = new JsonObject();
        json.addProperty("item", Registries.ITEM.getId(stack.getItem()).toString());
        json.addProperty("count", stack.getCount());
        if (stack.hasNbt()) {
            json.add("nbt", NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, stack.getNbt()));
        }

        return json;
    }

    public record Result(SizedIngredient top, SizedIngredient middle, Optional<SizedIngredient> bottom, ItemStack output, Identifier id) implements RecipeJsonProvider{

        @Override
        public void serialize(JsonObject jsonObject) {
            jsonObject.addProperty("type", "fletchingrecipe:fletching");

            jsonObject.add("top", top.toJson());
            jsonObject.add("middle", middle.toJson());

            bottom.ifPresent(sizedIngredient -> jsonObject.add("bottom", sizedIngredient.toJson()));

            jsonObject.add("output", itemToJson(output));
        }

        @Override
        public Identifier getRecipeId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return FletchingRecipe.FLETCHING_RECIPE_SERIALIZER;
        }

        @Override
        public @Nullable JsonObject toAdvancementJson() {
            return null;
        }

        @Override
        public @Nullable Identifier getAdvancementId() {
            return null;
        }
    }
}
