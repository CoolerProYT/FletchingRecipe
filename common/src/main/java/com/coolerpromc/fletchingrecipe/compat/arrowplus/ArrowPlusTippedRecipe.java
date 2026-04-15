package com.coolerpromc.fletchingrecipe.compat.arrowplus;

import com.coolerpromc.arrowplus.datacomponent.ModDataComponents;
import com.coolerpromc.arrowplus.item.ModItems;
import com.coolerpromc.arrowplus.registry.ModRegistries;
import com.coolerpromc.fletchingrecipe.CommonClass;
import com.coolerpromc.fletchingrecipe.compat.jei.recipe.JeiExplosiveRecipe;
import com.coolerpromc.fletchingrecipe.compat.jei.recipe.JeiFletchingRecipe;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;
// TODO: Replace the ARROW_PLUS item call with arrowplus multi-loader call once it is updated
public class ArrowPlusTippedRecipe {
    public static void register(Holder<Potion> holder, List<JeiFletchingRecipe> list){
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (!holder.value().getEffects().isEmpty() && level != null){
            level.registryAccess().lookupOrThrow(ModRegistries.ARROW_DATA_KEY).asHolderIdMap().forEach(arrowDataHolder -> {
                ItemStack outputStack = new ItemStack(ModItems.ARROW_PLUS, ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount());
                outputStack.set(ModDataComponents.ARROW_DATA, arrowDataHolder);
                outputStack.set(DataComponents.POTION_CONTENTS, new PotionContents(holder));

                JeiFletchingRecipe recipe = new JeiFletchingRecipe(
                        List.of(new ItemStack(Items.LINGERING_POTION.builtInRegistryHolder(), 1, DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(holder)).build())),
                        List.of(new ItemStack(ModItems.ARROW_PLUS.builtInRegistryHolder(), ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount(), DataComponentPatch.builder().set(ModDataComponents.ARROW_DATA, arrowDataHolder).build())),
                        List.of(),
                        outputStack
                );

                list.add(recipe);
            });
        }
    }

    public static void registerExplosive(Holder<Item> explosiveIngredient, List<JeiExplosiveRecipe> list){
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (level != null){
            level.registryAccess().lookupOrThrow(ModRegistries.ARROW_DATA_KEY).asHolderIdMap().forEach(arrowDataHolder -> {
                ItemStack outputStack = new ItemStack(ModItems.ARROW_PLUS, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
                outputStack.set(ModDataComponents.ARROW_DATA, arrowDataHolder);
                outputStack.set(CommonClass.EXPLOSIVE.get(), explosiveIngredient);

                JeiExplosiveRecipe recipe = new JeiExplosiveRecipe(
                        new ItemStack(explosiveIngredient, 1),
                        new ItemStack(ModItems.ARROW_PLUS.builtInRegistryHolder(), ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount(), DataComponentPatch.builder().set(ModDataComponents.ARROW_DATA, arrowDataHolder).build()),
                        outputStack
                );

                list.add(recipe);
            });
        }
    }

    public static void registerExplosiveTipped(Holder<Potion> holder, Holder<Item> explosiveIngredient, List<JeiExplosiveRecipe> list){
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (!holder.value().getEffects().isEmpty() && level != null){
            level.registryAccess().lookupOrThrow(ModRegistries.ARROW_DATA_KEY).asHolderIdMap().forEach(arrowDataHolder -> {
                ItemStack outputStack = new ItemStack(ModItems.ARROW_PLUS, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
                outputStack.set(ModDataComponents.ARROW_DATA, arrowDataHolder);
                outputStack.set(DataComponents.POTION_CONTENTS, new PotionContents(holder));
                outputStack.set(CommonClass.EXPLOSIVE.get(), explosiveIngredient);

                JeiExplosiveRecipe recipe = new JeiExplosiveRecipe(
                        new ItemStack(explosiveIngredient, 1),
                        new ItemStack(ModItems.ARROW_PLUS.builtInRegistryHolder(), ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount(), DataComponentPatch.builder().set(ModDataComponents.ARROW_DATA, arrowDataHolder).set(DataComponents.POTION_CONTENTS, new PotionContents(holder)).build()),
                        outputStack
                );

                list.add(recipe);
            });
        }
    }
}