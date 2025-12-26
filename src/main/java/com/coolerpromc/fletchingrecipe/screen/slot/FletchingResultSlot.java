package com.coolerpromc.fletchingrecipe.screen.slot;

import com.coolerpromc.fletchingrecipe.config.FletchingRecipeConfig;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableMenu;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public class FletchingResultSlot extends ResultSlot {
    private final CraftingContainer craftSlots;
    private final FletchingTableMenu menu;
    private RecipeHolder<FletchingTableRecipe> recipeHolder = null;

    public FletchingResultSlot(Player player, CraftingContainer craftSlots, ResultContainer container, int slot, int xPosition, int yPosition, FletchingTableMenu menu) {
        super(player, craftSlots, container, slot, xPosition, yPosition);
        this.craftSlots = craftSlots;
        this.menu = menu;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.checkTakeAchievements(stack);

        if (recipeHolder != null && recipeHolder.value() instanceof FletchingTableRecipe recipe) {
            consumeIngredient(craftSlots, 0, recipe.top());
            consumeIngredient(craftSlots, 1, recipe.middle());

            if (recipe.bottom().isPresent()) {
                consumeIngredient(craftSlots, 2, recipe.bottom().get());
            }
        }
        else if (menu.isValidTippedRecipe()){
            menu.consumeTippedArrowIngredients();
        }
        else if (menu.hasExplosive()){
            for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
                ItemStack ingredient = this.craftSlots.getItem(i);
                if (!ingredient.isEmpty()) {
                    ingredient.shrink(FletchingRecipeConfig.CONFIG.explosiveArrowCraftingAmount.get());
                    if (ingredient.isEmpty()) {
                        this.craftSlots.setItem(i, ItemStack.EMPTY);
                    }
                }
            }
            menu.consumeExplosive();
        }

        craftSlots.setChanged();
        menu.slotsChanged(craftSlots);
    }

    private void consumeIngredient(Container container, int slotIndex, SizedIngredient ingredient) {
        ItemStack slotStack = container.getItem(slotIndex);
        if (slotStack.isEmpty()) return;

        int consumeCount = ingredient.count();

        slotStack.shrink(consumeCount);
        if (slotStack.isEmpty()) {
            container.setItem(slotIndex, ItemStack.EMPTY);
        } else {
            container.setChanged();
        }
    }

    public void setRecipeHolder(RecipeHolder<FletchingTableRecipe> recipeHolder) {
        this.recipeHolder = recipeHolder;
    }
}