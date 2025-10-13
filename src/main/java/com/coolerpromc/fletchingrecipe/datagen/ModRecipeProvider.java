package com.coolerpromc.fletchingrecipe.datagen;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.datagen.recipebuilder.FletchingRecipeBuilder;
import com.coolerpromc.fletchingrecipe.util.SizedIngredient;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.impl.recipe.ingredient.builtin.ComponentsIngredient;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.component.ComponentMapPredicate;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> lookup) {
        super(output, lookup);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
        return new RecipeGenerator(wrapperLookup, recipeExporter) {
            @Override
            public void generate() {
                FletchingRecipeBuilder.builder()
                        .top(SizedIngredient.of(Items.FLINT, 1))
                        .middle(SizedIngredient.of(Items.STICK, 1))
                        .bottom(SizedIngredient.of(Items.FEATHER, 1))
                        .output(new ItemStack(Items.ARROW, 4))
                        .criterion(hasItem(Items.FLINT), conditionsFromItem(Items.FLINT))
                        .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                        .criterion(hasItem(Items.FEATHER), conditionsFromItem(Items.FEATHER))
                        .offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(FletchingRecipe.MOD_ID, "fletching/arrow")));

                FletchingRecipeBuilder.builder()
                        .top(SizedIngredient.of(Items.GLOWSTONE_DUST, 4))
                        .middle(SizedIngredient.of(Items.ARROW, 1))
                        .output(new ItemStack(Items.SPECTRAL_ARROW, 2))
                        .criterion(hasItem(Items.GLOWSTONE_DUST), conditionsFromItem(Items.GLOWSTONE_DUST))
                        .criterion(hasItem(Items.ARROW), conditionsFromItem(Items.ARROW))
                        .offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(FletchingRecipe.MOD_ID, "fletching/spectral_arrow")));

                Registries.POTION.getIndexedEntries().forEach(potion -> {
                    ItemStack outputStack = new ItemStack(Items.TIPPED_ARROW, 8);
                    outputStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(potion));

                    FletchingRecipeBuilder.builder()
                            .top(new SizedIngredient(new ComponentsIngredient(Ingredient.ofItem(Items.LINGERING_POTION), ComponentChanges.builder().add(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(potion)).build()).toVanilla(), 1))
                            .middle(SizedIngredient.of(Items.ARROW, 8))
                            .output(outputStack)
                            .criterion(getHasName(Items.LINGERING_POTION, new PotionContentsComponent(potion)), has(Items.LINGERING_POTION, new PotionContentsComponent(potion)))
                            .criterion(hasItem(Items.ARROW), conditionsFromItem(Items.ARROW))
                            .offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(FletchingRecipe.MOD_ID, "fletching/" + potion.getKey().get().getValue().getPath() + "_tipped_arrow")));
                });
            }

            private static String getHasName(ItemConvertible itemLike, PotionContentsComponent key) {
                return "has_" + key.potion().get().value().getBaseName() + "_" + getItemPath(itemLike);
            }

            private AdvancementCriterion<InventoryChangedCriterion.Conditions> has(ItemConvertible itemLike, PotionContentsComponent key) {
                return conditionsFromPredicates(ItemPredicate.Builder.create().components(ComponentsPredicate.Builder.create().exact(ComponentMapPredicate.builder().add(DataComponentTypes.POTION_CONTENTS, key).build()).build()));
            }
        };
    }

    @Override
    public String getName() {
        return "";
    }
}
