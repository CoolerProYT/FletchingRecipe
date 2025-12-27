package com.coolerpromc.fletchingrecipe.mixin;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.config.ExplosiveIngredientConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(PersistentProjectileEntity.class)
public abstract class ProjectileMixin extends ProjectileEntity {
    public ProjectileMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract byte getPierceLevel();

    @Shadow
    protected abstract EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition);

    @Inject(method = "applyCollision", at = @At("HEAD"), cancellable = true)
    private void onBlockHit(BlockHitResult blockHitResult, CallbackInfo ci) {
        while(true) {
            if (this.isAlive()) {
                Vec3d vec3d = this.getEntityPos();
                EntityHitResult entityHitResult = this.getEntityCollision(vec3d, blockHitResult.getPos());
                Vec3d vec3d2 = Objects.requireNonNullElse(entityHitResult, blockHitResult).getPos();
                this.setPosition(vec3d2);
                this.tickBlockCollision(vec3d, vec3d2);
                if (this.portalManager != null && this.portalManager.isInPortal()) {
                    this.tickPortalTeleportation();
                }

                if (entityHitResult == null) {
                    if (this.isAlive() && blockHitResult.getType() != HitResult.Type.MISS && !onProjectileImpact(this, blockHitResult)) {
                        this.hitOrDeflect(blockHitResult);
                        this.velocityDirty = true;
                    }
                } else {
                    if (!this.isAlive() || this.noClip || entityHitResult.getType() == HitResult.Type.MISS) {
                        continue;
                    }

                    if(!onProjectileImpact(this, entityHitResult)){
                        ProjectileDeflection projectileDeflection = this.hitOrDeflect(entityHitResult);
                        this.velocityDirty = true;
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

    private static boolean onProjectileImpact(ProjectileEntity projectile, HitResult hitResult){
        if (!projectile.getEntityWorld().isClient() && projectile instanceof PersistentProjectileEntity abstractArrow && abstractArrow.getItemStack().contains(FletchingRecipe.EXPLOSIVE)){
            Vec3d pos = hitResult.getPos();
            float ratio = abstractArrow.isCritical() ? 1f : 2f;
            RegistryEntry<Item> explosiveItemHolder = abstractArrow.getItemStack().get(FletchingRecipe.EXPLOSIVE);
            abstractArrow.getEntityWorld().createExplosion(null, pos.x, pos.y, pos.z, ExplosiveIngredientConfig.explosiveIngredients.get(explosiveItemHolder) / ratio, World.ExplosionSourceType.TNT);
            if (hitResult instanceof EntityHitResult result && result.getEntity().getEntity() instanceof LivingEntity entity){
                abstractArrow.onHit(entity);
            }
            abstractArrow.getItemStack().decrement(1);
            abstractArrow.remove(Entity.RemovalReason.KILLED);
        }

        return false;
    }
}
