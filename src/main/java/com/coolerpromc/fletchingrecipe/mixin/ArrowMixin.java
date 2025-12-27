package com.coolerpromc.fletchingrecipe.mixin;

import com.coolerpromc.fletchingrecipe.util.ArrowStack;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Arrow.class)
public class ArrowMixin implements ArrowStack {
    private ItemStack arrowStack;

    @Override
    public ItemStack getPickupStack() {
        return arrowStack;
    }

    @Override
    public void setPickupStack(ItemStack itemStack) {
        this.arrowStack = itemStack;
    }
}
