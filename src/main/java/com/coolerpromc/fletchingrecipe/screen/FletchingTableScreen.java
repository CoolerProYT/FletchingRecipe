package com.coolerpromc.fletchingrecipe.screen;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class FletchingTableScreen extends AbstractContainerScreen<FletchingTableMenu> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(FletchingRecipe.MOD_ID, "textures/gui/fletching_table.png");
    private final CyclingSlotBackground explosionSlotBackground = new CyclingSlotBackground(4);

    public FletchingTableScreen(FletchingTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (this.menu.isExplosiveEnabled()) {
            this.explosionSlotBackground.tick(List.of(Identifier.fromNamespaceAndPath(FletchingRecipe.MOD_ID, "empty_slot_gunpowder"), Identifier.fromNamespaceAndPath(FletchingRecipe.MOD_ID, "empty_slot_tnt")));
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        if (this.menu.isExplosiveEnabled()) {
            this.explosionSlotBackground.extractRenderState(this.menu, graphics, a, leftPos, topPos);
        }
        else{
            graphics.fill(this.leftPos + 16, this.topPos + 34, this.leftPos + 17 + 18, this.topPos + 35 + 18, 0xFFC6C6C6);
        }
    }
}
