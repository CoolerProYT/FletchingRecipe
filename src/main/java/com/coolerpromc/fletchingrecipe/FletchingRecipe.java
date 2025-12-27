package com.coolerpromc.fletchingrecipe;

import com.coolerpromc.fletchingrecipe.config.ExplosiveIngredientConfig;
import com.coolerpromc.fletchingrecipe.config.FletchingRecipeConfig;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableMenu;
import com.coolerpromc.fletchingrecipe.util.ArrowStack;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FletchingTableBlock;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.function.Predicate;

@SuppressWarnings("NullableProblems")
@Mod(FletchingRecipe.MODID)
public class FletchingRecipe {
    public static final String MODID = "fletchingrecipe";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);

    public static final RegistryObject<MenuType<FletchingTableMenu>> FLETCHING_TABLE_MENU = MENU_TYPES.register("fletching_table", () -> IForgeMenuType.create(FletchingTableMenu::new));
    public static final RegistryObject<RecipeType<FletchingTableRecipe>> FLETCHING_RECIPE_TYPE = TYPES.register("fletching", () -> RecipeType.simple(new ResourceLocation(MODID, "fletching")));
    public static final RegistryObject<RecipeSerializer<FletchingTableRecipe>> FLETCHING_RECIPE_SERIALIZER = SERIALIZERS.register("fletching", () -> FletchingTableRecipe.Serializer.INSTANCE);

    public FletchingRecipe() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::commonSetup);

        MENU_TYPES.register(modEventBus);
        SERIALIZERS.register(modEventBus);
        TYPES.register(modEventBus);

        ExplosiveIngredientConfig.load();
        ExplosiveIngredientConfig.startWatcher();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FletchingRecipeConfig.CONFIG_SPEC);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ClientBoundConfigSyncPacket::register);
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingGetProjectile(LivingGetProjectileEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack weapon = event.getProjectileWeaponItemStack();
        ItemStack fired = event.getProjectileItemStack();

        if (!(fired.is(Items.SPECTRAL_ARROW) || weapon.is(Items.ARROW))) return;

        for(int i = 0; i < player.getInventory().getContainerSize(); ++i) {
            Predicate<ItemStack> predicate = ((ProjectileWeaponItem)weapon.getItem()).getSupportedHeldProjectiles();
            ItemStack itemstack1 = player.getInventory().getItem(i);
            if (predicate.test(itemstack1)) {
                event.setProjectileItemStack(player.getInventory().items.get(i));
                return;
            }
        }
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        ItemStack arrowStack = ItemStack.EMPTY;

        if (!projectile.level().isClientSide() && projectile instanceof AbstractArrow abstractArrow && abstractArrow.getPickupItem().hasTag() && abstractArrow.getPickupItem().getTag().contains("explosive")){
            arrowStack = abstractArrow.getPickupItem();
        }

        if(!projectile.level().isClientSide() && projectile instanceof ArrowStack abstractArrow && abstractArrow.getPickupStack().hasTag() && abstractArrow.getPickupStack().getTag().contains("explosive")){
            arrowStack = abstractArrow.getPickupStack();
        }

        if (!projectile.level().isClientSide() && projectile instanceof AbstractArrow abstractArrow && !arrowStack.isEmpty()){
            Vec3 pos = event.getRayTraceResult().getLocation();
            float ratio = abstractArrow.isCritArrow() ? 1f : 2f;
            Holder<Item> explosiveItemHolder = abstractArrow.level().registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(ResourceKey.create(Registries.ITEM, new ResourceLocation(arrowStack.getTag().getString("explosive"))));
            abstractArrow.level().explode(null, pos.x, pos.y, pos.z, ExplosiveIngredientConfig.explosiveIngredients.get(explosiveItemHolder) / ratio, Level.ExplosionInteraction.TNT);
            if (event.getRayTraceResult() instanceof EntityHitResult result && result.getEntity() instanceof LivingEntity entity){
                abstractArrow.doPostHurtEffects(entity);
            }
            arrowStack.shrink(1);
            abstractArrow.remove(Entity.RemovalReason.KILLED);
        }
    }

    @SubscribeEvent
    public void onOnDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null){
            ServerPlayer serverPlayer = event.getPlayer();
            FletchingRecipeConfig config = FletchingRecipeConfig.CONFIG;
            ClientBoundConfigSyncPacket.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ClientBoundConfigSyncPacket(config.allowExplosiveCrafting.get(), config.tippedArrowCraftingAmount.get(), config.explosiveArrowCraftingAmount.get()));
        }
    }
}
