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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FletchingRecipe implements ModInitializer {
	public static final String MOD_ID = "fletchingrecipe";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final MenuType<FletchingTableMenu> FLETCHING_TABLE_MENU = Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(MOD_ID, "fletching_table"), new MenuType<>(FletchingTableMenu::new, FeatureFlagSet.of()));
	public static final RecipeType<FletchingTableRecipe> FLETCHING_RECIPE_TYPE = Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, "fletching"), new RecipeType<FletchingTableRecipe>() {
		@Override
		public String toString() {
			return Identifier.fromNamespaceAndPath(MOD_ID, "fletching").toString();
		}
	});
	public static final RecipeSerializer<FletchingTableRecipe> FLETCHING_RECIPE_SERIALIZER = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(MOD_ID, "fletching"), FletchingTableRecipe.SERIALIZER);
    public static final DataComponentType<Holder<Item>> EXPLOSIVE = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, "explosive"), DataComponentType.<Holder<Item>>builder().persistent(Item.CODEC).networkSynchronized(Item.STREAM_CODEC).cacheEncoding().build());

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

			if (block == Blocks.FLETCHING_TABLE || (FabricLoader.getInstance().isModLoaded("lolmft") && MoreFletchingTableCheck.checkBlock(block)) && (player.getMainHandItem().isEmpty() || (!player.getMainHandItem().isEmpty() && !player.isShiftKeyDown()))){
				if (!level.isClientSide()){
					player.openMenu(new SimpleMenuProvider((i, inventory, player1) -> new FletchingTableMenu(i, inventory, ContainerLevelAccess.create(level, pos)), Component.translatable(block.getDescriptionId())));
				}
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		});

        RecipeSynchronization.synchronizeRecipeSerializer(FLETCHING_RECIPE_SERIALIZER);
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((serverPlayerEntity, b) -> {
            ServerPlayNetworking.send(serverPlayerEntity, new ClientBoundConfigSyncPacket(FletchingRecipeConfig.allowExplosiveCrafting(), FletchingRecipeConfig.tippedArrowCraftingAmount(), FletchingRecipeConfig.explosiveArrowCraftingAmount()));
        });

        PayloadTypeRegistry.clientboundPlay().register(ClientBoundConfigSyncPacket.TYPE, ClientBoundConfigSyncPacket.STREAM_CODEC);
	}
}