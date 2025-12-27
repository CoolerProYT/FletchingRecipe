package com.coolerpromc.fletchingrecipe.compat.jei;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.compat.arrowplus.ArrowPlusTippedRecipe;
import com.coolerpromc.fletchingrecipe.compat.jei.category.ExplosiveCategory;
import com.coolerpromc.fletchingrecipe.compat.jei.category.FletchingCategory;
import com.coolerpromc.fletchingrecipe.compat.jei.recipe.JeiExplosiveRecipe;
import com.coolerpromc.fletchingrecipe.compat.jei.recipe.JeiFletchingRecipe;
import com.coolerpromc.fletchingrecipe.compat.jei.subtype.ExplosiveSubtypeInterpreter;
import com.coolerpromc.fletchingrecipe.compat.morefletchingtable.MoreFletchingTableStation;
import com.coolerpromc.fletchingrecipe.config.ExplosiveIngredientConfig;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModJEIPlugin implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return Identifier.of(FletchingRecipe.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(new FletchingCategory(guiHelper), new ExplosiveCategory(guiHelper));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(FletchingCategory.FLETCHING_TYPE, !FabricLoader.getInstance().isModLoaded("lolmft") ? new ItemConvertible[]{Blocks.FLETCHING_TABLE} : MoreFletchingTableStation.get());
        registration.addRecipeCatalysts(ExplosiveCategory.EXPLOSIVE_TYPE, !FabricLoader.getInstance().isModLoaded("lolmft") ? new ItemConvertible[]{Blocks.FLETCHING_TABLE} : MoreFletchingTableStation.get());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<JeiFletchingRecipe> fletchingTableRecipes = new ArrayList<>(MinecraftClient.getInstance().world.getRecipeManager().listAllOfType(FletchingRecipe.FLETCHING_RECIPE_TYPE).stream().map(RecipeEntry::value).map(recipe -> {
            List<ItemStack> bottom = List.of();
            if (recipe.bottom().isPresent()){
                bottom = Arrays.stream(recipe.bottom().get().ingredient().getMatchingStacks()).map(stack -> stack.copyWithCount(recipe.bottom().get().count())).toList();
            }
            return new JeiFletchingRecipe(
                    Arrays.stream(recipe.top().ingredient().getMatchingStacks()).map(stack -> stack.copyWithCount(recipe.top().count())).toList(),
                    Arrays.stream(recipe.middle().ingredient().getMatchingStacks()).map(stack -> stack.copyWithCount(recipe.middle().count())).toList(),
                    bottom,
                    recipe.output()
            );
        }).toList());

        Registries.POTION.getIndexedEntries().forEach(potion -> {
            if (FabricLoader.getInstance().isModLoaded("arrowplus")) ArrowPlusTippedRecipe.register(potion, fletchingTableRecipes);

            ItemStack outputStack = new ItemStack(Items.TIPPED_ARROW, ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount());
            outputStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(potion));

            JeiFletchingRecipe recipe = new JeiFletchingRecipe(
                    List.of(new ItemStack(Items.LINGERING_POTION.getRegistryEntry(), 1, ComponentChanges.builder().add(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(potion)).build())),
                    List.of(new ItemStack(Items.ARROW.getRegistryEntry(), ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount())),
                    List.of(),
                    outputStack
            );

            fletchingTableRecipes.add(recipe);
        });

        registration.addRecipes(FletchingCategory.FLETCHING_TYPE, fletchingTableRecipes);

        List<JeiExplosiveRecipe> explosiveRecipes = new ArrayList<>();

        for (RegistryEntry<Item> holder : ExplosiveIngredientConfig.explosiveIngredients.keySet()){
            ItemStack explosiveIngredient = new ItemStack(holder);

            ItemStack arrow = Items.ARROW.getDefaultStack();
            arrow.setCount(ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            ItemStack arrowOutputStack = new ItemStack(Items.ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            arrowOutputStack.set(FletchingRecipe.EXPLOSIVE, holder);
            explosiveRecipes.add(new JeiExplosiveRecipe(explosiveIngredient, arrow, arrowOutputStack));

            ItemStack spectralArrow = Items.SPECTRAL_ARROW.getDefaultStack();
            spectralArrow.setCount(ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            ItemStack spectralArrowOutputStack = new ItemStack(Items.SPECTRAL_ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            spectralArrowOutputStack.set(FletchingRecipe.EXPLOSIVE, holder);
            explosiveRecipes.add(new JeiExplosiveRecipe(explosiveIngredient, spectralArrow, spectralArrowOutputStack));

            if (FabricLoader.getInstance().isModLoaded("arrowplus")) ArrowPlusTippedRecipe.registerExplosive(holder, explosiveRecipes);

            Registries.POTION.getIndexedEntries().forEach(potion -> {
                if (FabricLoader.getInstance().isModLoaded("arrowplus")) ArrowPlusTippedRecipe.registerExplosiveTipped(potion, holder, explosiveRecipes);

                ItemStack inputStack = new ItemStack(Items.TIPPED_ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
                inputStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(potion));

                ItemStack outputStack = new ItemStack(Items.TIPPED_ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
                outputStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(potion));
                outputStack.set(FletchingRecipe.EXPLOSIVE, holder);

                JeiExplosiveRecipe recipe = new JeiExplosiveRecipe(
                        new ItemStack(holder),
                        inputStack,
                        outputStack
                );

                explosiveRecipes.add(recipe);
            });
        }

        registration.addRecipes(ExplosiveCategory.EXPLOSIVE_TYPE, explosiveRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(FletchingTableScreen.class, 81, 35, 22, 16, FletchingCategory.FLETCHING_TYPE);
        registration.addRecipeClickArea(FletchingTableScreen.class, 81, 35, 22, 16, ExplosiveCategory.EXPLOSIVE_TYPE);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(Items.ARROW, ExplosiveSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(Items.SPECTRAL_ARROW, ExplosiveSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(Items.TIPPED_ARROW, ExplosiveSubtypeInterpreter.INSTANCE);
    }
}
