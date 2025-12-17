package com.coolerpromc.fletchingrecipe.datagen;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.datagen.recipebuilder.FletchingRecipeBuilder;
import com.coolerpromc.fletchingrecipe.util.DataComponentIngredient;
import com.coolerpromc.fletchingrecipe.util.SizedIngredient;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    private final HolderGetter<Item> items;
    private final HolderLookup.Provider lookupProvider;

    public ModRecipeProvider(HolderLookup.Provider lookupProvider, RecipeOutput output) {
        super(lookupProvider, output);
        this.items = lookupProvider.lookupOrThrow(Registries.ITEM);
        this.lookupProvider = lookupProvider;
    }

    @Override
    protected void buildRecipes() {
        FletchingRecipeBuilder.builder()
                .top(SizedIngredient.of(Items.FLINT, 1))
                .middle(SizedIngredient.of(Items.STICK, 1))
                .bottom(SizedIngredient.of(Items.FEATHER, 1))
                .output(new ItemStack(Items.ARROW, 4))
                .unlockedBy(getHasName(Items.FLINT), has(Items.FLINT))
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .unlockedBy(getHasName(Items.FEATHER), has(Items.FEATHER))
                .save(output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(FletchingRecipe.MODID, "fletching/arrow")));

        FletchingRecipeBuilder.builder()
                .top(SizedIngredient.of(Items.GLOWSTONE_DUST, 4))
                .middle(SizedIngredient.of(Items.ARROW, 1))
                .output(new ItemStack(Items.SPECTRAL_ARROW, 2))
                .unlockedBy(getHasName(Items.GLOWSTONE_DUST), has(Items.GLOWSTONE_DUST))
                .unlockedBy(getHasName(Items.ARROW), has(Items.ARROW))
                .save(output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(FletchingRecipe.MODID, "fletching/spectral_arrow")));

        BuiltInRegistries.POTION.asHolderIdMap().forEach(potion -> {
            ItemStack outputStack = new ItemStack(Items.TIPPED_ARROW, 8);
            outputStack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));

            FletchingRecipeBuilder.builder()
                    .top(new SizedIngredient(DataComponentIngredient.of(true, DataComponentMap.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(potion)).build(), Items.LINGERING_POTION), 1))
                    .middle(SizedIngredient.of(Items.ARROW, 8))
                    .output(outputStack)
                    .unlockedBy(getHasName(Items.LINGERING_POTION, new PotionContents(potion)), has(Items.LINGERING_POTION, new PotionContents(potion)))
                    .unlockedBy(getHasName(Items.ARROW), has(Items.ARROW))
                    .save(output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(FletchingRecipe.MODID, "fletching/" + potion.unwrapKey().get().identifier().getPath() + "_tipped_arrow")));
        });
    }

    protected static String getHasName(ItemLike itemLike, PotionContents key) {
        return "has_" + key.potion().get().value().name() + "_" + getItemName(itemLike);
    }

    protected Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike itemLike, PotionContents key) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(this.items, itemLike).withComponents(DataComponentMatchers.Builder.components().exact(DataComponentExactPredicate.builder().expect(DataComponents.POTION_CONTENTS, key).build()).build()));
    }

    private Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate.Builder... p_299527_) {
        return inventoryTrigger(Arrays.stream(p_299527_).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
    }

    private Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate... p_297226_) {
        return CriteriaTriggers.INVENTORY_CHANGED
                .createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(p_297226_)));
    }

    public static final class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider lookupProvider, RecipeOutput output) {
            return new ModRecipeProvider(lookupProvider, output);
        }

        @Override
        public String getName() {
            return "Fletching Recipe recipes";
        }
    }
}
