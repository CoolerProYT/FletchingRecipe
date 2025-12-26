package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.config.ExplosiveIngredientConfig;
import com.coolerpromc.fletchingrecipe.config.FletchingRecipeConfig;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableMenu;
import com.coolerpromc.fletchingrecipe.util.DataComponentIngredient;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.crafting.ingredients.IIngredientSerializer;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.function.Supplier;

@SuppressWarnings("NullableProblems")
@Mod(FletchingRecipe.MODID)
public final class FletchingRecipe {
    public static final String MODID = "fletchingrecipe";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final Channel<CustomPacketPayload> CHANNEL = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(MODID, "channel_registration"))
            .payloadChannel()
            .play()
            .clientbound()
            .addMain(ClientBoundConfigSyncPacket.TYPE, ClientBoundConfigSyncPacket.STREAM_CODEC, ClientBoundConfigSyncPacket::handle)
            .build();

    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MODID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);
    public static final DeferredRegister<IIngredientSerializer<?>> INGREDIENT_SERIALIZERS = DeferredRegister.create(ForgeRegistries.INGREDIENT_SERIALIZERS, MODID);
    private static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);

    public static final RegistryObject<IIngredientSerializer<DataComponentIngredient>> DATA_COMPONENT_INGREDIENT = INGREDIENT_SERIALIZERS.register("data_component", DataComponentIngredient.Serializer::new);
    public static final Supplier<MenuType<FletchingTableMenu>> FLETCHING_TABLE_MENU = MENU_TYPES.register("fletching_table", () -> IForgeMenuType.create(FletchingTableMenu::new));
    public static final Supplier<RecipeType<FletchingTableRecipe>> FLETCHING_RECIPE_TYPE = TYPES.register("fletching", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(MODID, "fletching")));
    public static final Supplier<RecipeSerializer<FletchingTableRecipe>> FLETCHING_RECIPE_SERIALIZER = SERIALIZERS.register("fletching", () -> FletchingTableRecipe.Serializer.INSTANCE);
    public static final Supplier<DataComponentType<Holder<Item>>> EXPLOSIVE = COMPONENTS.register("explosive", () -> new DataComponentType.Builder<Holder<Item>>().persistent(Item.CODEC).networkSynchronized(Item.STREAM_CODEC).cacheEncoding().build());

    public FletchingRecipe(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();
        BusGroup.DEFAULT.register(MethodHandles.lookup(), this);

        MENU_TYPES.register(modBusGroup);
        SERIALIZERS.register(modBusGroup);
        TYPES.register(modBusGroup);
        INGREDIENT_SERIALIZERS.register(modBusGroup);
        COMPONENTS.register(modBusGroup);

        ExplosiveIngredientConfig.load();
        ExplosiveIngredientConfig.startWatcher();

        context.registerConfig(ModConfig.Type.COMMON, FletchingRecipeConfig.CONFIG_SPEC);
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        ExplosiveIngredientConfig.stopWatcher();
    }

    @SubscribeEvent
    public boolean onPlayerInteractRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Block block = level.getBlockState(pos).getBlock();

        if (block == Blocks.FLETCHING_TABLE && (player.getMainHandItem().isEmpty() || (!player.getMainHandItem().isEmpty() && !player.isShiftKeyDown()))){
            if (!level.isClientSide()){
                player.openMenu(new SimpleMenuProvider((i, inventory, player1) -> new FletchingTableMenu(i, inventory, ContainerLevelAccess.create(level, pos)), Component.translatable("block.minecraft.fletching_table")));
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (!projectile.level().isClientSide() && projectile instanceof AbstractArrow abstractArrow && abstractArrow.getPickupItemStackOrigin().has(EXPLOSIVE.get())){
            Vec3 pos = event.getRayTraceResult().getLocation();
            float ratio = abstractArrow.isCritArrow() ? 1f : 2f;
            Holder<Item> explosiveItemHolder = abstractArrow.getPickupItemStackOrigin().get(EXPLOSIVE.get());
            abstractArrow.level().explode(null, pos.x, pos.y, pos.z, ExplosiveIngredientConfig.explosiveIngredients.get(explosiveItemHolder) / ratio, Level.ExplosionInteraction.TNT);
            if (event.getRayTraceResult() instanceof EntityHitResult result && result.getEntity().asLivingEntity() instanceof LivingEntity entity){
                abstractArrow.doPostHurtEffects(entity);
            }
            abstractArrow.getPickupItemStackOrigin().shrink(1);
            abstractArrow.remove(Entity.RemovalReason.KILLED);
        }
    }

    @SubscribeEvent
    public void onOnDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer){
            FletchingRecipeConfig config = FletchingRecipeConfig.CONFIG;
            CHANNEL.send(new ClientBoundConfigSyncPacket(config.allowExplosiveCrafting.get(), config.tippedArrowCraftingAmount.get(), config.explosiveArrowCraftingAmount.get()), PacketDistributor.PLAYER.with(serverPlayer));
        }
    }
}
