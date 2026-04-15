package com.coolerpromc.fletchingrecipe.compat.arrowplus;

import com.coolerpromc.arrowplus.item.custom.ModArrowItem;
import net.minecraft.world.item.ItemStack;

public class ArrowCheck {
    public static boolean check(ItemStack stack){
        return stack.getItem() instanceof ModArrowItem;
    }
}