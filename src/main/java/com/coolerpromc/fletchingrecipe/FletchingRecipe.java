package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.compat.morefletchingtable.MoreFletchingTableCheck;
import com.coolerpromc.fletchingrecipe.config.ExplosiveIngredientConfig;
import com.coolerpromc.fletchingrecipe.config.FletchingRecipeConfig;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableMenu;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FletchingRecipe implements ModInitializer {
	public static final String MOD_ID = "fletchingrecipe";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ScreenHandlerType<FletchingTableMenu> FLETCHING_TABLE_MENU = Registry.register(Registries.SCREEN_HANDLER, Identifier.of(MOD_ID, "fletching_table"), new ScreenHandlerType<>(FletchingTableMenu::new, FeatureSet.empty()));
	public static final RecipeType<FletchingTableRecipe> FLETCHING_RECIPE_TYPE = Registry.register(Registries.RECIPE_TYPE, Identifier.of(MOD_ID, "fletching"), new RecipeType<FletchingTableRecipe>() {
		@Override
		public String toString() {
			return Identifier.of(MOD_ID, "fletching").toString();
		}
	});
	public static final RecipeSerializer<FletchingTableRecipe> FLETCHING_RECIPE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(MOD_ID, "fletching"), FletchingTableRecipe.Serializer.INSTANCE);
    public static final ComponentType<RegistryEntry<Item>> EXPLOSIVE = Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(MOD_ID, "explosive"), ComponentType.<RegistryEntry<Item>>builder().codec(Item.ENTRY_CODEC).packetCodec(Item.ENTRY_PACKET_CODEC).cache().build());

	@Override
	public void onInitialize() {
        ExplosiveIngredientConfig.load();
        ExplosiveIngredientConfig.startWatcher();

        FletchingRecipeConfig.init();

        ServerLifecycleEvents.SERVER_STOPPING.register(minecraftServer -> {
            ExplosiveIngredientConfig.stopWatcher();
            FletchingRecipeConfig.close();
        });

		UseBlockCallback.EVENT.register((player, level, hand, blockHitResult) -> {
			BlockPos pos = blockHitResult.getBlockPos();
			Block block = level.getBlockState(pos).getBlock();

			if (block == Blocks.FLETCHING_TABLE || (FabricLoader.getInstance().isModLoaded("lolmft") && MoreFletchingTableCheck.checkBlock(block)) && (player.getMainHandStack().isEmpty() || (!player.getMainHandStack().isEmpty() && !player.isSneaking()))){
				if (!level.isClient()){
					player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, inventory, player1) -> new FletchingTableMenu(i, inventory, ScreenHandlerContext.create(level, pos)), Text.translatable(block.getTranslationKey())));
				}
				return ActionResult.SUCCESS;
			}
			return ActionResult.PASS;
		});

        RecipeSynchronization.synchronizeRecipeSerializer(FLETCHING_RECIPE_SERIALIZER);
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((serverPlayerEntity, b) -> {
            ServerPlayNetworking.send(serverPlayerEntity, new ClientBoundConfigSyncPacket(FletchingRecipeConfig.allowExplosiveCrafting(), FletchingRecipeConfig.tippedArrowCraftingAmount(), FletchingRecipeConfig.explosiveArrowCraftingAmount()));
        });

        PayloadTypeRegistry.playS2C().register(ClientBoundConfigSyncPacket.TYPE, ClientBoundConfigSyncPacket.STREAM_CODEC);
	}
}