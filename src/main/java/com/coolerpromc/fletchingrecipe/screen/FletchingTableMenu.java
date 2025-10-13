package com.coolerpromc.fletchingrecipe.screen;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.recipe.FletchingRecipeInput;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.screen.slot.FletchingResultSlot;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

public class FletchingTableMenu extends ScreenHandler {
    private final PlayerEntity player;
    private final CraftingInventory inputSlots = new CraftingInventory(this, 1, 3);
    private final CraftingResultInventory resultSlot = new CraftingResultInventory();
    private final ScreenHandlerContext access;
    private final FletchingResultSlot fletchingResultSlot;

    public FletchingTableMenu(int containerId, PlayerInventory playerInventory) {
        this(containerId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public FletchingTableMenu(int containerId, PlayerInventory playerInventory, ScreenHandlerContext access) {
        super(FletchingRecipe.FLETCHING_TABLE_MENU, containerId);
        this.player = playerInventory.player;
        this.access = access;

        this.fletchingResultSlot = new FletchingResultSlot(player, inputSlots, resultSlot, 0, 124, 35, this);
        this.addSlot(fletchingResultSlot);

        for (int i = 0; i < inputSlots.size(); i++){
            this.addSlot(new Slot(inputSlots, i, 48, 17 + i * 18));
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    public static final int RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = 1;
    private static final int CRAFT_SLOT_END = 4;
    private static final int INV_SLOT_START = 4;
    private static final int INV_SLOT_END = 31;
    private static final int HOTBAR_SLOT_START = INV_SLOT_END;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int HOTBAR_SLOT_END = HOTBAR_SLOT_START + HOTBAR_SLOT_COUNT;

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack original = slot.getStack();
            result = original.copy();

            if (index == RESULT_SLOT) {
                if (!this.insertItem(original, INV_SLOT_START, HOTBAR_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(original, result);
            }
            else if (index >= INV_SLOT_START && index < HOTBAR_SLOT_END) {
                if (!this.insertItem(original, CRAFT_SLOT_START, CRAFT_SLOT_END, false)) {
                    if (index >= INV_SLOT_START && index < INV_SLOT_END) {
                        if (!this.insertItem(original, HOTBAR_SLOT_START, HOTBAR_SLOT_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.insertItem(original, INV_SLOT_START, INV_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            else if (index >= CRAFT_SLOT_START && index < CRAFT_SLOT_END) {
                if (!this.insertItem(original, INV_SLOT_START, HOTBAR_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.insertItem(original, INV_SLOT_START, HOTBAR_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (original.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (original.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, original);
            if (index == RESULT_SLOT) {
                player.dropItem(original, false);
            }
        }
        return result;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.access, player, Blocks.FLETCHING_TABLE);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    protected void slotChangedCraftingGrid(ServerWorld level, PlayerEntity player, RecipeInputInventory craftSlots, CraftingResultInventory resultSlots) {
        FletchingRecipeInput input = new FletchingRecipeInput(craftSlots.getStack(0), craftSlots.getStack(1), craftSlots.getStack(2));
        ServerPlayerEntity serverplayer = (ServerPlayerEntity)player;
        ItemStack itemstack = ItemStack.EMPTY;
        Optional<FletchingTableRecipe> optional = level.getServer().getRecipeManager().getFirstMatch(FletchingRecipe.FLETCHING_RECIPE_TYPE, input, level);
        if (optional.isPresent()) {
            FletchingTableRecipe recipe = optional.get();
            if (resultSlots.shouldCraftRecipe(level, serverplayer, recipe)) {
                ItemStack itemstack1 = recipe.craft(input, level.getRegistryManager());
                if (itemstack1.isItemEnabled(level.getEnabledFeatures())) {
                    itemstack = itemstack1;
                }
            }
            fletchingResultSlot.setRecipe(recipe);
        }

        resultSlots.setStack(0, itemstack);
        this.setPreviousTrackedSlot(0, itemstack);
        serverplayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(this.syncId, this.nextRevision(), 0, itemstack));
    }

    @Override
    public void onContentChanged(Inventory container) {
        this.access.run((level, pos) -> {
            if (level instanceof ServerWorld serverlevel) {
                slotChangedCraftingGrid(serverlevel, this.player, this.inputSlots, this.resultSlot);
            }
        });
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.access.run((p_39371_, p_39372_) -> this.dropInventory(player, this.inputSlots));
    }
}
