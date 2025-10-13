package com.coolerpromc.fletchingrecipe.screen;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FletchingTableScreen extends HandledScreen<FletchingTableMenu> {
    public static final Identifier TEXTURE = Identifier.of(FletchingRecipe.MOD_ID, "textures/gui/fletching_table.png");

    public FletchingTableScreen(FletchingTableMenu menu, PlayerInventory playerInventory, Text title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        renderBackground(context);
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
