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
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Arrays;
import java.util.List;

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
        registry.registerRecipeFiller(FletchingTableRecipe.class, FletchingRecipe.FLETCHING_RECIPE_TYPE, fletchingTableRecipeRecipeHolder -> {
            FletchingTableRecipe recipe = fletchingTableRecipeRecipeHolder.value();
            List<EntryIngredient> input;
            EntryIngredient top = EntryIngredients.ofItemStacks(Arrays.stream(recipe.top().ingredient().getMatchingStacks()).map(stack -> stack.copyWithCount(recipe.top().count())).toList());
            EntryIngredient middle = EntryIngredients.ofItemStacks(Arrays.stream(recipe.middle().ingredient().getMatchingStacks()).map(stack -> stack.copyWithCount(recipe.middle().count())).toList());
            if (recipe.bottom().isPresent()){
                EntryIngredient bottom = EntryIngredients.ofItemStacks(Arrays.stream(recipe.bottom().get().ingredient().getMatchingStacks()).map(stack -> stack.copyWithCount(recipe.bottom().get().count())).toList());
                input = List.of(top, middle, bottom);
            }
            else {
                input = List.of(top, middle);
            }

            List<EntryIngredient> output = List.of(EntryIngredients.of(recipe.output()));

            return new FletchingDisplay(input, output);
        });

        Registries.POTION.getIndexedEntries().forEach(potion -> {
            if (FabricLoader.getInstance().isModLoaded("arrowplus")) ReiTippedRecipe.register(potion, registry);

            ItemStack outputStack = new ItemStack(Items.TIPPED_ARROW, ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount());
            outputStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(potion));

            List<EntryIngredient> input = List.of(
                    EntryIngredients.of(new ItemStack(Items.LINGERING_POTION.getRegistryEntry(), 1, ComponentChanges.builder().add(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(potion)).build())),
                    EntryIngredients.of(new ItemStack(Items.ARROW.getRegistryEntry(), ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount()))
            );
            List<EntryIngredient> output = List.of(EntryIngredients.of(outputStack));

            registry.add(new FletchingDisplay(input, output));
        });

        for (RegistryEntry<Item> holder : ExplosiveIngredientConfig.explosiveIngredients.keySet()){
            ItemStack explosiveIngredient = new ItemStack(holder);

            ItemStack arrow = Items.ARROW.getDefaultStack();
            arrow.setCount(ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            ItemStack arrowOutputStack = new ItemStack(Items.ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            arrowOutputStack.set(FletchingRecipe.EXPLOSIVE, holder);
            List<EntryIngredient> arrowInput = List.of(
                    EntryIngredients.of(new ItemStack(holder)),
                    EntryIngredients.of(arrow)
            );
            List<EntryIngredient> arrowOutput = List.of(EntryIngredients.of(arrowOutputStack));
            registry.add(new ExplosiveDisplay(arrowInput, arrowOutput));

            ItemStack spectralArrow = Items.SPECTRAL_ARROW.getDefaultStack();
            spectralArrow.setCount(ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            ItemStack spectralArrowOutputStack = new ItemStack(Items.SPECTRAL_ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            spectralArrowOutputStack.set(FletchingRecipe.EXPLOSIVE, holder);
            List<EntryIngredient> spectralArrowInput = List.of(
                    EntryIngredients.of(new ItemStack(holder)),
                    EntryIngredients.of(spectralArrow)
            );
            List<EntryIngredient> spectralArrowOutput = List.of(EntryIngredients.of(spectralArrowOutputStack));
            registry.add(new ExplosiveDisplay(spectralArrowInput, spectralArrowOutput));

            if (FabricLoader.getInstance().isModLoaded("arrowplus")) ReiTippedRecipe.registerExplosive(holder, registry);

            Registries.POTION.getIndexedEntries().forEach(potion -> {
                if (FabricLoader.getInstance().isModLoaded("arrowplus")) ReiTippedRecipe.registerExplosiveTipped(potion, holder, registry);

                ItemStack inputStack = new ItemStack(Items.TIPPED_ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
                inputStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(potion));

                ItemStack outputStack = new ItemStack(Items.TIPPED_ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
                outputStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(potion));
                outputStack.set(FletchingRecipe.EXPLOSIVE, holder);

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