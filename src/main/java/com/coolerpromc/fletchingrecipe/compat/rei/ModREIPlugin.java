/*
package com.coolerpromc.fletchingrecipe.compat.rei;

import com.coolerpromc.fletchingrecipe.compat.rei.fletching.FletchingCategory;
import com.coolerpromc.fletchingrecipe.compat.rei.fletching.FletchingDisplay;
import com.coolerpromc.fletchingrecipe.screen.FletchingTableScreen;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.world.level.block.Blocks;

@REIPluginClient
public class ModREIPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new FletchingCategory(), config -> config.addWorkstations(EntryStacks.of(Blocks.FLETCHING_TABLE)));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerClickArea(screen -> new Rectangle(((screen.width - 176) / 2) + 89, ((screen.height - 166) / 2) + 34, 22, 16), FletchingTableScreen.class, FletchingDisplay.CATEGORY_IDENTIFIER);
    }
}*/
