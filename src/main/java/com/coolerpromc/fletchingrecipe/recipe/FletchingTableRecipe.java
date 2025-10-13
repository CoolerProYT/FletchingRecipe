package com.coolerpromc.fletchingrecipe.recipe;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.util.SizedIngredient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;

import static net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix.GSON;

public record FletchingTableRecipe(SizedIngredient top, SizedIngredient middle, Optional<SizedIngredient> bottom, ItemStack output, Identifier id) implements Recipe<FletchingRecipeInput> {
    @Override
    public boolean matches(FletchingRecipeInput fletchingRecipeInput, World level) {
        return bottom.map(sizedIngredient -> top.test(fletchingRecipeInput.top()) && middle.test(fletchingRecipeInput.middle()) && sizedIngredient.test(fletchingRecipeInput.bottom()))
                .orElseGet(() -> top.test(fletchingRecipeInput.top()) && middle.test(fletchingRecipeInput.middle()) && fletchingRecipeInput.bottom().isEmpty());
    }

    @Override
    public ItemStack craft(FletchingRecipeInput inventory, DynamicRegistryManager registryManager) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.output.copy();
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<? extends Recipe<FletchingRecipeInput>> getSerializer() {
        return FletchingRecipe.FLETCHING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<FletchingRecipeInput>> getType() {
        return FletchingRecipe.FLETCHING_RECIPE_TYPE;
    }

    public static ItemStack getItemStack(JsonObject json, boolean readNBT, boolean disallowsAirInRecipe) {
        String itemName = JsonHelper.getString(json, "item");
        Item item = getItem(itemName, disallowsAirInRecipe);
        if (readNBT && json.has("nbt")) {
            NbtCompound nbt = getNBT(json.get("nbt"));
            NbtCompound tmp = new NbtCompound();
            if (nbt.contains("ForgeCaps")) {
                tmp.put("ForgeCaps", nbt.get("ForgeCaps"));
                nbt.remove("ForgeCaps");
            }

            tmp.put("tag", nbt);
            tmp.putString("id", itemName);
            tmp.putInt("Count", JsonHelper.getInt(json, "count", 1));
            return ItemStack.fromNbt(tmp);
        } else {
            return new ItemStack(item, JsonHelper.getInt(json, "count", 1));
        }
    }

    public static Item getItem(String itemName, boolean disallowsAirInRecipe) {
        Identifier itemKey = new Identifier(itemName);
        if (!Registries.ITEM.containsId(itemKey)) {
            throw new JsonSyntaxException("Unknown item '" + itemName + "'");
        } else {
            Item item = Registries.ITEM.get(itemKey);
            if (disallowsAirInRecipe && item == Items.AIR) {
                throw new JsonSyntaxException("Invalid item: " + itemName);
            } else {
                return Objects.requireNonNull(item);
            }
        }
    }

    public static NbtCompound getNBT(JsonElement element) {
        try {
            return element.isJsonObject() ? StringNbtReader.parse(GSON.toJson(element)) : StringNbtReader.parse(JsonHelper.asString(element, "nbt"));
        } catch (CommandSyntaxException e) {
            throw new JsonSyntaxException("Invalid NBT Entry: " + String.valueOf(e));
        }
    }

    public static class Serializer implements RecipeSerializer<FletchingTableRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public FletchingTableRecipe read(Identifier id, JsonObject jsonObject) {
            SizedIngredient top = SizedIngredient.fromJson(jsonObject.getAsJsonObject("top"));
            SizedIngredient middle = SizedIngredient.fromJson(jsonObject.getAsJsonObject("middle"));
            Optional<SizedIngredient> bottom = Optional.empty();
            if (jsonObject.has("bottom")){
                bottom = Optional.of(SizedIngredient.fromJson(jsonObject.getAsJsonObject("bottom")));
            }
            ItemStack output = FletchingTableRecipe.getItemStack(jsonObject.getAsJsonObject("output"), true, true);

            return new FletchingTableRecipe(top, middle, bottom, output, id);
        }

        @Override
        public FletchingTableRecipe read(Identifier id, PacketByteBuf buf) {
            SizedIngredient top = SizedIngredient.fromPacket(buf);
            SizedIngredient middle = SizedIngredient.fromPacket(buf);
            Optional<SizedIngredient> bottom = Optional.empty();
            if (buf.readBoolean()){
                bottom = Optional.of(SizedIngredient.fromPacket(buf));
            }
            ItemStack output = buf.readItemStack();
            return new FletchingTableRecipe(top, middle, bottom, output, id);
        }

        @Override
        public void write(PacketByteBuf buf, FletchingTableRecipe recipe) {
            recipe.top.write(buf);
            recipe.middle.write(buf);
            buf.writeBoolean(recipe.bottom.isPresent());
            recipe.bottom.ifPresent(sizedIngredient -> sizedIngredient.write(buf));
            buf.writeItemStack(recipe.output);
        }
    }
}
