package com.coolerpromc.fletchingrecipe.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {
    @Shadow
    protected abstract Collection<EntityHitResult> findHitEntities(Vec3 p_458868_, Vec3 p_458662_);

    @Shadow
    protected abstract ProjectileDeflection hitTargetsOrDeflectSelf(Collection<EntityHitResult> p_452868_);

    @Shadow
    public abstract byte getPierceLevel();

    protected AbstractArrowMixin(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
    }

    @Inject(method = "stepMoveAndHit", at = @At("HEAD"), cancellable = true)
    public void stepMoveAndHit(BlockHitResult hitResult, CallbackInfo ci){
        while(true) {
            if (this.isAlive()) {
                Vec3 vec3 = this.position();
                ArrayList<EntityHitResult> arraylist = new ArrayList(this.findHitEntities(vec3, hitResult.getLocation()));
                arraylist.sort(Comparator.comparingDouble((p_481016_) -> vec3.distanceToSqr(p_481016_.getEntity().position())));
                EntityHitResult entityhitresult = arraylist.isEmpty() ? null : arraylist.getFirst();
                Vec3 vec31 = ((HitResult) Objects.requireNonNullElse(entityhitresult, hitResult)).getLocation();
                this.setPos(vec31);
                this.applyEffectsFromBlocks(vec3, vec31);
                if (this.portalProcess != null && this.portalProcess.isInsidePortalThisTick()) {
                    this.handlePortal();
                }

                if (arraylist.isEmpty()) {
                    if (this.isAlive() && hitResult.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hitResult)) {
                        this.hitTargetOrDeflectSelf(hitResult);
                        this.needsSync = true;
                    }
                } else {
                    if (!this.isAlive() || this.noPhysics || entityhitresult.getType() == HitResult.Type.MISS) {
                        continue;
                    }

                    if (!ForgeEventFactory.onProjectileImpact(this, entityhitresult)) {
                        ProjectileDeflection projectiledeflection = this.hitTargetsOrDeflectSelf(arraylist);
                        this.needsSync = true;
                        if (this.getPierceLevel() > 0 && projectiledeflection == ProjectileDeflection.NONE) {
                            continue;
                        }
                    }
                }
            }

            return;
        }
    }
}
