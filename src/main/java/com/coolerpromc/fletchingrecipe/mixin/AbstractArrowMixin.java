package com.coolerpromc.fletchingrecipe.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {
    @Shadow
    public abstract byte getPierceLevel();

    @Shadow
    @Nullable
    protected abstract EntityHitResult findHitEntity(Vec3 p_36758_, Vec3 p_36759_);

    protected AbstractArrowMixin(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
    }

    @Inject(method = "stepMoveAndHit", at = @At("HEAD"), cancellable = true)
    public void stepMoveAndHit(BlockHitResult hitResult, CallbackInfo ci){
        while (this.isAlive()) {
            Vec3 vec3 = this.position();
            EntityHitResult entityhitresult = this.findHitEntity(vec3, hitResult.getLocation());
            Vec3 vec31 = Objects.requireNonNullElse(entityhitresult, hitResult).getLocation();
            this.setPos(vec31);
            this.applyEffectsFromBlocks(vec3, vec31);
            if (this.portalProcess != null && this.portalProcess.isInsidePortalThisTick()) {
                this.handlePortal();
            }

            if (entityhitresult == null) {
                if (this.isAlive() && hitResult.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hitResult)) {
                    this.hitTargetOrDeflectSelf(hitResult);
                    this.hasImpulse = true;
                }
                break;
            } else if (this.isAlive() && !this.noPhysics) {
                if (!ForgeEventFactory.onProjectileImpact(this, entityhitresult)){
                    ProjectileDeflection projectiledeflection = this.hitTargetOrDeflectSelf(entityhitresult);
                    this.hasImpulse = true;
                    if (this.getPierceLevel() > 0 && projectiledeflection == ProjectileDeflection.NONE) {
                        continue;
                    }
                }
                break;
            }
        }
        ci.cancel();
    }
}
