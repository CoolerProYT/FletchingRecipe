package com.coolerpromc.fletchingrecipe.compat.jei;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.compat.arrowplus.ArrowPlusTippedRecipe;
import com.coolerpromc.fletchingrecipe.compat.jei.category.ExplosiveCategory;
import com.coolerpromc.fletchingrecipe.compat.jei.category.FletchingCategory;
import com.coolerpromc.fletchingrecipe.compat.jei.recipe.JeiExplosiveRecipe;
import com.coolerpromc.fletchingrecipe.compat.jei.recipe.JeiFletchingRecipe;
import com.coolerpromc.fletchingrecipe.compat.jei.subtype.ExplosiveSubtypeInterpreter;
import com.coolerpromc.fletchingrecipe.config.ExplosiveIngredientConfig;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JeiPlugin
public class ModJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(FletchingRecipe.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(new FletchingCategory(guiHelper), new ExplosiveCategory(guiHelper));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(Blocks.FLETCHING_TABLE.asItem().getDefaultInstance(), FletchingCategory.FLETCHING_TYPE);
        registration.addRecipeCatalyst(Blocks.FLETCHING_TABLE.asItem().getDefaultInstance(), ExplosiveCategory.EXPLOSIVE_TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<JeiFletchingRecipe> fletchingTableRecipes = new ArrayList<>(Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(FletchingRecipe.FLETCHING_RECIPE_TYPE.get()).stream().map(recipe -> {
            List<ItemStack> bottom = List.of();
            if (recipe.bottom().isPresent()){
                bottom = Arrays.stream(recipe.bottom().get().ingredient().getItems()).map(stack -> stack.copyWithCount(recipe.bottom().get().count())).toList();
            }
            return new JeiFletchingRecipe(
                    Arrays.stream(recipe.top().ingredient().getItems()).map(stack -> stack.copyWithCount(recipe.top().count())).toList(),
                    Arrays.stream(recipe.middle().ingredient().getItems()).map(stack -> stack.copyWithCount(recipe.middle().count())).toList(),
                    bottom,
                    recipe.output()
            );
        }).toList());

        BuiltInRegistries.POTION.asHolderIdMap().forEach(potion -> {
            if (ModList.get().isLoaded("arrowplus")) ArrowPlusTippedRecipe.register(potion, fletchingTableRecipes);

            ItemStack outputStack = PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW, ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount()), potion.value());

            CompoundTag tag = new CompoundTag();
            tag.putString("Potion", BuiltInRegistries.POTION.getKey(potion.value()).toString());

            JeiFletchingRecipe recipe = new JeiFletchingRecipe(
                    List.of(new ItemStack(Items.LINGERING_POTION, 1, tag)),
                    List.of(new ItemStack(Items.ARROW.builtInRegistryHolder(), ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount())),
                    List.of(),
                    outputStack
            );

            fletchingTableRecipes.add(recipe);
        });

        registration.addRecipes(FletchingCategory.FLETCHING_TYPE, fletchingTableRecipes);

        List<JeiExplosiveRecipe> explosiveRecipes = new ArrayList<>();

        for (Holder<Item> holder : ExplosiveIngredientConfig.explosiveIngredients.keySet()){
            ItemStack explosiveIngredient = new ItemStack(holder);

            CompoundTag explosiveTag = new CompoundTag();
            explosiveTag.putString("explosive", holder.unwrapKey().get().location().toString());

            ItemStack arrow = Items.ARROW.getDefaultInstance();
            arrow.setCount(ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            ItemStack arrowOutputStack = new ItemStack(Items.ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            arrowOutputStack.setTag(explosiveTag.copy());
            explosiveRecipes.add(new JeiExplosiveRecipe(explosiveIngredient, arrow, arrowOutputStack));

            ItemStack spectralArrow = Items.SPECTRAL_ARROW.getDefaultInstance();
            spectralArrow.setCount(ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            ItemStack spectralArrowOutputStack = new ItemStack(Items.SPECTRAL_ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            spectralArrowOutputStack.setTag(explosiveTag.copy());
            explosiveRecipes.add(new JeiExplosiveRecipe(explosiveIngredient, spectralArrow, spectralArrowOutputStack));

            if (ModList.get().isLoaded("arrowplus")) ArrowPlusTippedRecipe.registerExplosive(holder, explosiveRecipes);

            BuiltInRegistries.POTION.asHolderIdMap().forEach(potion -> {
                if (ModList.get().isLoaded("arrowplus")) ArrowPlusTippedRecipe.registerExplosiveTipped(potion, holder, explosiveRecipes);

                CompoundTag potionTag = new CompoundTag();
                explosiveTag.putString("Potion", BuiltInRegistries.POTION.getKey(potion.value()).toString());

                ItemStack inputStack = new ItemStack(Items.TIPPED_ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
                inputStack.setTag(potionTag.copy());

                ItemStack outputStack = new ItemStack(Items.TIPPED_ARROW, ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
                outputStack.setTag(potionTag.copy().merge(potionTag.copy()));

                JeiExplosiveRecipe recipe = new JeiExplosiveRecipe(
                        new ItemStack(holder),
                        inputStack,
                        outputStack
                );

                explosiveRecipes.add(recipe);
            });
        }

        registration.addRecipes(ExplosiveCategory.EXPLOSIVE_TYPE, explosiveRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(FletchingTableScreen.class, 81, 35, 22, 16, FletchingCategory.FLETCHING_TYPE);
        registration.addRecipeClickArea(FletchingTableScreen.class, 81, 35, 22, 16, ExplosiveCategory.EXPLOSIVE_TYPE);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(Items.ARROW, ExplosiveSubtypeInterpreter.INSTANCE);
        registration.registerSubtypeInterpreter(Items.SPECTRAL_ARROW, ExplosiveSubtypeInterpreter.INSTANCE);
//        registration.registerSubtypeInterpreter(Items.TIPPED_ARROW, ExplosiveSubtypeInterpreter.INSTANCE);
    }
}
