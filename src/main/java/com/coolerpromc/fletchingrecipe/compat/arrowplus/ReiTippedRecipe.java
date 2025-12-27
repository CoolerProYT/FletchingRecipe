package com.coolerpromc.fletchingrecipe.compat.arrowplus;

import com.coolerpromc.arrowplus.datacomponent.ModDataComponents;
import com.coolerpromc.arrowplus.item.ModItems;
import com.coolerpromc.arrowplus.registry.ModRegistries;
import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.compat.rei.explosive.ExplosiveDisplay;
import com.coolerpromc.fletchingrecipe.compat.rei.fletching.FletchingDisplay;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;

public class ReiTippedRecipe {
    public static void register(RegistryEntry<Potion> holder, DisplayRegistry registry){
        if (!holder.value().getEffects().isEmpty()){
            BasicDisplay.registryAccess().get(ModRegistries.ARROW_DATA_KEY).getIndexedEntries().forEach(arrowDataHolder -> {
                ItemStack outputStack = new ItemStack(ModItems.ARROW_PLUS, ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount());
                outputStack.set(ModDataComponents.ARROW_DATA, arrowDataHolder);
                outputStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(holder));

                List<EntryIngredient> input = List.of(
                        EntryIngredients.of(new ItemStack(Items.LINGERING_POTION.getRegistryEntry(), 1, ComponentChanges.builder().add(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(holder)).build())),
                        EntryIngredients.of(new ItemStack(ModItems.ARROW_PLUS.getRegistryEntry(), ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount(), ComponentChanges.builder().add(ModDataComponents.ARROW_DATA, arrowDataHolder).build()))
                );
                List<EntryIngredient> output = List.of(EntryIngredients.of(outputStack));

                registry.add(new FletchingDisplay(input, output));
            });
        }
    }

    public static void registerExplosive(RegistryEntry<Item> explosiveIngredient, DisplayRegistry registry){
        BasicDisplay.registryAccess().get(ModRegistries.ARROW_DATA_KEY).getIndexedEntries().forEach(arrowDataHolder -> {
            ItemStack outputStack = new ItemStack(ModItems.ARROW_PLUS, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            outputStack.set(ModDataComponents.ARROW_DATA, arrowDataHolder);
            outputStack.set(FletchingRecipe.EXPLOSIVE, explosiveIngredient);

            List<EntryIngredient> input = List.of(
                    EntryIngredients.of(new ItemStack(explosiveIngredient, 1)),
                    EntryIngredients.of(new ItemStack(ModItems.ARROW_PLUS.getRegistryEntry(), ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount(), ComponentChanges.builder().add(ModDataComponents.ARROW_DATA, arrowDataHolder).build()))
            );
            List<EntryIngredient> output = List.of(EntryIngredients.of(outputStack));

            registry.add(new ExplosiveDisplay(input, output));
        });
    }

    public static void registerExplosiveTipped(RegistryEntry<Potion> holder, RegistryEntry<Item> explosiveIngredient, DisplayRegistry registry){
        if (!holder.value().getEffects().isEmpty()){
            BasicDisplay.registryAccess().get(ModRegistries.ARROW_DATA_KEY).getIndexedEntries().forEach(arrowDataHolder -> {
                ItemStack outputStack = new ItemStack(ModItems.ARROW_PLUS, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
                outputStack.set(ModDataComponents.ARROW_DATA, arrowDataHolder);
                outputStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(holder));
                outputStack.set(FletchingRecipe.EXPLOSIVE, explosiveIngredient);

                List<EntryIngredient> input = List.of(
                        EntryIngredients.of(new ItemStack(explosiveIngredient, 1)),
                        EntryIngredients.of(new ItemStack(ModItems.ARROW_PLUS.getRegistryEntry(), ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount(), ComponentChanges.builder().add(ModDataComponents.ARROW_DATA, arrowDataHolder).add(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(holder)).build()))
                );
                List<EntryIngredient> output = List.of(EntryIngredients.of(outputStack));

                registry.add(new ExplosiveDisplay(input, output));
            });
        }
    }
}