package com.coolerpromc.fletchingrecipe.compat.jei.category;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.compat.jei.recipe.JeiFletchingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record FletchingCategory(IGuiHelper helper) implements IRecipeCategory<JeiFletchingRecipe> {
    public static final Identifier UID = Identifier.of(FletchingRecipe.MOD_ID, "fletching");
    public static final Identifier TEXTURE = Identifier.of(FletchingRecipe.MOD_ID, "textures/gui/fletching_table.png");
    public static final RecipeType<JeiFletchingRecipe> FLETCHING_TYPE = RecipeType.create(UID.getNamespace(), UID.getPath(), JeiFletchingRecipe.class);

    @Override
    public RecipeType<JeiFletchingRecipe> getRecipeType() {
        return FLETCHING_TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("category.jei.fletching");
    }

    @Override
    public IDrawable getBackground() {
        return helper.createDrawable(TEXTURE, 34, 15, 123, 56);
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.FLETCHING_TABLE));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, JeiFletchingRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,14,2).addItemStacks(recipe.top());
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,14,20).addItemStacks(recipe.middle());
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,14,38).addItemStacks(recipe.bottom());
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT,90,20).addItemStack(recipe.output());
    }
}