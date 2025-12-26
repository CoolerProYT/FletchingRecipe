package com.coolerpromc.fletchingrecipe.datagen;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.datagen.recipebuilder.FletchingRecipeBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        FletchingRecipeBuilder.builder()
                .top(SizedIngredient.of(Items.FLINT, 1))
                .middle(SizedIngredient.of(Items.STICK, 1))
                .bottom(SizedIngredient.of(Items.FEATHER, 1))
                .output(new ItemStack(Items.ARROW, 8))
                .unlockedBy(getHasName(Items.FLINT), has(Items.FLINT))
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .unlockedBy(getHasName(Items.FEATHER), has(Items.FEATHER))
                .save(output, ResourceLocation.fromNamespaceAndPath(FletchingRecipe.MODID, "fletching/arrow"));

        FletchingRecipeBuilder.builder()
                .top(SizedIngredient.of(Items.GLOWSTONE_DUST, 4))
                .middle(SizedIngredient.of(Items.ARROW, 1))
                .output(new ItemStack(Items.SPECTRAL_ARROW, 4))
                .unlockedBy(getHasName(Items.GLOWSTONE_DUST), has(Items.GLOWSTONE_DUST))
                .unlockedBy(getHasName(Items.ARROW), has(Items.ARROW))
                .save(output, ResourceLocation.fromNamespaceAndPath(FletchingRecipe.MODID, "fletching/spectral_arrow"));
    }

    protected static String getHasName(ItemLike itemLike, PotionContents key) {
        return "has_" + key.potion().get().getKey().location().getPath() + "_" + getItemName(itemLike);
    }

    protected Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike itemLike, PotionContents key) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(itemLike).hasComponents(DataComponentPredicate.builder().expect(DataComponents.POTION_CONTENTS, key).build()).build());
    }
}
