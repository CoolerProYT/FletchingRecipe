package com.coolerpromc.fletchingrecipe.compat.rei;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.compat.arrowplus.ReiTippedRecipe;
import com.coolerpromc.fletchingrecipe.compat.rei.explosive.ExplosiveCategory;
import com.coolerpromc.fletchingrecipe.compat.rei.explosive.ExplosiveDisplay;
import com.coolerpromc.fletchingrecipe.compat.rei.fletching.FletchingCategory;
import com.coolerpromc.fletchingrecipe.compat.rei.fletching.FletchingDisplay;
import com.coolerpromc.fletchingrecipe.config.ExplosiveIngredientConfig;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.ModList;

import java.util.List;

@REIPluginClient
public class ModREIPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new FletchingCategory(), config -> config.addWorkstations(EntryStacks.of(Blocks.FLETCHING_TABLE)));
        registry.add(new ExplosiveCategory(), config -> config.addWorkstations(EntryStacks.of(Blocks.FLETCHING_TABLE)));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(screen -> new Rectangle(((screen.width - 176) / 2) + 89, ((screen.height - 166) / 2) + 34, 22, 16), FletchingTableScreen.class, FletchingDisplay.CATEGORY_IDENTIFIER);
        registry.registerClickArea(screen -> new Rectangle(((screen.width - 176) / 2) + 89, ((screen.height - 166) / 2) + 34, 22, 16), FletchingTableScreen.class, ExplosiveDisplay.CATEGORY_IDENTIFIER);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(FletchingTableRecipe.class, FletchingRecipe.FLETCHING_RECIPE_TYPE.get(), FletchingDisplay::new);

        BuiltInRegistries.POTION.asHolderIdMap().forEach(potion -> {
            if (ModList.get().isLoaded("arrowplus")) ReiTippedRecipe.register(potion, registry);

            ItemStack outputStack = PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW, ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount()), potion.value());

            CompoundTag tag = new CompoundTag();
            tag.putString("Potion", BuiltInRegistries.POTION.getKey(potion.value()).toString());

            List<EntryIngredient> input = List.of(
                    EntryIngredients.of(new ItemStack(Items.LINGERING_POTION, 1, tag)),
                    EntryIngredients.of(new ItemStack(Items.ARROW.builtInRegistryHolder(), ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount()))
            );
            List<EntryIngredient> output = List.of(EntryIngredients.of(outputStack));

            registry.add(new FletchingDisplay(input, output));
        });

        for (Holder<Item> holder : ExplosiveIngredientConfig.explosiveIngredients.keySet()){
            ItemStack explosiveIngredient = new ItemStack(holder);

            CompoundTag explosiveTag = new CompoundTag();
            explosiveTag.putString("explosive", holder.unwrapKey().get().location().toString());

            ItemStack arrow = Items.ARROW.getDefaultInstance();
            arrow.setCount(ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            ItemStack arrowOutputStack = new ItemStack(Items.ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            arrowOutputStack.setTag(explosiveTag.copy());
            List<EntryIngredient> arrowInput = List.of(
                    EntryIngredients.of(new ItemStack(holder)),
                    EntryIngredients.of(arrow)
            );
            List<EntryIngredient> arrowOutput = List.of(EntryIngredients.of(arrowOutputStack));
            registry.add(new ExplosiveDisplay(arrowInput, arrowOutput));

            ItemStack spectralArrow = Items.SPECTRAL_ARROW.getDefaultInstance();
            spectralArrow.setCount(ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            ItemStack spectralArrowOutputStack = new ItemStack(Items.SPECTRAL_ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            spectralArrowOutputStack.setTag(explosiveTag.copy());
            List<EntryIngredient> spectralArrowInput = List.of(
                    EntryIngredients.of(new ItemStack(holder)),
                    EntryIngredients.of(spectralArrow)
            );
            List<EntryIngredient> spectralArrowOutput = List.of(EntryIngredients.of(spectralArrowOutputStack));
            registry.add(new ExplosiveDisplay(spectralArrowInput, spectralArrowOutput));

            if (ModList.get().isLoaded("arrowplus")) ReiTippedRecipe.registerExplosive(holder, registry);

            BuiltInRegistries.POTION.asHolderIdMap().forEach(potion -> {
                if (ModList.get().isLoaded("arrowplus")) ReiTippedRecipe.registerExplosiveTipped(potion, holder, registry);

                CompoundTag potionTag = new CompoundTag();
                explosiveTag.putString("Potion", BuiltInRegistries.POTION.getKey(potion.value()).toString());

                ItemStack inputStack = new ItemStack(Items.TIPPED_ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
                inputStack.setTag(potionTag.copy());

                ItemStack outputStack = new ItemStack(Items.TIPPED_ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
                outputStack.setTag(potionTag.copy().merge(potionTag.copy()));

                List<EntryIngredient> input = List.of(
                        EntryIngredients.of(new ItemStack(holder)),
                        EntryIngredients.of(inputStack)
                );
                List<EntryIngredient> output = List.of(EntryIngredients.of(outputStack));

                registry.add(new ExplosiveDisplay(input, output));
            });
        }
    }
}
