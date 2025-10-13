package com.coolerpromc.fletchingrecipe.screen.slot;

import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.util.SizedIngredient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.CraftingResultSlot;

public class FletchingResultSlot extends CraftingResultSlot {
    private final RecipeInputInventory craftSlots;
    private final ScreenHandler menu;
    private RecipeEntry<FletchingTableRecipe> recipeHolder = null;

    public FletchingResultSlot(PlayerEntity player, RecipeInputInventory craftSlots, CraftingResultInventory container, int slot, int xPosition, int yPosition, ScreenHandler menu) {
        super(player, craftSlots, container, slot, xPosition, yPosition);
        this.craftSlots = craftSlots;
        this.menu = menu;
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        this.onCrafted(stack);

        if (recipeHolder != null && recipeHolder.value() instanceof FletchingTableRecipe recipe) {
            consumeIngredient(craftSlots, 0, recipe.top());
            consumeIngredient(craftSlots, 1, recipe.middle());

            if (recipe.bottom().isPresent()) {
                consumeIngredient(craftSlots, 2, recipe.bottom().get());
            }
        }

        craftSlots.markDirty();
        menu.onContentChanged(craftSlots);
    }

    private void consumeIngredient(Inventory container, int slotIndex, SizedIngredient ingredient) {
        ItemStack slotStack = container.getStack(slotIndex);
        if (slotStack.isEmpty()) return;

        int consumeCount = ingredient.count();

        slotStack.decrement(consumeCount);
        if (slotStack.isEmpty()) {
            container.setStack(slotIndex, ItemStack.EMPTY);
        } else {
            container.markDirty();
        }
    }

    public void setRecipeHolder(RecipeEntry<FletchingTableRecipe> recipeHolder) {
        this.recipeHolder = recipeHolder;
    }
}
