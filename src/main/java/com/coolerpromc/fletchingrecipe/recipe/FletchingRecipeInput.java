package com.coolerpromc.fletchingrecipe.recipe;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public record FletchingRecipeInput(ItemStack top, ItemStack middle, ItemStack bottom) implements Inventory {
    @Override
    public int size() {
        return 3;
    }

    @Override
    public boolean isEmpty() {
        return top.isEmpty() || middle.isEmpty() || bottom.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return switch (slot) {
            case 0 -> top;
            case 1 -> middle;
            case 2 -> bottom;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return switch (slot) {
            case 0 -> {
                top.decrement(amount);
                yield top;
            }
            case 1 -> {
                middle.decrement(amount);
                yield middle;
            }
            case 2 -> {
                bottom.decrement(amount);
                yield bottom;
            }
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public ItemStack removeStack(int slot) {
        return getStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {

    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {

    }
}
