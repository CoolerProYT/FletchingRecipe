package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.config.ExplosiveIngredientConfig;
import com.coolerpromc.fletchingrecipe.config.FletchingRecipeConfig;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableMenu;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FletchingTableBlock;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.function.Supplier;

@SuppressWarnings("NullableProblems")
@Mod(FletchingRecipe.MODID)
public class FletchingRecipe {
    public static final String MODID = "fletchingrecipe";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);
    private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);

    public static final RegistryObject<MenuType<FletchingTableMenu>> FLETCHING_TABLE_MENU = MENU_TYPES.register("fletching_table", () -> IForgeMenuType.create(FletchingTableMenu::new));
    public static final RegistryObject<RecipeType<FletchingTableRecipe>> FLETCHING_RECIPE_TYPE = TYPES.register("fletching", () -> RecipeType.simple(new ResourceLocation(MODID, "fletching")));
    public static final RegistryObject<RecipeSerializer<FletchingTableRecipe>> FLETCHING_RECIPE_SERIALIZER = SERIALIZERS.register("fletching", () -> FletchingTableRecipe.Serializer.INSTANCE);
    public static final Supplier<DataComponentType<Holder<Item>>> EXPLOSIVE = COMPONENTS.registerComponentType("explosive", builder -> builder.persistent(ITEM_HOLDER_CODEC).networkSynchronized(ByteBufCodecs.holderRegistry(Registries.ITEM)).cacheEncoding());

    public FletchingRecipe() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);

        MENU_TYPES.register(modEventBus);
        SERIALIZERS.register(modEventBus);
        TYPES.register(modEventBus);
        COMPONENTS.register(modEventBus);

        ExplosiveIngredientConfig.load();
        ExplosiveIngredientConfig.startWatcher();

        modContainer.registerConfig(ModConfig.Type.COMMON, FletchingRecipeConfig.CONFIG_SPEC);
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

        if (block instanceof FletchingTableBlock && (player.getMainHandItem().isEmpty() || (!player.getMainHandItem().isEmpty() && !player.isShiftKeyDown()))){
            if (!level.isClientSide()){
                player.openMenu(new SimpleMenuProvider((i, inventory, player1) -> new FletchingTableMenu(i, inventory, ContainerLevelAccess.create(level, pos)), Component.translatable(block.getDescriptionId())));
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (!projectile.level().isClientSide() && projectile instanceof AbstractArrow abstractArrow && abstractArrow.getPickupItemStackOrigin().has(EXPLOSIVE)){
            Vec3 pos = event.getRayTraceResult().getLocation();
            float ratio = abstractArrow.isCritArrow() ? 1f : 2f;
            Holder<Item> explosiveItemHolder = abstractArrow.getPickupItemStackOrigin().get(EXPLOSIVE);
            abstractArrow.level().explode(null, pos.x, pos.y, pos.z, ExplosiveIngredientConfig.explosiveIngredients.get(explosiveItemHolder) / ratio, Level.ExplosionInteraction.TNT);
            if (event.getRayTraceResult() instanceof EntityHitResult result && result.getEntity() instanceof LivingEntity entity){
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
            PacketDistributor.sendToPlayer(serverPlayer, new ClientBoundConfigSyncPacket(config.allowExplosiveCrafting.get(), config.tippedArrowCraftingAmount.get(), config.explosiveArrowCraftingAmount.get()));
        }
    }
}
