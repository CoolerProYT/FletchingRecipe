
package com.coolerpromc.fletchingrecipe.compat.rei.fletching;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record FletchingDisplay(List<EntryIngredient> input, List<EntryIngredient> output) implements Display {
    public static final CategoryIdentifier<FletchingDisplay> CATEGORY_IDENTIFIER = CategoryIdentifier.of(FletchingRecipe.MOD_ID, "fletching");
    public static final DisplaySerializer<FletchingDisplay> SERIALIZER = DisplaySerializer.of(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    EntryIngredient.codec().listOf().fieldOf("input").forGetter(FletchingDisplay::input),
                    EntryIngredient.codec().listOf().fieldOf("output").forGetter(FletchingDisplay::output)
            ).apply(instance, FletchingDisplay::new)),
            PacketCodec.tuple(
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
                    FletchingDisplay::input,
                    EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
                    FletchingDisplay::output,
                    FletchingDisplay::new
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
    public Optional<Identifier> getDisplayLocation() {
        return Optional.empty();
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }
}