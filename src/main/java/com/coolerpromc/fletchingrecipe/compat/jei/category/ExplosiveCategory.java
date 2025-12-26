package com.coolerpromc.fletchingrecipe.compat.jei.category;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.compat.jei.recipe.JeiExplosiveRecipe;
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

import java.util.List;

public record ExplosiveCategory(IGuiHelper helper) implements IRecipeCategory<JeiExplosiveRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(FletchingRecipe.MODID, "explosive");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FletchingRecipe.MODID, "textures/gui/fletching_table.png");
    public static final RecipeType<JeiExplosiveRecipe> EXPLOSIVE_TYPE = RecipeType.create(UID.getNamespace(), UID.getPath(), JeiExplosiveRecipe.class);

    @Override
    public RecipeType<JeiExplosiveRecipe> getRecipeType() {
        return EXPLOSIVE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("category.jei.explosive_arrow");
    }

    @Override
    public IDrawable getBackground() {
        return helper.createDrawable(TEXTURE, 10, 15, 140, 56);
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.FLETCHING_TABLE));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, JeiExplosiveRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,7,20).addItemStacks(List.of(recipe.explosiveIngredient()));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,38,2).addItemStacks(List.of(recipe.arrow()));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT,114,20).addItemStack(recipe.output());
    }
}
