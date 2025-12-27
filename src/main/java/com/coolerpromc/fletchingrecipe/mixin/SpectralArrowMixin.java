package com.coolerpromc.fletchingrecipe.mixin;

import com.coolerpromc.fletchingrecipe.util.ArrowStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpectralArrowItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpectralArrow.class)
public class SpectralArrowMixin implements ArrowStack {
    @Unique
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
