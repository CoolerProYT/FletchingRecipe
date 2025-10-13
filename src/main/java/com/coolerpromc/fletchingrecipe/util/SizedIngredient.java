package com.coolerpromc.fletchingrecipe.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.JsonHelper;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public record SizedIngredient(Ingredient ingredient, int count) {
    public static SizedIngredient of(ItemConvertible item, int count) {
        return new SizedIngredient(Ingredient.ofItems(item), count);
    }

    public boolean test(ItemStack stack) {
        return ingredient.test(stack) && stack.getCount() >= count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof SizedIngredient)) {
            return false;
        } else {
            SizedIngredient other = (SizedIngredient)o;
            return this.count == other.count && this.ingredient.equals(other.ingredient);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredient, count);
    }

    @Override
    public String toString() {
        return count + "x " + ingredient;
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.add("ingredient", ingredient.toJson());
        json.addProperty("count", count);
        return json;
    }

    public static SizedIngredient fromJson(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            Ingredient ingredient = Ingredient.fromJson(obj.get("ingredient"));
            int count = JsonHelper.getInt(obj, "count", 1);
            return new SizedIngredient(ingredient, count);
        } else {
            return new SizedIngredient(Ingredient.fromJson(element), 1);
        }
    }

    public void write(PacketByteBuf buffer) {
        ingredient.write(buffer);
        buffer.writeVarInt(count);
    }

    public static SizedIngredient fromPacket(PacketByteBuf buffer) {
        Ingredient ingredient = Ingredient.fromPacket(buffer);
        int count = buffer.readVarInt();
        return new SizedIngredient(ingredient, count);
    }
}