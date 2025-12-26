
package com.coolerpromc.fletchingrecipe.compat.rei.explosive;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record ExplosiveDisplay(List<EntryIngredient> input, List<EntryIngredient> output) implements Display {
    public static final CategoryIdentifier<ExplosiveDisplay> CATEGORY_IDENTIFIER = CategoryIdentifier.of(FletchingRecipe.MODID, "explosive");
    public static final DisplaySerializer<ExplosiveDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    EntryIngredient.codec().listOf().fieldOf("input").forGetter(ExplosiveDisplay::input),
                    EntryIngredient.codec().listOf().fieldOf("output").forGetter(ExplosiveDisplay::output)
            ).apply(instance, ExplosiveDisplay::new)),
            StreamCodec.composite(
                    EntryIngredient.streamCodec().apply(ByteBufCodecs.list()),
                    ExplosiveDisplay::input,
                    EntryIngredient.streamCodec().apply(ByteBufCodecs.list()),
                    ExplosiveDisplay::output,
                    ExplosiveDisplay::new
            )
    );

    @Override
    public List<EntryIngredient> getInputEntries() {
        return input;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return output;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CATEGORY_IDENTIFIER;
    }

    @Override
    public Optional<ResourceLocation> getDisplayLocation() {
        return Optional.empty();
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }
}