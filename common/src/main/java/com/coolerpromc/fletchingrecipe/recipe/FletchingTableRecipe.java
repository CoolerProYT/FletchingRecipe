package com.coolerpromc.fletchingrecipe.recipe;

import com.coolerpromc.fletchingrecipe.CommonClass;
import com.coolerpromc.fletchingrecipe.util.SizedIngredient;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record FletchingTableRecipe(SizedIngredient top, SizedIngredient middle, Optional<SizedIngredient> bottom, ItemStackTemplate output) implements Recipe<FletchingRecipeInput> {
    public static final MapCodec<FletchingTableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SizedIngredient.CODEC.fieldOf("top").forGetter(FletchingTableRecipe::top),
            SizedIngredient.CODEC.fieldOf("middle").forGetter(FletchingTableRecipe::middle),
            SizedIngredient.CODEC.optionalFieldOf("bottom").forGetter(FletchingTableRecipe::bottom),
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(FletchingTableRecipe::output)
    ).apply(instance, FletchingTableRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FletchingTableRecipe> STREAM_CODEC = StreamCodec.composite(
            SizedIngredient.PACKET_CODEC,
            FletchingTableRecipe::top,
            SizedIngredient.PACKET_CODEC,
            FletchingTableRecipe::middle,
            SizedIngredient.PACKET_CODEC.apply(ByteBufCodecs::optional),
            FletchingTableRecipe::bottom,
            ItemStackTemplate.STREAM_CODEC,
            FletchingTableRecipe::output,
            FletchingTableRecipe::new
    );

    public static final RecipeSerializer<FletchingTableRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    @Override
    public boolean matches(FletchingRecipeInput fletchingRecipeInput, Level level) {
        return bottom.map(sizedIngredient -> top.test(fletchingRecipeInput.top()) && middle.test(fletchingRecipeInput.middle()) && sizedIngredient.test(fletchingRecipeInput.bottom()))
                .orElseGet(() -> top.test(fletchingRecipeInput.top()) && middle.test(fletchingRecipeInput.middle()) && fletchingRecipeInput.bottom().isEmpty());
    }

    @Override
    public ItemStack assemble(FletchingRecipeInput fletchingRecipeInput) {
        return this.output.create();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<? extends Recipe<FletchingRecipeInput>> getSerializer() {
        return CommonClass.FLETCHING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<FletchingRecipeInput>> getType() {
        return CommonClass.FLETCHING_RECIPE_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return null;
    }
}
