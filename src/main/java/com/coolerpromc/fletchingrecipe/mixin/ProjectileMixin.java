package com.coolerpromc.fletchingrecipe.mixin;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.config.ExplosiveIngredientConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class ProjectileMixin extends ProjectileEntity {
    public ProjectileMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract byte getPierceLevel();

    @Shadow
    protected abstract EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition);

    @Shadow
    public abstract boolean isNoClip();

    @Shadow
    protected boolean inGround;

    @Shadow
    public int shake;

    @Shadow
    @Nullable
    private BlockState inBlockState;

    @Shadow
    protected abstract void age();

    @Shadow
    protected abstract void fall();

    @Shadow
    protected abstract boolean shouldFall();

    @Shadow
    protected int inGroundTime;

    @Shadow
    public abstract boolean isCritical();

    @Shadow
    protected abstract float getDragInWater();

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        super.tick();
        boolean bl = this.isNoClip();
        Vec3d vec3d = this.getVelocity();
        if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            double d = vec3d.horizontalLength();
            this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI)));
            this.setPitch((float)(MathHelper.atan2(vec3d.y, d) * (double)(180F / (float)Math.PI)));
            this.prevYaw = this.getYaw();
            this.prevPitch = this.getPitch();
        }

        BlockPos blockPos = this.getBlockPos();
        BlockState blockState = this.getWorld().getBlockState(blockPos);
        if (!blockState.isAir() && !bl) {
            VoxelShape voxelShape = blockState.getCollisionShape(this.getWorld(), blockPos);
            if (!voxelShape.isEmpty()) {
                Vec3d vec3d2 = this.getPos();

                for(Box box : voxelShape.getBoundingBoxes()) {
                    if (box.offset(blockPos).contains(vec3d2)) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        if (this.shake > 0) {
            --this.shake;
        }

        if (this.isTouchingWaterOrRain() || blockState.isOf(Blocks.POWDER_SNOW)) {
            this.extinguish();
        }

        if (this.inGround && !bl) {
            if (this.inBlockState != blockState && this.shouldFall()) {
                this.fall();
            } else if (!this.getWorld().isClient) {
                this.age();
            }

            ++this.inGroundTime;
        } else {
            this.inGroundTime = 0;
            Vec3d vec3d3 = this.getPos();
            Vec3d vec3d2 = vec3d3.add(vec3d);
            HitResult hitResult = this.getWorld().raycast(new RaycastContext(vec3d3, vec3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
            if (hitResult.getType() != HitResult.Type.MISS) {
                vec3d2 = hitResult.getPos();
            }

            while(!this.isRemoved()) {
                EntityHitResult entityHitResult = this.getEntityCollision(vec3d3, vec3d2);
                if (entityHitResult != null) {
                    hitResult = entityHitResult;
                }

                if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
                    Entity entity = ((EntityHitResult)hitResult).getEntity();
                    Entity entity2 = this.getOwner();
                    if (entity instanceof PlayerEntity && entity2 instanceof PlayerEntity && !((PlayerEntity)entity2).shouldDamagePlayer((PlayerEntity)entity)) {
                        hitResult = null;
                        entityHitResult = null;
                    }
                }

                if (hitResult != null && hitResult.getType() != HitResult.Type.MISS && !bl) {
                    if (onProjectileImpact(this, hitResult))
                        break;
                    ProjectileDeflection projectileDeflection = this.hitOrDeflect(hitResult);
                    this.velocityDirty = true;
                    if (projectileDeflection != ProjectileDeflection.NONE) {
                        break;
                    }
                }

                if (entityHitResult == null || this.getPierceLevel() <= 0) {
                    break;
                }

                hitResult = null;
            }

            vec3d = this.getVelocity();
            double e = vec3d.x;
            double f = vec3d.y;
            double g = vec3d.z;
            if (this.isCritical()) {
                for(int i = 0; i < 4; ++i) {
                    this.getWorld().addParticle(ParticleTypes.CRIT, this.getX() + e * (double)i / (double)4.0F, this.getY() + f * (double)i / (double)4.0F, this.getZ() + g * (double)i / (double)4.0F, -e, -f + 0.2, -g);
                }
            }

            double h = this.getX() + e;
            double j = this.getY() + f;
            double k = this.getZ() + g;
            double l = vec3d.horizontalLength();
            if (bl) {
                this.setYaw((float)(MathHelper.atan2(-e, -g) * (double)(180F / (float)Math.PI)));
            } else {
                this.setYaw((float)(MathHelper.atan2(e, g) * (double)(180F / (float)Math.PI)));
            }

            this.setPitch((float)(MathHelper.atan2(f, l) * (double)(180F / (float)Math.PI)));
            this.setPitch(updateRotation(this.prevPitch, this.getPitch()));
            this.setYaw(updateRotation(this.prevYaw, this.getYaw()));
            float m = 0.99F;
            if (this.isTouchingWater()) {
                for(int n = 0; n < 4; ++n) {
                    float o = 0.25F;
                    this.getWorld().addParticle(ParticleTypes.BUBBLE, h - e * (double)0.25F, j - f * (double)0.25F, k - g * (double)0.25F, e, f, g);
                }

                m = this.getDragInWater();
            }

            this.setVelocity(vec3d.multiply(m));
            if (!bl) {
                this.applyGravity();
            }

            this.setPosition(h, j, k);
            this.checkBlockCollision();
        }
        ci.cancel();
    }

    private static boolean onProjectileImpact(ProjectileEntity projectile, HitResult hitResult){
        if (!projectile.getEntityWorld().isClient() && projectile instanceof PersistentProjectileEntity abstractArrow && abstractArrow.getItemStack().contains(FletchingRecipe.EXPLOSIVE)){
            Vec3d pos = hitResult.getPos();
            float ratio = abstractArrow.isCritical() ? 1f : 2f;
            RegistryEntry<Item> explosiveItemHolder = abstractArrow.getItemStack().get(FletchingRecipe.EXPLOSIVE);
            abstractArrow.getEntityWorld().createExplosion(null, pos.x, pos.y, pos.z, ExplosiveIngredientConfig.explosiveIngredients.get(explosiveItemHolder) / ratio, World.ExplosionSourceType.TNT);
            if (hitResult instanceof EntityHitResult result && result.getEntity() instanceof LivingEntity entity){
                abstractArrow.onHit(entity);
            }
            abstractArrow.getItemStack().decrement(1);
            abstractArrow.remove(Entity.RemovalReason.KILLED);
        }

        return false;
    }
}
