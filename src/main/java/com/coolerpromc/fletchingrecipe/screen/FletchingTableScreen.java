package com.coolerpromc.fletchingrecipe.screen;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CyclingSlotIcon;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class FletchingTableScreen extends HandledScreen<FletchingTableMenu> {
    public static final Identifier TEXTURE = Identifier.of(FletchingRecipe.MOD_ID, "textures/gui/fletching_table.png");
    private final CyclingSlotIcon explosionSlotBackground = new CyclingSlotIcon(4);

    public FletchingTableScreen(FletchingTableMenu menu, PlayerInventory playerInventory, Text title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        if (this.handler.isExplosiveEnabled()) {
            this.explosionSlotBackground.updateTexture(List.of(Identifier.of(FletchingRecipe.MOD_ID, "item/empty_slot_gunpowder"), Identifier.of(FletchingRecipe.MOD_ID, "item/empty_slot_tnt")));
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
        if (this.handler.isExplosiveEnabled()) {
            this.explosionSlotBackground.render(this.handler, context, deltaTicks, x, y);
        }
        else{
            context.fill(this.x + 16, this.y + 34, this.x + 17 + 18, this.y + 35 + 18, 0xFFC6C6C6);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
