package com.coolerpromc.fletchingrecipe.datagen;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.datagen.recipebuilder.FletchingRecipeBuilder;
import com.coolerpromc.fletchingrecipe.util.SizedIngredient;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.impl.recipe.ingredient.builtin.NbtIngredient;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        FletchingRecipeBuilder.builder()
                .top(SizedIngredient.of(Items.FLINT, 1))
                .middle(SizedIngredient.of(Items.STICK, 1))
                .bottom(SizedIngredient.of(Items.FEATHER, 1))
                .output(new ItemStack(Items.ARROW, 4))
                .criterion(hasItem(Items.FLINT), conditionsFromItem(Items.FLINT))
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .criterion(hasItem(Items.FEATHER), conditionsFromItem(Items.FEATHER))
                .offerTo(exporter, Identifier.of(FletchingRecipe.MOD_ID, "fletching/arrow"));

        FletchingRecipeBuilder.builder()
                .top(SizedIngredient.of(Items.GLOWSTONE_DUST, 4))
                .middle(SizedIngredient.of(Items.ARROW, 1))
                .output(new ItemStack(Items.SPECTRAL_ARROW, 2))
                .criterion(hasItem(Items.GLOWSTONE_DUST), conditionsFromItem(Items.GLOWSTONE_DUST))
                .criterion(hasItem(Items.ARROW), conditionsFromItem(Items.ARROW))
                .offerTo(exporter, Identifier.of(FletchingRecipe.MOD_ID, "fletching/spectral_arrow"));

        Registries.POTION.getIndexedEntries().forEach(potion -> {
            ItemStack outputStack = PotionUtil.setPotion(new ItemStack(Items.TIPPED_ARROW, 8), potion.value());

            FletchingRecipeBuilder.builder()
                    .top(new SizedIngredient(new NbtIngredient(Ingredient.ofItems(Items.LINGERING_POTION), outputStack.getOrCreateNbt(), true).toVanilla(), 1))
                    .middle(SizedIngredient.of(Items.ARROW, 8))
                    .output(outputStack)
                    .criterion(getHasName(Items.LINGERING_POTION, potion), has(Items.LINGERING_POTION, outputStack.getOrCreateNbt()))
                    .criterion(hasItem(Items.ARROW), conditionsFromItem(Items.ARROW))
                    .offerTo(exporter, Identifier.of(FletchingRecipe.MOD_ID, "fletching/" + potion.getKey().get().getValue().getPath() + "_tipped_arrow"));
        });
    }

    @Override
    public String getName() {
        return "";
    }

    private static String getHasName(ItemConvertible itemLike, RegistryEntry<Potion> key) {
        return "has_" + key.getKey().get().getValue().getPath() + "_" + getItemPath(itemLike);
    }

    private InventoryChangedCriterion.Conditions has(ItemConvertible itemLike, NbtCompound key) {
        return conditionsFromItemPredicates(ItemPredicate.Builder.create().items(itemLike).nbt(key).build());
    }
}
