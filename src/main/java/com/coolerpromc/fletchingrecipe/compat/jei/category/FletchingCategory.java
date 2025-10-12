package com.coolerpromc.fletchingrecipe.compat.jei.category;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;

public class FletchingCategory extends AbstractRecipeCategory<RecipeHolder<FletchingTableRecipe>> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(FletchingRecipe.MODID, "fletching");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FletchingRecipe.MODID, "textures/gui/fletching_table.png");
    public static final IRecipeHolderType<FletchingTableRecipe> FLETCHING_TYPE = IRecipeHolderType.create(FletchingRecipe.FLETCHING_RECIPE_TYPE.get());

    public FletchingCategory(IGuiHelper helper) {
        super(FLETCHING_TYPE, Component.translatable("block.minecraft.fletching_table"), helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.FLETCHING_TABLE)), 168, 77);
    }

    @Override
    public void draw(RecipeHolder<FletchingTableRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 5, 5, 168, 77, 256, 256);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, RecipeHolder<FletchingTableRecipe> recipeHolder, IFocusGroup iFocusGroup) {
        FletchingTableRecipe recipe = recipeHolder.value();
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,43,12).add(recipe.top().ingredient());
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,43,30).add(recipe.middle().ingredient());
        if (recipe.bottom().isPresent()){
            iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,43,48).add(recipe.bottom().get().ingredient());
        }

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT,119,30).add(recipe.output());
    }
}
