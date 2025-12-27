package com.coolerpromc.fletchingrecipe.compat.jei.category;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.compat.jei.recipe.JeiFletchingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.block.Blocks;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FletchingCategory extends AbstractRecipeCategory<JeiFletchingRecipe> {
    public static final Identifier UID = Identifier.of(FletchingRecipe.MOD_ID, "fletching");
    public static final Identifier TEXTURE = Identifier.of(FletchingRecipe.MOD_ID, "textures/gui/fletching_table.png");
    public static final IRecipeType<JeiFletchingRecipe> FLETCHING_TYPE = IRecipeType.create(UID, JeiFletchingRecipe.class);

    public FletchingCategory(IGuiHelper helper) {
        super(FLETCHING_TYPE, Text.translatable("category.jei.fletching"), helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.FLETCHING_TABLE)), 123, 56);
    }

    @Override
    public void draw(JeiFletchingRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        guiGraphics.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 34, 15, 123, 56, 256, 256);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, JeiFletchingRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,14,2).addItemStacks(recipe.top());
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,14,20).addItemStacks(recipe.middle());
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,14,38).addItemStacks(recipe.bottom());
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT,90,20).add(recipe.output());
    }
}