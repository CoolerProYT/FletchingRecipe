package com.coolerpromc.fletchingrecipe.compat.arrowplus;

import com.coolerpromc.arrowplus.item.ModItems;
import com.coolerpromc.arrowplus.registry.ModRegistries;
import com.coolerpromc.fletchingrecipe.compat.rei.explosive.ExplosiveDisplay;
import com.coolerpromc.fletchingrecipe.compat.rei.fletching.FletchingDisplay;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;

import java.util.List;

public class ReiTippedRecipe {
    public static void register(Holder<Potion> holder, DisplayRegistry registry){
        if (!holder.value().getEffects().isEmpty()){
            RegistryUtil.getRegistry(ModRegistries.ARROW_DATA_KEY).asHolderIdMap().forEach(arrowDataHolder -> {
                CompoundTag outputTag = new CompoundTag();
                arrowDataHolder.value().save(outputTag, arrowDataHolder.unwrapKey().get().location());
                outputTag.putString("Potion", BuiltInRegistries.POTION.getKey(holder.value()).toString());

                ItemStack outputStack = new ItemStack(ModItems.ARROW_PLUS.get(), ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount());
                outputStack.setTag(outputTag);

                CompoundTag lingeringTag = new CompoundTag();
                lingeringTag.putString("Potion", BuiltInRegistries.POTION.getKey(holder.value()).toString());

                CompoundTag arrowTag = new CompoundTag();
                arrowDataHolder.value().save(arrowTag, arrowDataHolder.unwrapKey().get().location());

                ItemStack lingeringPotion = new ItemStack(Items.LINGERING_POTION, 1);
                lingeringPotion.setTag(lingeringTag);

                ItemStack inputArrow = new ItemStack(ModItems.ARROW_PLUS.get(), ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount());
                inputArrow.setTag(arrowTag);

                List<EntryIngredient> input = List.of(
                        EntryIngredients.of(lingeringPotion),
                        EntryIngredients.of(inputArrow)
                );
                List<EntryIngredient> output = List.of(EntryIngredients.of(outputStack));

                registry.add(new FletchingDisplay(input, output));
            });
        }
    }

    public static void registerExplosive(Holder<Item> explosiveIngredient, DisplayRegistry registry){
        RegistryUtil.getRegistry(ModRegistries.ARROW_DATA_KEY).asHolderIdMap().forEach(arrowDataHolder -> {
            CompoundTag outputTag = new CompoundTag();
            arrowDataHolder.value().save(outputTag, arrowDataHolder.unwrapKey().get().location());
            outputTag.putString("explosive", explosiveIngredient.unwrapKey().get().location().toString());

            ItemStack outputStack = new ItemStack(ModItems.ARROW_PLUS.get(), ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            outputStack.setTag(outputTag);

            CompoundTag arrowTag = new CompoundTag();
            arrowDataHolder.value().save(arrowTag, arrowDataHolder.unwrapKey().get().location());

            ItemStack inputArrow = new ItemStack(ModItems.ARROW_PLUS.get(), ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            inputArrow.setTag(arrowTag);

            List<EntryIngredient> input = List.of(
                    EntryIngredients.of(new ItemStack(explosiveIngredient, 1)),
                    EntryIngredients.of(inputArrow)
            );
            List<EntryIngredient> output = List.of(EntryIngredients.of(outputStack));

            registry.add(new ExplosiveDisplay(input, output));
        });
    }

    public static void registerExplosiveTipped(Holder<Potion> holder, Holder<Item> explosiveIngredient, DisplayRegistry registry){
        if (!holder.value().getEffects().isEmpty()){
            RegistryUtil.getRegistry(ModRegistries.ARROW_DATA_KEY).asHolderIdMap().forEach(arrowDataHolder -> {
                CompoundTag outputTag = new CompoundTag();
                arrowDataHolder.value().save(outputTag, arrowDataHolder.unwrapKey().get().location());
                outputTag.putString("Potion", BuiltInRegistries.POTION.getKey(holder.value()).toString());
                outputTag.putString("explosive", explosiveIngredient.unwrapKey().get().location().toString());

                ItemStack outputStack = new ItemStack(ModItems.ARROW_PLUS.get(), ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
                outputStack.setTag(outputTag);

                CompoundTag inputArrowTag = new CompoundTag();
                arrowDataHolder.value().save(inputArrowTag, arrowDataHolder.unwrapKey().get().location());
                inputArrowTag.putString("Potion", BuiltInRegistries.POTION.getKey(holder.value()).toString());

                ItemStack inputArrow = new ItemStack(ModItems.ARROW_PLUS.get(), ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
                inputArrow.setTag(inputArrowTag);

                List<EntryIngredient> input = List.of(
                        EntryIngredients.of(new ItemStack(explosiveIngredient, 1)),
                        EntryIngredients.of(inputArrow)
                );
                List<EntryIngredient> output = List.of(EntryIngredients.of(outputStack));

                registry.add(new ExplosiveDisplay(input, output));
            });
        }
    }
}
