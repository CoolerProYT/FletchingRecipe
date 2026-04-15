package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.config.ExplosiveIngredientConfig;
import com.coolerpromc.fletchingrecipe.config.FabricFletchingRecipeConfig;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public class FletchingRecipe implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonClass.init();

        ExplosiveIngredientConfig.load();
        ExplosiveIngredientConfig.startWatcher();

        FabricFletchingRecipeConfig.init();

        ServerLifecycleEvents.SERVER_STOPPING.register(_ -> {
            ExplosiveIngredientConfig.stopWatcher();
            FabricFletchingRecipeConfig.close();
        });

        RecipeSynchronization.synchronizeRecipeSerializer(CommonClass.FLETCHING_RECIPE_SERIALIZER.get());
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((serverPlayerEntity, b) -> {
            ServerPlayNetworking.send(serverPlayerEntity, new ClientBoundConfigSyncPacket(FabricFletchingRecipeConfig.allowExplosiveCrafting(), FabricFletchingRecipeConfig.tippedArrowCraftingAmount(), FabricFletchingRecipeConfig.explosiveArrowCraftingAmount()));
        });

        PayloadTypeRegistry.clientboundPlay().register(ClientBoundConfigSyncPacket.TYPE, ClientBoundConfigSyncPacket.STREAM_CODEC);

        UseBlockCallback.EVENT.register((player, level, _, blockHitResult) -> {
            BlockPos pos = blockHitResult.getBlockPos();
            Block block = level.getBlockState(pos).getBlock();

            return CommonClass.onRightClickBlock(block, level, player, pos);
        });
    }
}
