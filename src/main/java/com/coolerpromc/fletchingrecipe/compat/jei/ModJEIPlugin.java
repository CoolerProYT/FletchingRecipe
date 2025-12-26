package com.coolerpromc.fletchingrecipe.compat.jei;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.FletchingRecipeClient;
import com.coolerpromc.fletchingrecipe.compat.arrowplus.ArrowPlusTippedRecipe;
import com.coolerpromc.fletchingrecipe.compat.jei.category.ExplosiveCategory;
import com.coolerpromc.fletchingrecipe.compat.jei.category.FletchingCategory;
import com.coolerpromc.fletchingrecipe.compat.jei.recipe.JeiExplosiveRecipe;
import com.coolerpromc.fletchingrecipe.compat.jei.recipe.JeiFletchingRecipe;
import com.coolerpromc.fletchingrecipe.config.ExplosiveIngredientConfig;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ModJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(FletchingRecipe.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(new FletchingCategory(guiHelper), new ExplosiveCategory(guiHelper));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(FletchingCategory.FLETCHING_TYPE, Blocks.FLETCHING_TABLE);
        registration.addCraftingStation(ExplosiveCategory.EXPLOSIVE_TYPE, Blocks.FLETCHING_TABLE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<JeiFletchingRecipe> fletchingTableRecipes = new ArrayList<>(FletchingRecipeClient.recipeMap.byType(FletchingRecipe.FLETCHING_RECIPE_TYPE.get()).stream().map(RecipeHolder::value).map(recipe -> {
            ContextMap contextMap = SlotDisplayContext.fromLevel(Minecraft.getInstance().level);
            List<ItemStack> bottom = List.of();
            if (recipe.bottom().isPresent()){
                bottom = recipe.bottom().get().ingredient().display().resolveForStacks(contextMap).stream().map(stack -> stack.copyWithCount(recipe.bottom().get().count())).toList();
            }
            return new JeiFletchingRecipe(
                    recipe.top().ingredient().display().resolveForStacks(contextMap).stream().map(stack -> stack.copyWithCount(recipe.top().count())).toList(),
                    recipe.middle().ingredient().display().resolveForStacks(contextMap).stream().map(stack -> stack.copyWithCount(recipe.middle().count())).toList(),
                    bottom,
                    recipe.output()
            );
        }).toList());

        BuiltInRegistries.POTION.asHolderIdMap().forEach(potion -> {
            if (ModList.get().isLoaded("arrowplus")) ArrowPlusTippedRecipe.register(potion, fletchingTableRecipes);

            ItemStack outputStack = new ItemStack(Items.TIPPED_ARROW, ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount());
            outputStack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));

            JeiFletchingRecipe recipe = new JeiFletchingRecipe(
                    List.of(new ItemStack(Items.LINGERING_POTION.builtInRegistryHolder(), 1, DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(potion)).build())),
                    List.of(new ItemStack(Items.ARROW.builtInRegistryHolder(), ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount())),
                    List.of(),
                    outputStack
            );

            fletchingTableRecipes.add(recipe);
        });

        registration.addRecipes(FletchingCategory.FLETCHING_TYPE, fletchingTableRecipes);

        List<JeiExplosiveRecipe> explosiveRecipes = new ArrayList<>();

        for (Holder<Item> holder : ExplosiveIngredientConfig.explosiveIngredients.keySet()){
            ItemStack explosiveIngredient = new ItemStack(holder);

            ItemStack arrow = Items.ARROW.getDefaultInstance();
            arrow.setCount(ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            ItemStack arrowOutputStack = new ItemStack(Items.ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            arrowOutputStack.set(FletchingRecipe.EXPLOSIVE, holder);
            explosiveRecipes.add(new JeiExplosiveRecipe(explosiveIngredient, arrow, arrowOutputStack));

            ItemStack spectralArrow = Items.SPECTRAL_ARROW.getDefaultInstance();
            spectralArrow.setCount(ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            ItemStack spectralArrowOutputStack = new ItemStack(Items.SPECTRAL_ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            spectralArrowOutputStack.set(FletchingRecipe.EXPLOSIVE, holder);
            explosiveRecipes.add(new JeiExplosiveRecipe(explosiveIngredient, spectralArrow, spectralArrowOutputStack));

            if (ModList.get().isLoaded("arrowplus")) ArrowPlusTippedRecipe.registerExplosive(holder, explosiveRecipes);

            BuiltInRegistries.POTION.asHolderIdMap().forEach(potion -> {
                if (ModList.get().isLoaded("arrowplus")) ArrowPlusTippedRecipe.registerExplosiveTipped(potion, holder, explosiveRecipes);

                ItemStack inputStack = new ItemStack(Items.TIPPED_ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
                inputStack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));

                ItemStack outputStack = new ItemStack(Items.TIPPED_ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
                outputStack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
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
        registration.registerFromDataComponentTypes(Items.ARROW, FletchingRecipe.EXPLOSIVE.get());
        registration.registerFromDataComponentTypes(Items.SPECTRAL_ARROW, FletchingRecipe.EXPLOSIVE.get());
        registration.registerFromDataComponentTypes(Items.TIPPED_ARROW, FletchingRecipe.EXPLOSIVE.get());
    }
}
