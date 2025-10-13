package com.coolerpromc.fletchingrecipe.screen;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.recipe.FletchingRecipeInput;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.screen.slot.FletchingResultSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;

public class FletchingTableMenu extends AbstractContainerMenu {
    private final Player player;
    private final TransientCraftingContainer inputSlots = new TransientCraftingContainer(this, 1, 3);
    private final ResultContainer resultSlot = new ResultContainer();
    private final ContainerLevelAccess access;
    private final FletchingResultSlot fletchingResultSlot;

    public FletchingTableMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public FletchingTableMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(FletchingRecipe.FLETCHING_TABLE_MENU.get(), containerId);
        this.player = playerInventory.player;
        this.access = access;

        this.fletchingResultSlot = new FletchingResultSlot(player, inputSlots, resultSlot, 0, 124, 35, this);
        this.addSlot(fletchingResultSlot);

        for (int i = 0; i < inputSlots.getContainerSize(); i++){
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
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack original = slot.getItem();
            result = original.copy();

            if (index == RESULT_SLOT) {
                if (!this.moveItemStackTo(original, INV_SLOT_START, HOTBAR_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(original, result);
            }
            else if (index >= INV_SLOT_START && index < HOTBAR_SLOT_END) {
                if (!this.moveItemStackTo(original, CRAFT_SLOT_START, CRAFT_SLOT_END, false)) {
                    if (index >= INV_SLOT_START && index < INV_SLOT_END) {
                        if (!this.moveItemStackTo(original, HOTBAR_SLOT_START, HOTBAR_SLOT_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(original, INV_SLOT_START, INV_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            else if (index >= CRAFT_SLOT_START && index < CRAFT_SLOT_END) {
                if (!this.moveItemStackTo(original, INV_SLOT_START, HOTBAR_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(original, INV_SLOT_START, HOTBAR_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (original.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (original.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, original);
            if (index == RESULT_SLOT) {
                player.drop(original, false);
            }
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, Blocks.FLETCHING_TABLE);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    protected void slotChangedCraftingGrid(ServerLevel level, Player player, CraftingContainer craftSlots, ResultContainer resultSlots) {
        FletchingRecipeInput input = new FletchingRecipeInput(craftSlots.getItem(0), craftSlots.getItem(1), craftSlots.getItem(2));
        ServerPlayer serverplayer = (ServerPlayer)player;
        ItemStack itemstack = ItemStack.EMPTY;
        Optional<RecipeHolder<FletchingTableRecipe>> optional = level.getServer().getRecipeManager().getRecipeFor(FletchingRecipe.FLETCHING_RECIPE_TYPE.get(), input, level);
        if (optional.isPresent()) {
            RecipeHolder<FletchingTableRecipe> recipeholder = optional.get();
            FletchingTableRecipe recipe = recipeholder.value();
            if (resultSlots.setRecipeUsed(serverplayer, recipeholder)) {
                ItemStack itemstack1 = recipe.assemble(input, level.registryAccess());
                if (itemstack1.isItemEnabled(level.enabledFeatures())) {
                    itemstack = itemstack1;
                }
            }
            fletchingResultSlot.setRecipeHolder(recipeholder);
        }

        resultSlots.setItem(0, itemstack);
        this.setRemoteSlot(0, itemstack);
        serverplayer.connection.send(new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), 0, itemstack));
    }

    @Override
    public void slotsChanged(Container container) {
        this.access.execute((level, pos) -> {
            if (level instanceof ServerLevel serverlevel) {
                slotChangedCraftingGrid(serverlevel, this.player, this.inputSlots, this.resultSlot);
            }
        });
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((p_39371_, p_39372_) -> this.clearContainer(player, this.inputSlots));
    }
}
