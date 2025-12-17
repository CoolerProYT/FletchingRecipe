package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableMenu;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.function.Supplier;

@Mod(FletchingRecipe.MODID)
public class FletchingRecipe {
    public static final String MODID = "fletchingrecipe";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, MODID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);

    public static final Supplier<MenuType<FletchingTableMenu>> FLETCHING_TABLE_MENU = MENU_TYPES.register("fletching_table", () -> IMenuTypeExtension.create(FletchingTableMenu::new));
    public static final Supplier<RecipeType<FletchingTableRecipe>> FLETCHING_RECIPE_TYPE = TYPES.register("fletching", () -> RecipeType.simple(Identifier.fromNamespaceAndPath(MODID, "fletching")));
    public static final Supplier<RecipeSerializer<FletchingTableRecipe>> FLETCHING_RECIPE_SERIALIZER = SERIALIZERS.register("fletching", () -> FletchingTableRecipe.Serializer.INSTANCE);

    public FletchingRecipe(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);

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

    @SubscribeEvent
    public void onOnDatapackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(FLETCHING_RECIPE_TYPE.get());
    }
}
