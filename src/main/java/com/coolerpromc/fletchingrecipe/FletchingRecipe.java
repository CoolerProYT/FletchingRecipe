package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.config.ExplosiveIngredientConfig;
import com.coolerpromc.fletchingrecipe.config.FletchingRecipeConfig;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableMenu;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.FletchingTableBlock;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
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

    public static final Codec<RegistryEntry<Item>> ITEM_HOLDER_CODEC = Registries.ITEM.getEntryCodec().validate((entry) -> entry.matches(Items.AIR.getRegistryEntry()) ? DataResult.error(() -> "Item must not be minecraft:air") : DataResult.success(entry));

	public static final ScreenHandlerType<FletchingTableMenu> FLETCHING_TABLE_MENU = Registry.register(Registries.SCREEN_HANDLER, Identifier.of(MOD_ID, "fletching_table"), new ScreenHandlerType<>(FletchingTableMenu::new, FeatureSet.empty()));
	public static final RecipeType<FletchingTableRecipe> FLETCHING_RECIPE_TYPE = Registry.register(Registries.RECIPE_TYPE, Identifier.of(MOD_ID, "fletching"), new RecipeType<FletchingTableRecipe>() {
		@Override
		public String toString() {
			return Identifier.of(MOD_ID, "fletching").toString();
		}
	});
	public static final RecipeSerializer<FletchingTableRecipe> FLETCHING_RECIPE_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(MOD_ID, "fletching"), FletchingTableRecipe.Serializer.INSTANCE);
    public static final ComponentType<RegistryEntry<Item>> EXPLOSIVE = Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(MOD_ID, "explosive"), ComponentType.<RegistryEntry<Item>>builder().codec(ITEM_HOLDER_CODEC).packetCodec(PacketCodecs.registryEntry(RegistryKeys.ITEM)).cache().build());

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

			if (block instanceof FletchingTableBlock && (player.getMainHandStack().isEmpty() || (!player.getMainHandStack().isEmpty() && !player.isSneaking()))){
				if (!level.isClient()){
					player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, inventory, player1) -> new FletchingTableMenu(i, inventory, ScreenHandlerContext.create(level, pos)), Text.translatable(block.getTranslationKey())));
				}
				return ActionResult.SUCCESS;
			}
			return ActionResult.PASS;
		});

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((serverPlayerEntity, b) -> {
            ServerPlayNetworking.send(serverPlayerEntity, new ClientBoundConfigSyncPacket(FletchingRecipeConfig.allowExplosiveCrafting(), FletchingRecipeConfig.tippedArrowCraftingAmount(), FletchingRecipeConfig.explosiveArrowCraftingAmount()));
        });

        PayloadTypeRegistry.playS2C().register(ClientBoundConfigSyncPacket.TYPE, ClientBoundConfigSyncPacket.STREAM_CODEC);
	}
}