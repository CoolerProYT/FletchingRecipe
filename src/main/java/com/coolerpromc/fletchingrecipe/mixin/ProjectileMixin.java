package com.coolerpromc.fletchingrecipe.mixin;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.config.ExplosiveIngredientConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

@Mixin(AbstractArrow.class)
public abstract class ProjectileMixin extends Projectile {
    public ProjectileMixin(EntityType<? extends Projectile> entityType, Level world) {
        super(entityType, world);
    }

    @Shadow
    protected abstract Collection<EntityHitResult> findHitEntities(Vec3 from, Vec3 to);

    @Shadow
    public abstract byte getPierceLevel();

    @Shadow
    protected abstract ProjectileDeflection hitTargetsOrDeflectSelf(Collection<EntityHitResult> hitResults);

    @Inject(method = "stepMoveAndHit", at = @At("HEAD"), cancellable = true)
    private void onBlockHit(BlockHitResult blockHitResult, CallbackInfo ci) {
        while(true) {
            if (this.isAlive()) {
                Vec3 vec3d = this.position();
                ArrayList<EntityHitResult> arrayList = new ArrayList<>(this.findHitEntities(vec3d, blockHitResult.getLocation()));
                arrayList.sort(Comparator.comparingDouble((entityHitResultx) -> vec3d.distanceToSqr(entityHitResultx.getEntity().position())));
                EntityHitResult entityHitResult = arrayList.isEmpty() ? null : arrayList.getFirst();
                Vec3 vec3d2 = Objects.requireNonNullElse(entityHitResult, blockHitResult).getLocation();
                this.setPos(vec3d2);
                this.applyEffectsFromBlocks(vec3d, vec3d2);
                if (this.portalProcess != null && this.portalProcess.isInsidePortalThisTick()) {
                    this.handlePortal();
                }

                if (arrayList.isEmpty()) {
                    if (this.isAlive() && blockHitResult.getType() != HitResult.Type.MISS && ! onProjectileImpact(this, blockHitResult)) {
                        this.hitTargetOrDeflectSelf(blockHitResult);
                        this.needsSync = true;
                    }
                } else {
                    if (!this.isAlive() || this.noPhysics || entityHitResult.getType() == HitResult.Type.MISS) {
                        continue;
                    }

                    if(!onProjectileImpact(this, entityHitResult)){
                        ProjectileDeflection projectileDeflection = this.hitTargetsOrDeflectSelf(arrayList);
                        this.needsSync = true;
                        if (this.getPierceLevel() > 0 && projectileDeflection == ProjectileDeflection.NONE) {
                            continue;
                        }
                    }
                }
            }

            ci.cancel();
            return;
        }
    }

    private static boolean onProjectileImpact(Projectile projectile, HitResult hitResult){
        if (!projectile.level().isClientSide() && projectile instanceof AbstractArrow abstractArrow && abstractArrow.getPickupItemStackOrigin().has(FletchingRecipe.EXPLOSIVE)){
            Vec3 pos = hitResult.getLocation();
            float ratio = abstractArrow.isCritArrow() ? 1f : 2f;
            Holder<Item> explosiveItemHolder = abstractArrow.getPickupItemStackOrigin().get(FletchingRecipe.EXPLOSIVE);
            abstractArrow.level().explode(null, pos.x, pos.y, pos.z, ExplosiveIngredientConfig.explosiveIngredients.get(explosiveItemHolder) / ratio, Level.ExplosionInteraction.TNT);
            if (hitResult instanceof EntityHitResult result && result.getEntity().asLivingEntity() instanceof LivingEntity entity){
                abstractArrow.doPostHurtEffects(entity);
            }
            abstractArrow.getPickupItemStackOrigin().shrink(1);
            abstractArrow.remove(Entity.RemovalReason.KILLED);
        }

        return false;
    }
}
