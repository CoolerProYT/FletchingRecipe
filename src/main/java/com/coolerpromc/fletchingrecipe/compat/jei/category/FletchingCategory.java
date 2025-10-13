package com.coolerpromc.fletchingrecipe.compat.jei.category;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public class FletchingCategory extends AbstractRecipeCategory<FletchingTableRecipe> {
    public static final Identifier TEXTURE = Identifier.of(FletchingRecipe.MOD_ID, "textures/gui/fletching_table.png");
    public static final RecipeType<FletchingTableRecipe> FLETCHING_TYPE = RecipeType.create(FletchingRecipe.MOD_ID, "fletching", FletchingTableRecipe.class);

    public FletchingCategory(IGuiHelper helper) {
        super(FLETCHING_TYPE, Text.translatable("block.minecraft.fletching_table"), helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.FLETCHING_TABLE)), 137, 56);
    }

    @Override
    public void draw(FletchingTableRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        guiGraphics.drawTexture(TEXTURE, 0, 0, 20, 15, 137, 56, 256, 256);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, FletchingTableRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,28,2).addItemStacks(Arrays.stream(recipe.top().ingredient().getMatchingStacks()).map(stack -> stack.copyWithCount(recipe.top().count())).toList());
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,28,20).addItemStacks(Arrays.stream(recipe.middle().ingredient().getMatchingStacks()).map(stack -> stack.copyWithCount(recipe.middle().count())).toList());
        if (recipe.bottom().isPresent()){
            iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,28,38).addItemStacks(Arrays.stream(recipe.bottom().get().ingredient().getMatchingStacks()).map(stack -> stack.copyWithCount(recipe.bottom().get().count())).toList());
        }

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT,104,20).addItemStack(recipe.output());
    }
}
