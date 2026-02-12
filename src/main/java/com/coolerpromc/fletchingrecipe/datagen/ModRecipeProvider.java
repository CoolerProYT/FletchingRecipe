package com.coolerpromc.fletchingrecipe.datagen;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.datagen.recipebuilder.FletchingRecipeBuilder;
import com.coolerpromc.fletchingrecipe.util.SizedIngredient;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider wrapperLookup, RecipeOutput recipeExporter) {
        return new RecipeProvider(wrapperLookup, recipeExporter) {
            @Override
            public void buildRecipes() {
                FletchingRecipeBuilder.builder()
                        .top(SizedIngredient.of(Items.FLINT, 1))
                        .middle(SizedIngredient.of(Items.STICK, 1))
                        .bottom(SizedIngredient.of(Items.FEATHER, 1))
                        .output(new ItemStackTemplate(Items.ARROW, 8))
                        .unlockedBy(getHasName(Items.FLINT), has(Items.FLINT))
                        .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                        .unlockedBy(getHasName(Items.FEATHER), has(Items.FEATHER))
                        .save(output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(FletchingRecipe.MOD_ID, "fletching/arrow")));

                FletchingRecipeBuilder.builder()
                        .top(SizedIngredient.of(Items.GLOWSTONE_DUST, 4))
                        .middle(SizedIngredient.of(Items.ARROW, 1))
                        .output(new ItemStackTemplate(Items.SPECTRAL_ARROW, 4))
                        .unlockedBy(getHasName(Items.GLOWSTONE_DUST), has(Items.GLOWSTONE_DUST))
                        .unlockedBy(getHasName(Items.ARROW), has(Items.ARROW))
                        .save(output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(FletchingRecipe.MOD_ID, "fletching/spectral_arrow")));
            }

            private static String getHasName(ItemLike itemLike, PotionContents key) {
                return "has_" + key.potion().get().value().name() + "_" + getItemName(itemLike);
            }

            private Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike itemLike, PotionContents key) {
                return inventoryTrigger(ItemPredicate.Builder.item().withComponents(DataComponentMatchers.Builder.components().exact(DataComponentExactPredicate.builder().expect(DataComponents.POTION_CONTENTS, key).build()).build()));
            }
        };
    }

    @Override
    public String getName() {
        return "";
    }
}
