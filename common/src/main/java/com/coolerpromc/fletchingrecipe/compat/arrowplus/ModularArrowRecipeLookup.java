package com.coolerpromc.fletchingrecipe.compat.arrowplus;

import com.coolerpromc.arrowplus.config.ArrowPlusConfig;
import com.coolerpromc.arrowplus.datacomponent.ModDataComponents;
import com.coolerpromc.arrowplus.item.ModItems;
import com.coolerpromc.fletchingrecipe.CommonClass;
import com.coolerpromc.fletchingrecipe.compat.jei.category.ExplosiveCategory;
import com.coolerpromc.fletchingrecipe.compat.jei.category.FletchingCategory;
import com.coolerpromc.fletchingrecipe.compat.jei.recipe.JeiExplosiveRecipe;
import com.coolerpromc.fletchingrecipe.compat.jei.recipe.JeiFletchingRecipe;
import com.coolerpromc.fletchingrecipe.config.ExplosiveIngredientConfig;
import com.coolerpromc.fletchingrecipe.network.packet.ClientBoundConfigSyncPacket;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import mezz.jei.api.registration.IAdvancedRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ModularArrowRecipeLookup<T> implements ISimpleRecipeManagerPlugin<T> {
    private final Predicate<ItemStack> inputFilter;
    private final Predicate<ItemStack> outputFilter;
    private final Function<ItemStack, List<T>> inputs;
    private final Function<ItemStack, List<T>> outputs;

    private ModularArrowRecipeLookup(Predicate<ItemStack> inputFilter, Predicate<ItemStack> outputFilter,
                                    Function<ItemStack, List<T>> inputs, Function<ItemStack, List<T>> outputs) {
        this.inputFilter = inputFilter;
        this.outputFilter = outputFilter;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public static void register(IAdvancedRegistration registration) {
        registration.addSimpleRecipeManagerPlugin(FletchingCategory.FLETCHING_TYPE,
                new ModularArrowRecipeLookup<>(s -> !s.has(DataComponents.POTION_CONTENTS),
                        s -> s.has(DataComponents.POTION_CONTENTS),
                        ModularArrowRecipeLookup::tippingInputs,
                        s -> List.of(tipped(s, s.get(DataComponents.POTION_CONTENTS)))));
        if (ClientBoundConfigSyncPacket.INSTANCE.allowExplosiveCrafting()) {
            registration.addSimpleRecipeManagerPlugin(ExplosiveCategory.EXPLOSIVE_TYPE,
                    new ModularArrowRecipeLookup<>(s -> !s.has(CommonClass.EXPLOSIVE.get()),
                            s -> s.has(CommonClass.EXPLOSIVE.get()),
                            ModularArrowRecipeLookup::explosiveInputs,
                            ModularArrowRecipeLookup::explosiveOutput));
        }
    }

    private static boolean handles(ItemStack stack) {
        if (Minecraft.getInstance().level == null || !stack.is(ModItems.ARROW_PLUS.get())) return false;
        var head = stack.get(ModDataComponents.ARROW_DATA.get());
        return head != null && head.isBound() && head.unwrapKey().isPresent()
                && !ArrowPlusConfig.getRemoval().contains(head.unwrapKey().get().identifier().getPath());
    }

    @Override
    public boolean isHandledInput(ITypedIngredient<?> input) {
        return input.getItemStack().filter(ModularArrowRecipeLookup::handles).filter(inputFilter).isPresent();
    }

    @Override
    public boolean isHandledOutput(ITypedIngredient<?> output) {
        return output.getItemStack().filter(ModularArrowRecipeLookup::handles).filter(outputFilter).isPresent();
    }

    @Override
    public List<T> getRecipesForInput(ITypedIngredient<?> input) {
        return isHandledInput(input) ? inputs.apply(input.getItemStack().orElseThrow()) : List.of();
    }

    @Override
    public List<T> getRecipesForOutput(ITypedIngredient<?> output) {
        return isHandledOutput(output) ? outputs.apply(output.getItemStack().orElseThrow()) : List.of();
    }

    @Override
    public List<T> getAllRecipes() {
        return List.of();
    }

    private static List<JeiFletchingRecipe> tippingInputs(ItemStack arrow) {
        return Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.POTION).listElements()
                .filter(p -> !p.value().getEffects().isEmpty())
                .map(p -> tipped(arrow, new PotionContents(p))).toList();
    }

    private static JeiFletchingRecipe tipped(ItemStack arrow, PotionContents potion) {
        ItemStack input = arrow.copyWithCount(ClientBoundConfigSyncPacket.INSTANCE.tippedArrowCraftingAmount());
        input.remove(DataComponents.POTION_CONTENTS);
        ItemStack output = input.copy();
        output.set(DataComponents.POTION_CONTENTS, potion);
        ItemStack lingering = new ItemStack(Items.LINGERING_POTION);
        lingering.set(DataComponents.POTION_CONTENTS, potion);
        return new JeiFletchingRecipe(List.of(lingering), List.of(input), List.of(), output);
    }

    private static List<JeiExplosiveRecipe> explosiveInputs(ItemStack arrow) {
        List<JeiExplosiveRecipe> recipes = new ArrayList<>();
        ExplosiveIngredientConfig.explosiveIngredients.keySet().forEach(ingredient -> {
            ItemStack input = arrow.copyWithCount(ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
            ItemStack output = input.copy();
            output.set(CommonClass.EXPLOSIVE.get(), ingredient);
            recipes.add(new JeiExplosiveRecipe(new ItemStack(ingredient), input, output));
        });
        return recipes;
    }

    private static List<JeiExplosiveRecipe> explosiveOutput(ItemStack arrow) {
        var ingredient = arrow.get(CommonClass.EXPLOSIVE.get());
        if (!ExplosiveIngredientConfig.explosiveIngredients.containsKey(ingredient)) return List.of();
        ItemStack output = arrow.copyWithCount(ClientBoundConfigSyncPacket.INSTANCE.explosiveArrowCraftingAmount());
        ItemStack input = output.copy();
        input.remove(CommonClass.EXPLOSIVE.get());
        return List.of(new JeiExplosiveRecipe(new ItemStack(ingredient), input, output));
    }
}
