package com.coolerpromc.fletchingrecipe.compat.jei.category;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public record FletchingCategory(IGuiHelper helper) implements IRecipeCategory<FletchingTableRecipe> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(FletchingRecipe.MODID, "textures/gui/fletching_table.png");
    public static final RecipeType<FletchingTableRecipe> FLETCHING_TYPE = RecipeType.create(FletchingRecipe.MODID, "fletching", FletchingTableRecipe.class);

    @Override
    public RecipeType<FletchingTableRecipe> getRecipeType() {
        return FLETCHING_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.minecraft.fletching_table");
    }

    @Override
    public void draw(FletchingTableRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(TEXTURE, 0, 0, 20, 15, 137, 57);
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.FLETCHING_TABLE));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, FletchingTableRecipe recipeHolder, IFocusGroup iFocusGroup) {
        FletchingTableRecipe recipe = recipeHolder;
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,28,2).addItemStacks(Arrays.stream(recipe.top().ingredient().getItems()).map(stack -> stack.copyWithCount(recipe.top().count())).toList());
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,28,20).addItemStacks(Arrays.stream(recipe.middle().ingredient().getItems()).map(stack -> stack.copyWithCount(recipe.middle().count())).toList());
        if (recipe.bottom().isPresent()){
            iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,28,38).addItemStacks(Arrays.stream(recipe.bottom().get().ingredient().getItems()).map(stack -> stack.copyWithCount(recipe.bottom().get().count())).toList());
        }

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT,104,20).addItemStack(recipe.output());
    }
}
