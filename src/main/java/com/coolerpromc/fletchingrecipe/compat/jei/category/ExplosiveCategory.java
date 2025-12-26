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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class ExplosiveCategory extends AbstractRecipeCategory<JeiExplosiveRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(FletchingRecipe.MODID, "explosive");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FletchingRecipe.MODID, "textures/gui/fletching_table.png");
    public static final IRecipeType<JeiExplosiveRecipe> EXPLOSIVE_TYPE = IRecipeType.create(UID, JeiExplosiveRecipe.class);

    public ExplosiveCategory(IGuiHelper helper) {
        super(EXPLOSIVE_TYPE, Component.translatable("category.jei.explosive_arrow"), helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.FLETCHING_TABLE)), 140, 56);
    }

    @Override
    public void draw(JeiExplosiveRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 10, 15, 140, 56, 256, 256);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, JeiExplosiveRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,7,20).addItemStacks(List.of(recipe.explosiveIngredient()));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT,38,2).addItemStacks(List.of(recipe.arrow()));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT,114,20).add(recipe.output());
    }
}
