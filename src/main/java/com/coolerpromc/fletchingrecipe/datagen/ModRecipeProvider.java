package com.coolerpromc.fletchingrecipe.datagen;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.datagen.recipebuilder.FletchingRecipeBuilder;
import com.coolerpromc.fletchingrecipe.util.SizedIngredient;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.StrictNBTIngredient;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> output) {
        FletchingRecipeBuilder.builder()
                .top(SizedIngredient.of(Items.FLINT, 1))
                .middle(SizedIngredient.of(Items.STICK, 1))
                .bottom(SizedIngredient.of(Items.FEATHER, 1))
                .output(new ItemStack(Items.ARROW, 8))
                .unlockedBy(getHasName(Items.FLINT), has(Items.FLINT))
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .unlockedBy(getHasName(Items.FEATHER), has(Items.FEATHER))
                .save(output, new ResourceLocation(FletchingRecipe.MODID, "fletching/arrow"));

        FletchingRecipeBuilder.builder()
                .top(SizedIngredient.of(Items.GLOWSTONE_DUST, 4))
                .middle(SizedIngredient.of(Items.ARROW, 1))
                .output(new ItemStack(Items.SPECTRAL_ARROW, 4))
                .unlockedBy(getHasName(Items.GLOWSTONE_DUST), has(Items.GLOWSTONE_DUST))
                .unlockedBy(getHasName(Items.ARROW), has(Items.ARROW))
                .save(output, new ResourceLocation(FletchingRecipe.MODID, "fletching/spectral_arrow"));
    }

    protected static String getHasName(ItemLike itemLike, Holder<Potion> key) {
        return "has_" + key.unwrapKey().get().location().getPath() + "_" + getItemName(itemLike);
    }

    protected InventoryChangeTrigger.TriggerInstance has(ItemLike itemLike, CompoundTag key) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(itemLike).hasNbt(key).build());
    }
}
