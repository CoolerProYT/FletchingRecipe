package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.compat.morefletchingtable.MoreFletchingTableCheck;
import com.coolerpromc.fletchingrecipe.config.ExplosiveIngredientConfig;
import com.coolerpromc.fletchingrecipe.platform.Services;
import com.coolerpromc.fletchingrecipe.platform.util.RegistryHandler;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class CommonClass {
    public static final RegistryHandler<MenuType<FletchingTableMenu>> FLETCHING_TABLE_MENU = Services.REGISTRY.registerMenu("fletching_table", FletchingTableMenu::new, BlockPos.STREAM_CODEC);
    public static final RegistryHandler<RecipeType<FletchingTableRecipe>> FLETCHING_RECIPE_TYPE = Services.REGISTRY.registerRecipeType("fletching");
    public static final RegistryHandler<RecipeSerializer<FletchingTableRecipe>> FLETCHING_RECIPE_SERIALIZER = Services.REGISTRY.registerRecipeSerializer("fletching", FletchingTableRecipe.SERIALIZER);
    public static final RegistryHandler<DataComponentType<Holder<Item>>> EXPLOSIVE = Services.REGISTRY.registerDataComponent("explosive", builder -> builder.persistent(Item.CODEC).networkSynchronized(Item.STREAM_CODEC).cacheEncoding());

    public static void init() {

    }

    public static InteractionResult onRightClickBlock(Block block, Level level, Player player, BlockPos pos){
        if (block == Blocks.FLETCHING_TABLE || (Services.PLATFORM.isModLoaded("lolmft") && MoreFletchingTableCheck.checkBlock(block)) && (player.getMainHandItem().isEmpty() || (!player.getMainHandItem().isEmpty() && !player.isShiftKeyDown()))){
            if (!level.isClientSide()){
                Services.MENU.openMenu((ServerPlayer) player, new SimpleMenuProvider((i, inventory, _) -> new FletchingTableMenu(i, inventory, ContainerLevelAccess.create(level, pos)), Component.translatable("block.minecraft.fletching_table")), pos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static boolean onProjectileImpact(Projectile projectile, HitResult hitResult){
        if (!projectile.level().isClientSide() && projectile instanceof AbstractArrow abstractArrow && abstractArrow.getPickupItemStackOrigin().has(CommonClass.EXPLOSIVE.get())){
            Vec3 pos = hitResult.getLocation();
            float ratio = abstractArrow.isCritArrow() ? 1f : 2f;
            Holder<Item> explosiveItemHolder = abstractArrow.getPickupItemStackOrigin().get(CommonClass.EXPLOSIVE.get());
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