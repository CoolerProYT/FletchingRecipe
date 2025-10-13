package com.coolerpromc.fletchingrecipe.compat.jei;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.compat.jei.category.FletchingCategory;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ModJEIPlugin implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return Identifier.of(FletchingRecipe.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(new FletchingCategory(guiHelper));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(Blocks.FLETCHING_TABLE, FletchingCategory.FLETCHING_TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<FletchingTableRecipe> treeSimulatorRecipe = new ArrayList<>(MinecraftClient.getInstance().world.getRecipeManager().listAllOfType(FletchingRecipe.FLETCHING_RECIPE_TYPE));
        registration.addRecipes(FletchingCategory.FLETCHING_TYPE, treeSimulatorRecipe);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(FletchingTableScreen.class, 89, 34, 22, 16, FletchingCategory.FLETCHING_TYPE);
    }
}
