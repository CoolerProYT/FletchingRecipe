package com.coolerpromc.fletchingrecipe.mixin;

import com.coolerpromc.fletchingrecipe.CommonClass;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.stream.Stream;

@Pseudo
@Mixin(targets = "com.coolerpromc.arrowplus.compat.jei.ModJEIPlugin", remap = false)
public abstract class ArrowPlusJeiSubtypeMixin {
    @Redirect(method = "registerItemSubtypes", at = @At(value = "INVOKE",
            target = "Lmezz/jei/api/registration/ISubtypeRegistration;registerFromDataComponentTypes(Lnet/minecraft/world/item/Item;[Lnet/minecraft/core/component/DataComponentType;)V"))
    private void fletchingrecipe$includeExplosive(ISubtypeRegistration registration, Item item, DataComponentType<?>[] components) {
        if (item instanceof net.minecraft.world.item.ArrowItem) {
            components = Stream.concat(Stream.of(components), Stream.of(CommonClass.EXPLOSIVE.get()))
                    .distinct().toArray(DataComponentType<?>[]::new);
        }
        registration.registerFromDataComponentTypes(item, components);
    }
}
