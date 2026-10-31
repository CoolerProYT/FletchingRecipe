package com.coolerpromc.fletchingrecipe.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/** Lets our JEI plugin register potion and explosive identity together. */
@Pseudo
@Mixin(targets = "mezz.jei.library.plugins.vanilla.VanillaPlugin", remap = false, priority = 900)
public abstract class JeiVanillaTippedArrowMixin {
    // ArrowPlus may already redirect this call; in that case it handles deferring the registration.
    @WrapWithCondition(method = "registerItemSubtypes", at = @At(value = "INVOKE",
            target = "Lmezz/jei/api/registration/ISubtypeRegistration;registerSubtypeInterpreter(Lnet/minecraft/world/item/Item;Lmezz/jei/api/ingredients/subtypes/ISubtypeInterpreter;)V"), require = 0)
    private boolean fletchingrecipe$deferTippedArrow(ISubtypeRegistration registration, Item item,
                                                   ISubtypeInterpreter<ItemStack> interpreter) {
        return item != Items.TIPPED_ARROW;
    }
}
