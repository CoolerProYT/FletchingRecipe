package com.coolerpromc.fletchingrecipe.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

public final class SizedIngredient {
    private final Ingredient ingredient;
    private final int count;

    public static SizedIngredient of(ItemLike item, int count) {
        return new SizedIngredient(Ingredient.of(item), count);
    }

    public SizedIngredient(Ingredient ingredient, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        } else {
            this.ingredient = ingredient;
            this.count = count;
        }
    }

    public Ingredient ingredient() {
        return this.ingredient;
    }

    public int count() {
        return this.count;
    }

    public boolean test(ItemStack stack) {
        return this.ingredient.test(stack) && stack.getCount() >= this.count;
    }

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

    public int hashCode() {
        return Objects.hash(this.ingredient, this.count);
    }

    public String toString() {
        return this.count + "x " + this.ingredient;
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
            int count = GsonHelper.getAsInt(obj, "count", 1);
            return new SizedIngredient(ingredient, count);
        } else {
            return new SizedIngredient(Ingredient.fromJson(element), 1);
        }
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        ingredient.toNetwork(buffer);
        buffer.writeVarInt(count);
    }

    public static SizedIngredient fromNetwork(FriendlyByteBuf buffer) {
        Ingredient ingredient = Ingredient.fromNetwork(buffer);
        int count = buffer.readVarInt();
        return new SizedIngredient(ingredient, count);
    }
}
