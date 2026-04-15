package com.coolerpromc.fletchingrecipe.platform;

import com.coolerpromc.fletchingrecipe.Constants;
import com.coolerpromc.fletchingrecipe.platform.services.IRegistryHelper;
import com.coolerpromc.fletchingrecipe.platform.util.MenuFactory;
import com.coolerpromc.fletchingrecipe.platform.util.RegistryHandler;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.UnaryOperator;

public class FabricRegistryHelper implements IRegistryHelper {
    @Override
    public <T extends AbstractContainerMenu, D> RegistryHandler<MenuType<T>> registerMenu(String name, MenuFactory<T, D> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> data) {
        Identifier id = Constants.id(name);
        Holder<MenuType<T>> holder = Registry.registerForHolder(BuiltInRegistries.MENU, id, new ExtendedMenuType<>(factory::create, data));

        return new RegistryHandler<>() {
            @Override
            public Identifier id() {
                return id;
            }

            @Override
            public Holder<MenuType<T>> holder() {
                return holder;
            }

            @Override
            public MenuType<T> get() {
                return holder.value();
            }
        };
    }

    @Override
    public <T> RegistryHandler<DataComponentType<T>> registerDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        Identifier id = Constants.id(name);
        Holder<DataComponentType<T>> holder = Registry.registerForHolder(BuiltInRegistries.DATA_COMPONENT_TYPE, id, builder.apply(DataComponentType.builder()).build());

        return new RegistryHandler<>() {
            @Override
            public Identifier id() {
                return id;
            }

            @Override
            public Holder<DataComponentType<T>> holder() {
                return holder;
            }

            @Override
            public DataComponentType<T> get() {
                return holder.value();
            }
        };
    }

    @Override
    public <T extends Recipe<?>> RegistryHandler<RecipeSerializer<T>> registerRecipeSerializer(String name, RecipeSerializer<T> serializer) {
        Identifier id = Constants.id(name);
        Holder<RecipeSerializer<T>> holder = Registry.registerForHolder(BuiltInRegistries.RECIPE_SERIALIZER, id, serializer);

        return new RegistryHandler<>() {
            @Override
            public Identifier id() {
                return id;
            }

            @Override
            public Holder<RecipeSerializer<T>> holder() {
                return holder;
            }

            @Override
            public RecipeSerializer<T> get() {
                return holder.value();
            }
        };
    }

    @Override
    public <T extends Recipe<?>> RegistryHandler<RecipeType<T>> registerRecipeType(String name) {
        Identifier id = Constants.id(name);
        Holder<RecipeType<T>> holder = Registry.registerForHolder(BuiltInRegistries.RECIPE_TYPE, id, new RecipeType<T>() {
            @Override
            public String toString() {
                return id.toString();
            }
        });

        return new RegistryHandler<>() {
            @Override
            public Identifier id() {
                return id;
            }

            @Override
            public Holder<RecipeType<T>> holder() {
                return holder;
            }

            @Override
            public RecipeType<T> get() {
                return holder.value();
            }
        };
    }
}
