package com.coolerpromc.fletchingrecipe.compat.rei.explosive;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.block.Blocks;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ExplosiveCategory implements DisplayCategory<ExplosiveDisplay> {
    @Override
    public CategoryIdentifier<? extends ExplosiveDisplay> getCategoryIdentifier() {
        return ExplosiveDisplay.CATEGORY_IDENTIFIER;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("block.minecraft.fletching_table");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Blocks.FLETCHING_TABLE);
    }

    @Override
    public List<Widget> setupDisplay(ExplosiveDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createCategoryBase(new Rectangle(bounds.x, bounds.y, 140, 66)));
        widgets.add(Widgets.createArrow(new Point(bounds.x + 68, bounds.y + 25)));

        widgets.add(Widgets.createSlotBackground(new Point(bounds.x + 10, bounds.y + 25)));

        for (int i = 0;i < 3;i++){
            widgets.add(Widgets.createSlotBackground(new Point(bounds.x + 36, bounds.y + 7 + i * 18)));
        }

        widgets.add(Widgets.createResultSlotBackground(new Point(bounds.x + 109, bounds.y + 25)));

        widgets.add(Widgets.createSlot(new Point(bounds.x + 10, bounds.y + 25)).entries(display.getInputEntries().getFirst()).markInput());
        widgets.add(Widgets.createSlot(new Point(bounds.x + 36, bounds.y + 7)).entries(display.getInputEntries().get(1)).markInput());

        widgets.add(Widgets.createSlot(new Point(bounds.x + 109, bounds.y + 25)).disableBackground().entries(display.getOutputEntries().getFirst()).markOutput());

        return widgets;
    }
}