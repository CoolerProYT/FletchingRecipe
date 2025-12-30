package com.coolerpromc.fletchingrecipe.compat.arrowplus;

import com.coolerpromc.arrowplus.datacomponent.ModDataComponents;
import com.coolerpromc.arrowplus.item.ModItems;
import com.coolerpromc.arrowplus.registry.ModRegistries;
import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.compat.rei.explosive.ExplosiveDisplay;
import com.coolerpromc.fletchingrecipe.compat.rei.fletching.FletchingDisplay;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;

public class ReiTippedRecipe {
    public static void register(Holder<Potion> holder, DisplayRegistry registry){
        if (!holder.value().getEffects().isEmpty()){
            RegistryUtil.getRegistry(ModRegistries.ARROW_DATA_KEY).asHolderIdMap().forEach(arrowDataHolder -> {
                ItemStack outputStack = new ItemStack(ModItems.ARROW_PLUS.get(), ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount());
                outputStack.set(ModDataComponents.ARROW_DATA.get(), arrowDataHolder);
                outputStack.set(DataComponents.POTION_CONTENTS, new PotionContents(holder));

                List<EntryIngredient> input = List.of(
                        EntryIngredients.of(new ItemStack(Items.LINGERING_POTION.builtInRegistryHolder(), 1, DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(holder)).build())),
                        EntryIngredients.of(new ItemStack(ModItems.ARROW_PLUS.get().builtInRegistryHolder(), ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount(), DataComponentPatch.builder().set(ModDataComponents.ARROW_DATA.get(), arrowDataHolder).build()))
                );
                List<EntryIngredient> output = List.of(EntryIngredients.of(outputStack));

                registry.add(new FletchingDisplay(input, output));
            });
        }
    }

    public static void registerExplosive(Holder<Item> explosiveIngredient, DisplayRegistry registry){
        RegistryUtil.getRegistry(ModRegistries.ARROW_DATA_KEY).asHolderIdMap().forEach(arrowDataHolder -> {
            ItemStack outputStack = new ItemStack(ModItems.ARROW_PLUS.get(), ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            outputStack.set(ModDataComponents.ARROW_DATA.get(), arrowDataHolder);
            outputStack.set(FletchingRecipe.EXPLOSIVE, explosiveIngredient);

            List<EntryIngredient> input = List.of(
                    EntryIngredients.of(new ItemStack(explosiveIngredient, 1)),
                    EntryIngredients.of(new ItemStack(ModItems.ARROW_PLUS.get().builtInRegistryHolder(), ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount(), DataComponentPatch.builder().set(ModDataComponents.ARROW_DATA.get(), arrowDataHolder).build()))
            );
            List<EntryIngredient> output = List.of(EntryIngredients.of(outputStack));

            registry.add(new ExplosiveDisplay(input, output));
        });
    }

    public static void registerExplosiveTipped(Holder<Potion> holder, Holder<Item> explosiveIngredient, DisplayRegistry registry){
        if (!holder.value().getEffects().isEmpty()){
            RegistryUtil.getRegistry(ModRegistries.ARROW_DATA_KEY).asHolderIdMap().forEach(arrowDataHolder -> {
                ItemStack outputStack = new ItemStack(ModItems.ARROW_PLUS.get(), ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
                outputStack.set(ModDataComponents.ARROW_DATA.get(), arrowDataHolder);
                outputStack.set(DataComponents.POTION_CONTENTS, new PotionContents(holder));
                outputStack.set(FletchingRecipe.EXPLOSIVE, explosiveIngredient);

                List<EntryIngredient> input = List.of(
                        EntryIngredients.of(new ItemStack(explosiveIngredient, 1)),
                        EntryIngredients.of(new ItemStack(ModItems.ARROW_PLUS.get().builtInRegistryHolder(), ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount(), DataComponentPatch.builder().set(ModDataComponents.ARROW_DATA.get(), arrowDataHolder).set(DataComponents.POTION_CONTENTS, new PotionContents(holder)).build()))
                );
                List<EntryIngredient> output = List.of(EntryIngredients.of(outputStack));

                registry.add(new ExplosiveDisplay(input, output));
            });
        }
    }
}
