package com.coolerpromc.fletchingrecipe;


import com.coolerpromc.fletchingrecipe.config.ExplosiveIngredientConfig;
import com.coolerpromc.fletchingrecipe.config.NeoForgeFletchingRecipeConfig;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import com.coolerpromc.fletchingrecipe.platform.NeoForgeRegistryHelper;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod(Constants.MODID)
public class FletchingRecipe {
    public FletchingRecipe(IEventBus eventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);

        CommonClass.init();
        NeoForgeRegistryHelper.register(eventBus);

        ExplosiveIngredientConfig.load();
        ExplosiveIngredientConfig.startWatcher();

        eventBus.addListener(NeoForgeFletchingRecipeConfig::onConfigLoad);
        eventBus.addListener(NeoForgeFletchingRecipeConfig::onConfigReload);

        modContainer.registerConfig(ModConfig.Type.COMMON, NeoForgeFletchingRecipeConfig.CONFIG_SPEC);
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        ExplosiveIngredientConfig.stopWatcher();
    }

    @SubscribeEvent
    public void onPlayerInteractRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Block block = level.getBlockState(pos).getBlock();

        InteractionResult result = CommonClass.onRightClickBlock(block, level, player, pos);

        if (result == InteractionResult.SUCCESS){
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        CommonClass.onProjectileImpact(event.getProjectile(), event.getRayTraceResult());
    }

    @SubscribeEvent
    public void onOnDatapackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(CommonClass.FLETCHING_RECIPE_TYPE.get());

        if (event.getPlayer() instanceof ServerPlayer serverPlayer){
            NeoForgeFletchingRecipeConfig config = NeoForgeFletchingRecipeConfig.CONFIG;
            PacketDistributor.sendToPlayer(serverPlayer, new ClientBoundConfigSyncPacket(config.allowExplosiveCrafting.get(), config.tippedArrowCraftingAmount.get(), config.explosiveArrowCraftingAmount.get()));
        }
    }
}