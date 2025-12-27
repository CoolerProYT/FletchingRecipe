package com.coolerpromc.fletchingrecipe.screen;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.compat.arrowplus.ArrowCheck;
import com.coolerpromc.fletchingrecipe.compat.morefletchingtable.MoreFletchingTableCheck;
import com.coolerpromc.fletchingrecipe.config.ExplosiveIngredientConfig;
import com.coolerpromc.fletchingrecipe.config.FletchingRecipeConfig;
import com.coolerpromc.fletchingrecipe.recipe.FletchingRecipeInput;
import com.coolerpromc.fletchingrecipe.recipe.FletchingTableRecipe;
import com.coolerpromc.fletchingrecipe.screen.slot.FletchingResultSlot;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LingeringPotionItem;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

public class FletchingTableMenu extends ScreenHandler {
    private final PlayerEntity player;
    private final CraftingInventory inputSlots = new CraftingInventory(this, 1, 3);
    private final Inventory explosiveSlot = new SimpleInventory(1);
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

        this.addSlot(new Slot(explosiveSlot, 0, 17, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return ExplosiveIngredientConfig.explosiveIngredients.containsKey(stack.getRegistryEntry());
            }

            @Override
            public void markDirty() {
                super.markDirty();
                onContentChanged(explosiveSlot);
            }
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    public static final int RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = 1;
    private static final int CRAFT_SLOT_END = 4;
    private static final int GUNPOWDER_SLOT = 4;
    private static final int INV_SLOT_START = 5;
    private static final int INV_SLOT_END = 32;
    private static final int HOTBAR_SLOT_START = INV_SLOT_END;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int HOTBAR_SLOT_END = HOTBAR_SLOT_START + HOTBAR_SLOT_COUNT;

    public boolean hasExplosive() {
        return !explosiveSlot.getStack(0).isEmpty() && FletchingRecipeConfig.allowExplosiveCrafting();
    }

    public void consumeExplosive() {
        ItemStack gunpowder = explosiveSlot.getStack(0);
        if (!gunpowder.isEmpty()) {
            gunpowder.decrement(1);
            explosiveSlot.markDirty();
        }
    }

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
                if (ExplosiveIngredientConfig.explosiveIngredients.containsKey(original.getRegistryEntry())) {
                    if (!this.insertItem(original, GUNPOWDER_SLOT, GUNPOWDER_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (!this.insertItem(original, CRAFT_SLOT_START, CRAFT_SLOT_END, false)) {
                    if (index >= INV_SLOT_START && index < INV_SLOT_END) {
                        if (!this.insertItem(original, HOTBAR_SLOT_START, HOTBAR_SLOT_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.insertItem(original, INV_SLOT_START, INV_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            else if (index == GUNPOWDER_SLOT) {
                if (!this.insertItem(original, INV_SLOT_START, HOTBAR_SLOT_END, false)) {
                    return ItemStack.EMPTY;
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
        return this.access.get((world, pos) -> ((FabricLoader.getInstance().isModLoaded("lolmft") && MoreFletchingTableCheck.checkBlock(world.getBlockState(pos).getBlock())) || world.getBlockState(pos).getBlock() == Blocks.FLETCHING_TABLE) && player.canInteractWithBlockAt(pos, 4.0F), true);
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
        Optional<RecipeEntry<FletchingTableRecipe>> optional = level.getServer().getRecipeManager().getFirstMatch(FletchingRecipe.FLETCHING_RECIPE_TYPE, input, level);
        if (optional.isPresent()) {
            RecipeEntry<FletchingTableRecipe> recipeholder = optional.get();
            FletchingTableRecipe recipe = recipeholder.value();
            if (resultSlots.shouldCraftRecipe(serverplayer, recipeholder)) {
                ItemStack itemstack1 = recipe.craft(input, level.getRegistryManager());
                if (itemstack1.isItemEnabled(level.getEnabledFeatures())) {
                    itemstack = itemstack1;
                }
            }
            fletchingResultSlot.setRecipeHolder(recipeholder);
        }
        else if(isValidTippedRecipe()){
            itemstack = createTippedArrows();
            fletchingResultSlot.setRecipeHolder(null);
        }
        else if (hasExplosive()) {
            ItemStack arrowStack = findSingleArrow(craftSlots);
            if (!arrowStack.isEmpty()) {
                itemstack = createExplosiveArrow(arrowStack);
                fletchingResultSlot.setRecipeHolder(null);
            }
        }

        resultSlots.setStack(0, itemstack);
        this.setReceivedStack(0, itemstack);
        serverplayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(this.syncId, this.nextRevision(), 0, itemstack));
    }

    private boolean isValidArrowForTipping(ItemStack stack) {
        if (stack.contains(DataComponentTypes.POTION_CONTENTS)) {
            return false;
        }
        if (stack.isOf(Items.ARROW)) {
            return true;
        }
        if (FabricLoader.getInstance().isModLoaded("arrowplus")) {
            return ArrowCheck.check(stack);
        }

        return false;
    }

    public boolean isValidTippedRecipe(){
        ItemStack lingeringPotionItem = ItemStack.EMPTY;
        ItemStack arrowItem = ItemStack.EMPTY;
        int filledCount = 0;

        for (int i = 0; i < this.inputSlots.size(); i++){
            ItemStack stack = inputSlots.getStack(i);
            if (!stack.isEmpty()) filledCount++;

            if (stack.getItem() instanceof LingeringPotionItem){
                lingeringPotionItem = stack;
            }
            else if (isValidArrowForTipping(stack)){
                arrowItem = stack;
            }
        }

        if (filledCount > 2){
            return false;
        }

        if (!lingeringPotionItem.isEmpty() && !arrowItem.isEmpty()){
            return arrowItem.getCount() >= FletchingRecipeConfig.tippedArrowCraftingAmount();
        }

        return false;
    }

    private ItemStack createTippedArrows() {
        ItemStack lingeringPotion = ItemStack.EMPTY;
        ItemStack arrows = ItemStack.EMPTY;

        for (int i = 0; i < this.inputSlots.size(); i++){
            ItemStack stack = inputSlots.getStack(i);
            if (stack.getItem() instanceof LingeringPotionItem){
                lingeringPotion = stack;
            }
            else if (isValidArrowForTipping(stack)){
                arrows = stack;
            }
        }

        if (lingeringPotion.isEmpty() || arrows.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = arrows.isOf(Items.ARROW) ? new ItemStack(Items.TIPPED_ARROW, FletchingRecipeConfig.tippedArrowCraftingAmount()) : arrows.copyWithCount(FletchingRecipeConfig.tippedArrowCraftingAmount());
        PotionContentsComponent potionContents = lingeringPotion.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
        result.set(DataComponentTypes.POTION_CONTENTS, potionContents);

        return result;
    }

    public void consumeTippedArrowIngredients() {
        int arrowsNeeded = FletchingRecipeConfig.tippedArrowCraftingAmount();

        for (int i = 0; i < this.inputSlots.size(); i++){
            ItemStack stack = inputSlots.getStack(i);
            if (stack.getItem() instanceof LingeringPotionItem){
                stack.decrement(1);
                if (stack.isEmpty()) {
                    inputSlots.setStack(i, ItemStack.EMPTY);
                }
            }
            else if (isValidArrowForTipping(stack)){
                stack.decrement(arrowsNeeded);
                if (stack.isEmpty()) {
                    inputSlots.setStack(i, ItemStack.EMPTY);
                }
            }
        }
        inputSlots.markDirty();
    }

    private ItemStack findSingleArrow(RecipeInputInventory craftSlots) {
        ItemStack foundArrow = ItemStack.EMPTY;
        int itemCount = 0;

        for (int i = 0; i < craftSlots.size(); i++) {
            ItemStack stack = craftSlots.getStack(i);
            if (!stack.isEmpty()) {
                itemCount++;
                if (stack.getItem() instanceof ArrowItem && !stack.contains(FletchingRecipe.EXPLOSIVE)) {
                    foundArrow = stack;
                }
            }
        }

        return itemCount == 1 && foundArrow.getCount() >= FletchingRecipeConfig.explosiveArrowCraftingAmount() ? foundArrow : ItemStack.EMPTY;
    }

    private ItemStack createExplosiveArrow(ItemStack arrowStack) {
        ItemStack result = arrowStack.copy();
        result.setCount(FletchingRecipeConfig.explosiveArrowCraftingAmount());
        if (ExplosiveIngredientConfig.explosiveIngredients.containsKey(explosiveSlot.getStack(0).getRegistryEntry())){
            result.set(FletchingRecipe.EXPLOSIVE, explosiveSlot.getStack(0).getRegistryEntry());
        }
        return result;
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
        this.access.run((p_39371_, p_39372_) -> {
            this.dropInventory(player, this.inputSlots);
            this.dropInventory(player, this.explosiveSlot);
        });
    }
}
