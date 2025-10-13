package com.coolerpromc.fletchingrecipe.recipe;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.util.SizedIngredient;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

import java.util.Optional;

public record FletchingTableRecipe(SizedIngredient top, SizedIngredient middle, Optional<SizedIngredient> bottom, ItemStack output) implements Recipe<FletchingRecipeInput> {
    @Override
    public boolean matches(FletchingRecipeInput fletchingRecipeInput, World level) {
        return bottom.map(sizedIngredient -> top.test(fletchingRecipeInput.top()) && middle.test(fletchingRecipeInput.middle()) && sizedIngredient.test(fletchingRecipeInput.bottom()))
                .orElseGet(() -> top.test(fletchingRecipeInput.top()) && middle.test(fletchingRecipeInput.middle()) && fletchingRecipeInput.bottom().isEmpty());
    }

    @Override
    public ItemStack craft(FletchingRecipeInput fletchingRecipeInput, RegistryWrapper.WrapperLookup provider) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.output.copy();
    }

    @Override
    public RecipeSerializer<? extends Recipe<FletchingRecipeInput>> getSerializer() {
        return FletchingRecipe.FLETCHING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<FletchingRecipeInput>> getType() {
        return FletchingRecipe.FLETCHING_RECIPE_TYPE;
    }

    public static class Serializer implements RecipeSerializer<FletchingTableRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        public MapCodec<FletchingTableRecipe> codec() {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    SizedIngredient.CODEC.fieldOf("top").forGetter(FletchingTableRecipe::top),
                    SizedIngredient.CODEC.fieldOf("middle").forGetter(FletchingTableRecipe::middle),
                    SizedIngredient.CODEC.optionalFieldOf("bottom").forGetter(FletchingTableRecipe::bottom),
                    ItemStack.CODEC.fieldOf("output").forGetter(FletchingTableRecipe::output)
            ).apply(instance, FletchingTableRecipe::new));
        }

        public PacketCodec<RegistryByteBuf, FletchingTableRecipe> packetCodec() {
            return PacketCodec.tuple(
                    SizedIngredient.PACKET_CODEC,
                    FletchingTableRecipe::top,
                    SizedIngredient.PACKET_CODEC,
                    FletchingTableRecipe::middle,
                    SizedIngredient.PACKET_CODEC.collect(PacketCodecs::optional),
                    FletchingTableRecipe::bottom,
                    ItemStack.PACKET_CODEC,
                    FletchingTableRecipe::output,
                    FletchingTableRecipe::new
            );
        }
    }
}
