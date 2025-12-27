package com.coolerpromc.fletchingrecipe.compat.jei.category;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.compat.jei.recipe.JeiExplosiveRecipe;
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

import java.util.List;

public class ExplosiveCategory extends AbstractRecipeCategory<JeiExplosiveRecipe> {
    public static final Identifier UID = Identifier.of(FletchingRecipe.MOD_ID, "explosive");
    public static final Identifier TEXTURE = Identifier.of(FletchingRecipe.MOD_ID, "textures/gui/fletching_table.png");
    public static final IRecipeType<JeiExplosiveRecipe> EXPLOSIVE_TYPE = IRecipeType.create(UID, JeiExplosiveRecipe.class);

    public ExplosiveCategory(IGuiHelper helper) {
        super(EXPLOSIVE_TYPE, Text.translatable("category.jei.explosive_arrow"), helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.FLETCHING_TABLE)), 140, 56);
    }

    @Override
    public void draw(JeiExplosiveRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        guiGraphics.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 10, 15, 140, 56, 256, 256);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, JeiExplosiveRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,7,20).addItemStacks(List.of(recipe.explosiveIngredient()));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,38,2).addItemStacks(List.of(recipe.arrow()));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT,114,20).add(recipe.output());
    }
}