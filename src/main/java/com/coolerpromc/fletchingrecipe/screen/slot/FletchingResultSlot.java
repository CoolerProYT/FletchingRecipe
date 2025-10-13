package com.coolerpromc.fletchingrecipe.screen.slot;

import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.util.SizedIngredient;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;

public class FletchingResultSlot extends ResultSlot {
    private final CraftingContainer craftSlots;
    private final AbstractContainerMenu menu;
    private FletchingTableRecipe recipe = null;

    public FletchingResultSlot(Player player, CraftingContainer craftSlots, ResultContainer container, int slot, int xPosition, int yPosition, AbstractContainerMenu menu) {
        super(player, craftSlots, container, slot, xPosition, yPosition);
        this.craftSlots = craftSlots;
        this.menu = menu;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.checkTakeAchievements(stack);

        if (recipe != null) {
            consumeIngredient(craftSlots, 0, recipe.top());
            consumeIngredient(craftSlots, 1, recipe.middle());

            if (recipe.bottom().isPresent()) {
                consumeIngredient(craftSlots, 2, recipe.bottom().get());
            }
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

    public void setRecipe(FletchingTableRecipe recipe) {
        this.recipe = recipe;
    }
}
