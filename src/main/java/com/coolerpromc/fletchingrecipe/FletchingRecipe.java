package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableMenu;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.function.Supplier;

@Mod(FletchingRecipe.MODID)
public class FletchingRecipe {
    public static final String MODID = "fletchingrecipe";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);

    public static final Supplier<MenuType<FletchingTableMenu>> FLETCHING_TABLE_MENU = MENU_TYPES.register("fletching_table", () -> IForgeMenuType.create(FletchingTableMenu::new));
    public static final Supplier<RecipeType<FletchingTableRecipe>> FLETCHING_RECIPE_TYPE = TYPES.register("fletching", () -> RecipeType.simple(new ResourceLocation(MODID, "fletching")));
    public static final Supplier<RecipeSerializer<FletchingTableRecipe>> FLETCHING_RECIPE_SERIALIZER = SERIALIZERS.register("fletching", () -> FletchingTableRecipe.Serializer.INSTANCE);

    public FletchingRecipe() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);

        MENU_TYPES.register(modEventBus);
        SERIALIZERS.register(modEventBus);
        TYPES.register(modEventBus);
    }

    @SubscribeEvent
    public void onPlayerInteractRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Block block = level.getBlockState(pos).getBlock();

        if (block == Blocks.FLETCHING_TABLE && (player.getMainHandItem().isEmpty() || (!player.getMainHandItem().isEmpty() && !player.isShiftKeyDown()))){
            if (!level.isClientSide()){
                player.openMenu(new SimpleMenuProvider((i, inventory, player1) -> new FletchingTableMenu(i, inventory, ContainerLevelAccess.create(level, pos)), Component.translatable("block.minecraft.fletching_table")));
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    @Mod.EventBusSubscriber(modid = FletchingRecipe.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class FletchingRecipeClient {
        @SubscribeEvent
        public static void onFMLClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                MenuScreens.register(FletchingRecipe.FLETCHING_TABLE_MENU.get(), FletchingTableScreen::new);
            });
        }
    }
}
