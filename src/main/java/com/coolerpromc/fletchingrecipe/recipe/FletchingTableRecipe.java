package com.coolerpromc.fletchingrecipe.recipe;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.util.SizedIngredient;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record FletchingTableRecipe(SizedIngredient top, SizedIngredient middle, Optional<SizedIngredient> bottom, ItemStack output, ResourceLocation id) implements Recipe<FletchingRecipeInput> {
    @Override
    public boolean matches(FletchingRecipeInput fletchingRecipeInput, Level level) {
        return bottom.map(sizedIngredient -> top.test(fletchingRecipeInput.top()) && middle.test(fletchingRecipeInput.middle()) && sizedIngredient.test(fletchingRecipeInput.bottom()))
                .orElseGet(() -> top.test(fletchingRecipeInput.top()) && middle.test(fletchingRecipeInput.middle()) && fletchingRecipeInput.bottom().isEmpty());
    }

    @Override
    public ItemStack assemble(FletchingRecipeInput fletchingRecipeInput, RegistryAccess registryAccess) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<? extends Recipe<FletchingRecipeInput>> getSerializer() {
        return FletchingRecipe.FLETCHING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<FletchingRecipeInput>> getType() {
        return FletchingRecipe.FLETCHING_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<FletchingTableRecipe>{
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public FletchingTableRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            SizedIngredient top = SizedIngredient.fromJson(jsonObject.getAsJsonObject("top"));
            SizedIngredient middle = SizedIngredient.fromJson(jsonObject.getAsJsonObject("middle"));
            Optional<SizedIngredient> bottom = Optional.empty();
            if (jsonObject.has("bottom")){
                bottom = Optional.of(SizedIngredient.fromJson(jsonObject.getAsJsonObject("bottom")));
            }
            ItemStack output = ShapedRecipe.itemStackFromJson(jsonObject.getAsJsonObject("output"));

            return new FletchingTableRecipe(top, middle, bottom, output, resourceLocation);
        }

        @Override
        public @Nullable FletchingTableRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            SizedIngredient top = SizedIngredient.fromNetwork(friendlyByteBuf);
            SizedIngredient middle = SizedIngredient.fromNetwork(friendlyByteBuf);
            Optional<SizedIngredient> bottom = Optional.empty();
            if (friendlyByteBuf.readBoolean()){
                bottom = Optional.of(SizedIngredient.fromNetwork(friendlyByteBuf));
            }
            ItemStack output = friendlyByteBuf.readItem();
            return new FletchingTableRecipe(top, middle, bottom, output, resourceLocation);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, FletchingTableRecipe recipe) {
            recipe.top.toNetwork(friendlyByteBuf);
            recipe.middle.toNetwork(friendlyByteBuf);
            friendlyByteBuf.writeBoolean(recipe.bottom.isPresent());
            recipe.bottom.ifPresent(sizedIngredient -> sizedIngredient.toNetwork(friendlyByteBuf));
            friendlyByteBuf.writeItemStack(recipe.output, true);
        }
    }
}
