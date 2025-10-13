package com.coolerpromc.fletchingrecipe.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record FletchingRecipeInput(ItemStack top, ItemStack middle, ItemStack bottom) implements Container {
    @Override
    public int getContainerSize() {
        return 3;
    }

    @Override
    public boolean isEmpty() {
        return top.isEmpty() || middle.isEmpty() || bottom.isEmpty();
    }

    @Override
    public ItemStack getItem(int i) {
        return switch (i) {
            case 0 -> top;
            case 1 -> middle;
            case 2 -> bottom;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public ItemStack removeItem(int i, int i1) {
        return switch (i) {
            case 0 -> {
                top.shrink(i1);
                yield top;
            }
            case 1 -> {
                middle.shrink(i1);
                yield middle;
            }
            case 2 -> {
                bottom.shrink(i1);
                yield bottom;
            }
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return getItem(i);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {

    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {

    }
}
