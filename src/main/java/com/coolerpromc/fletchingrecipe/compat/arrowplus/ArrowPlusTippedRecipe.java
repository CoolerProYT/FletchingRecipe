package com.coolerpromc.fletchingrecipe.compat.arrowplus;

import com.coolerpromc.arrowplus.datacomponent.ModDataComponents;
import com.coolerpromc.arrowplus.item.ModItems;
import com.coolerpromc.arrowplus.registry.ModRegistries;
import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.compat.jei.recipe.JeiExplosiveRecipe;
import com.coolerpromc.fletchingrecipe.compat.jei.recipe.JeiFletchingRecipe;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;

public class ArrowPlusTippedRecipe {
    public static void register(RegistryEntry<Potion> holder, List<JeiFletchingRecipe> list){
        MinecraftClient minecraft = MinecraftClient.getInstance();
        ClientWorld level = minecraft.world;

        if (!holder.value().getEffects().isEmpty() && level != null){
            level.getRegistryManager().get(ModRegistries.ARROW_DATA_KEY).getIndexedEntries().forEach(arrowDataHolder -> {
                ItemStack outputStack = new ItemStack(ModItems.ARROW_PLUS, ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount());
                outputStack.set(ModDataComponents.ARROW_DATA, arrowDataHolder);
                outputStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(holder));

                JeiFletchingRecipe recipe = new JeiFletchingRecipe(
                        List.of(new ItemStack(Items.LINGERING_POTION.getRegistryEntry(), 1, ComponentChanges.builder().add(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(holder)).build())),
                        List.of(new ItemStack(ModItems.ARROW_PLUS.getRegistryEntry(), ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount(), ComponentChanges.builder().add(ModDataComponents.ARROW_DATA, arrowDataHolder).build())),
                        List.of(),
                        outputStack
                );

                list.add(recipe);
            });
        }
    }

    public static void registerExplosive(RegistryEntry<Item> explosiveIngredient, List<JeiExplosiveRecipe> list){
        MinecraftClient minecraft = MinecraftClient.getInstance();
        ClientWorld level = minecraft.world;

        if (level != null){
            level.getRegistryManager().get(ModRegistries.ARROW_DATA_KEY).getIndexedEntries().forEach(arrowDataHolder -> {
                ItemStack outputStack = new ItemStack(ModItems.ARROW_PLUS, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
                outputStack.set(ModDataComponents.ARROW_DATA, arrowDataHolder);
                outputStack.set(FletchingRecipe.EXPLOSIVE, explosiveIngredient);

                JeiExplosiveRecipe recipe = new JeiExplosiveRecipe(
                        new ItemStack(explosiveIngredient, 1),
                        new ItemStack(ModItems.ARROW_PLUS.getRegistryEntry(), ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount(), ComponentChanges.builder().add(ModDataComponents.ARROW_DATA, arrowDataHolder).build()),
                        outputStack
                );

                list.add(recipe);
            });
        }
    }

    public static void registerExplosiveTipped(RegistryEntry<Potion> holder, RegistryEntry<Item> explosiveIngredient, List<JeiExplosiveRecipe> list){
        MinecraftClient minecraft = MinecraftClient.getInstance();
        ClientWorld level = minecraft.world;

        if (!holder.value().getEffects().isEmpty() && level != null){
            level.getRegistryManager().get(ModRegistries.ARROW_DATA_KEY).getIndexedEntries().forEach(arrowDataHolder -> {
                ItemStack outputStack = new ItemStack(ModItems.ARROW_PLUS, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
                outputStack.set(ModDataComponents.ARROW_DATA, arrowDataHolder);
                outputStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(holder));
                outputStack.set(FletchingRecipe.EXPLOSIVE, explosiveIngredient);

                JeiExplosiveRecipe recipe = new JeiExplosiveRecipe(
                        new ItemStack(explosiveIngredient, 1),
                        new ItemStack(ModItems.ARROW_PLUS.getRegistryEntry(), ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount(), ComponentChanges.builder().add(ModDataComponents.ARROW_DATA, arrowDataHolder).add(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(holder)).build()),
                        outputStack
                );

                list.add(recipe);
            });
        }
    }
}