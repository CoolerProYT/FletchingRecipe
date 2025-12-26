package com.coolerpromc.fletchingrecipe.screen;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class FletchingTableScreen extends AbstractContainerScreen<FletchingTableMenu> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FletchingRecipe.MODID, "textures/gui/fletching_table.png");
    private final CyclingSlotBackground explosionSlotBackground = new CyclingSlotBackground(4);

    public FletchingTableScreen(FletchingTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.explosionSlotBackground.tick(List.of(ResourceLocation.fromNamespaceAndPath(FletchingRecipe.MODID, "empty_slot_gunpowder"), ResourceLocation.fromNamespaceAndPath(FletchingRecipe.MODID, "empty_slot_tnt")));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        this.explosionSlotBackground.render(this.menu, guiGraphics, v, leftPos, topPos);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
