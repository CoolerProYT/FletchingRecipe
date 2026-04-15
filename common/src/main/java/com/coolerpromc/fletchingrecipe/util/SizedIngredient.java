package com.coolerpromc.fletchingrecipe.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public record SizedIngredient(Ingredient ingredient, int count) {
    public static final Codec<Integer> POSITIVE_INT = intRangeWithMessage(1, Integer.MAX_VALUE, p_274847_ -> "Value must be positive: " + p_274847_);

    public static final Codec<SizedIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(SizedIngredient::ingredient),
                    optionalFieldAlwaysWrite(POSITIVE_INT, "count", 1).forGetter(SizedIngredient::count))
            .apply(instance, SizedIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SizedIngredient> PACKET_CODEC = StreamCodec.composite(Ingredient.CONTENTS_STREAM_CODEC, SizedIngredient::ingredient, ByteBufCodecs.VAR_INT, SizedIngredient::count, SizedIngredient::new);

    public static SizedIngredient of(ItemLike item, int count) {
        return new SizedIngredient(Ingredient.of(item), count);
    }

    public boolean test(ItemStack stack) {
        return ingredient.test(stack) && stack.getCount() >= count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SizedIngredient other)) return false;
        return count == other.count && ingredient.equals(other.ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredient, count);
    }

    @Override
    public String toString() {
        return count + "x " + ingredient;
    }

    public static <T> MapCodec<T> optionalFieldAlwaysWrite(Codec<T> codec, String name, T defaultValue) {
        return codec.optionalFieldOf(name).xmap(o -> o.orElse(defaultValue), Optional::of);
    }

    private static Codec<Integer> intRangeWithMessage(int min, int max, Function<Integer, String> errorMessage) {
        return Codec.INT
                .validate(
                        p_274889_ -> p_274889_.compareTo(min) >= 0 && p_274889_.compareTo(max) <= 0
                                ? DataResult.success(p_274889_)
                                : DataResult.error(() -> errorMessage.apply(p_274889_))
                );
    }
}