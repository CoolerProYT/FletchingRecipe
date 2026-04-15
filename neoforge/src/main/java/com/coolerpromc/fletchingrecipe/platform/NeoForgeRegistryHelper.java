package com.coolerpromc.fletchingrecipe.platform;

import com.coolerpromc.fletchingrecipe.Constants;
import com.coolerpromc.fletchingrecipe.platform.services.IRegistryHelper;
import com.coolerpromc.fletchingrecipe.platform.util.MenuFactory;
import com.coolerpromc.fletchingrecipe.platform.util.RegistryHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class NeoForgeRegistryHelper implements IRegistryHelper {
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, Constants.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Constants.MODID);
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Constants.MODID);
    private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Constants.MODID);

    @Override
    public <T extends AbstractContainerMenu, D> RegistryHandler<MenuType<T>> registerMenu(String name, MenuFactory<T, D> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> data) {
        DeferredHolder<MenuType<?>, MenuType<T>> menu = MENUS.register(name, () -> IMenuTypeExtension.create((id, inv, buf) -> factory.create(id, inv, data.decode(buf))));

        return new RegistryHandler<>() {
            @Override
            public Identifier id() {
                return menu.getId();
            }

            @Override
            public Holder<MenuType<T>> holder() {
                return (Holder<MenuType<T>>) (Holder<?>) menu.getDelegate();
            }

            @Override
            public MenuType<T> get() {
                return menu.get();
            }
        };
    }

    @Override
    public <T> RegistryHandler<DataComponentType<T>> registerDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DeferredHolder<DataComponentType<?>, DataComponentType<T>> component = COMPONENTS.register(name, () -> builder.apply(DataComponentType.builder()).build());

        return new RegistryHandler<>() {
            @Override
            public Identifier id() {
                return component.getId();
            }

            @Override
            public Holder<DataComponentType<T>> holder() {
                return (Holder<DataComponentType<T>>) (Holder<?>) component.getDelegate();
            }

            @Override
            public DataComponentType<T> get() {
                return component.get();
            }
        };
    }

    @Override
    public <T extends Recipe<?>> RegistryHandler<RecipeSerializer<T>> registerRecipeSerializer(String name, RecipeSerializer<T> serializer) {
        DeferredHolder<RecipeSerializer<?>, RecipeSerializer<T>> holder = SERIALIZERS.register(name, () -> serializer);

        return new RegistryHandler<>() {
            @Override
            public Identifier id() {
                return holder.getId();
            }

            @Override
            public Holder<RecipeSerializer<T>> holder() {
                return (Holder<RecipeSerializer<T>>) (Holder<?>) holder.getDelegate();
            }

            @Override
            public RecipeSerializer<T> get() {
                return holder.get();
            }
        };
    }

    @Override
    public <T extends Recipe<?>> RegistryHandler<RecipeType<T>> registerRecipeType(String name) {
        DeferredHolder<RecipeType<?>, RecipeType<T>> holder = TYPES.register(name, () -> RecipeType.simple(Constants.id(name)));

        return new RegistryHandler<>() {
            @Override
            public Identifier id() {
                return holder.getId();
            }

            @Override
            public Holder<RecipeType<T>> holder() {
                return (Holder<RecipeType<T>>) (Holder<?>) holder.getDelegate();
            }

            @Override
            public RecipeType<T> get() {
                return holder.get();
            }
        };
    }

    public static void register(IEventBus eventBus){
        MENUS.register(eventBus);
        COMPONENTS.register(eventBus);
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
