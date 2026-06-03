package com.coolerpromc.fletchingrecipe.mixin;

import com.coolerpromc.fletchingrecipe.CommonClass;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

@Mixin(AbstractArrow.class)
public abstract class ProjectileMixin extends Projectile {
    public ProjectileMixin(EntityType<? extends Projectile> entityType, Level world) {
        super(entityType, world);
    }

    @WrapOperation(
        method = "stepMoveAndHit",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;hitTargetsOrDeflectSelf(Ljava/util/Collection;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;")
    )
    private ProjectileDeflection wrapEntityHit(AbstractArrow instance, Collection<EntityHitResult> entityHitResults, Operation<ProjectileDeflection> original, @Local(name = "firstEntityHit") EntityHitResult entityHitResult) {
        if (CommonClass.onProjectileImpact(this, entityHitResult)) {
            return null;
        }
        return original.call(instance, entityHitResults);
    }

    @WrapOperation(
        method = "stepMoveAndHit",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;")
    )
    private ProjectileDeflection wrapBlockHit(AbstractArrow instance, HitResult hitResult, Operation<ProjectileDeflection> original) {
        if (CommonClass.onProjectileImpact(this, hitResult)) {
            return null;
        }
        return original.call(instance, hitResult);
    }
}