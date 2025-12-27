package com.coolerpromc.fletchingrecipe.mixin;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Shadow
    @Final
    private MatrixStack matrices;

    @Shadow
    public abstract void drawItem(ItemStack item, int x, int y);

    @Inject(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"))
    private void drawItemInSlot(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
        if (stack.contains(FletchingRecipe.EXPLOSIVE)) {
            RegistryEntry<Item> explosiveItemHolder = stack.get(FletchingRecipe.EXPLOSIVE);
            this.matrices.push();
            this.matrices.translate(0, 0, 200);
            this.matrices.scale(0.5f, 0.5f, 0.5f);
            this.drawItem(explosiveItemHolder.value().getDefaultStack(), x * 2, y * 2);
            this.matrices.pop();
        }
    }
}
