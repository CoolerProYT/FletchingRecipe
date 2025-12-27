package com.coolerpromc.fletchingrecipe.mixin;

import com.coolerpromc.fletchingrecipe.util.ArrowStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpectralArrowItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowItem.class)
public class ArrowItemMixin {
    @Inject(method = "createArrow", at = @At("HEAD"), cancellable = true)
    public void createArrow(Level level, ItemStack stack, LivingEntity entity, CallbackInfoReturnable<AbstractArrow> cir){
        Arrow arrow = new Arrow(level, entity);
        arrow.setEffectsFromItem(stack);
        System.out.println(stack);
        System.out.println(stack.getTag());
        if(arrow instanceof ArrowStack arrowStack){
            System.out.println("is");
            arrowStack.setPickupStack(stack.copy());
        }
        cir.setReturnValue(arrow);
    }
}
