package com.coolerpromc.fletchingrecipe.datagen.recipebuilder;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.util.SizedIngredient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class FletchingRecipeBuilder implements RecipeBuilder {
    private SizedIngredient top;
    private SizedIngredient middle;
    private SizedIngredient bottom;
    private ItemStack output;
    private final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>();
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
    public FletchingRecipeBuilder unlockedBy(String s, CriterionTriggerInstance criterion) {
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
    public void save(Consumer<FinishedRecipe> recipeOutput, ResourceLocation resourceLocation) {
        recipeOutput.accept(new Result(top, middle, bottom != null ? Optional.of(bottom) : Optional.empty(), output, resourceLocation));
    }

    public static JsonElement itemToJson(ItemStack stack){
        JsonObject json = new JsonObject();
        json.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
        json.addProperty("count", stack.getCount());
        if (stack.hasTag()) {
            json.add("nbt", NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, stack.getTag()));
        }

        return json;
    }

    public record Result(SizedIngredient top, SizedIngredient middle, Optional<SizedIngredient> bottom, ItemStack output, ResourceLocation id) implements FinishedRecipe{
        @Override
        public void serializeRecipeData(JsonObject jsonObject) {
            jsonObject.addProperty("type", "fletchingrecipe:fletching");

            jsonObject.add("top", top.toJson());
            jsonObject.add("middle", middle.toJson());

            bottom.ifPresent(sizedIngredient -> jsonObject.add("bottom", sizedIngredient.toJson()));

            jsonObject.add("output", itemToJson(output));
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return FletchingRecipe.FLETCHING_RECIPE_SERIALIZER.get();
        }

        @Override
        public @Nullable JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public @Nullable ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
