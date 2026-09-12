package com.coolerpromc.fletchingrecipe.datagen;

import com.coolerpromc.fletchingrecipe.Constants;
import com.coolerpromc.fletchingrecipe.recipe.builder.FletchingRecipeBuilder;
import com.coolerpromc.fletchingrecipe.util.SizedIngredient;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.predicates.DataComponentMatchers;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.MultiRegistryBootstrap;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import java.util.Set;

public class ModRecipeProvider extends RecipeProvider {
    private final HolderGetter<Item> items;

    public ModRecipeProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(recipeOutput, advancementOutput);
        this.items = recipeOutput.lookup(Registries.ITEM);
    }

    @Override
    protected void buildRecipes() {
        FletchingRecipeBuilder.builder()
                .top(SizedIngredient.of(Items.FLINT, 1))
                .middle(SizedIngredient.of(Items.STICK, 1))
                .bottom(SizedIngredient.of(Items.FEATHER, 1))
                .output(new ItemStackTemplate(Items.ARROW, 8))
                .unlockedBy(getHasName(Items.FLINT), has(Items.FLINT))
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .unlockedBy(getHasName(Items.FEATHER), has(Items.FEATHER))
                .save(output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(Constants.MODID, "fletching/arrow")));

        FletchingRecipeBuilder.builder()
                .top(SizedIngredient.of(Items.GLOWSTONE_DUST, 4))
                .middle(SizedIngredient.of(Items.ARROW, 1))
                .output(new ItemStackTemplate(Items.SPECTRAL_ARROW, 4))
                .unlockedBy(getHasName(Items.GLOWSTONE_DUST), has(Items.GLOWSTONE_DUST))
                .unlockedBy(getHasName(Items.ARROW), has(Items.ARROW))
                .save(output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(Constants.MODID, "fletching/spectral_arrow")));
    }

    protected static String getHasName(ItemLike itemLike, PotionContents key) {
        return "has_" + key.potion().get().value().name() + "_" + getItemName(itemLike);
    }

    protected Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike itemLike, PotionContents key) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(this.items, itemLike).withComponents(DataComponentMatchers.Builder.components().exact(DataComponentExactPredicate.builder().expect(DataComponents.POTION_CONTENTS, key).build()).build()));
    }

    public static MultiRegistryBootstrap create() {
        return new MultiRegistryBootstrap() {
            @Override
            public Set<ResourceKey<? extends Registry<?>>> requestedRegistries() {
                return Set.of(Registries.RECIPE, Registries.ADVANCEMENT);
            }

            @Override
            public void run(MultiRegistryBootstrap.BootstrapGetter registries) {
                new ModRecipeProvider(registries.get(Registries.RECIPE), registries.get(Registries.ADVANCEMENT)).buildRecipes();
            }
        };
    }
}
