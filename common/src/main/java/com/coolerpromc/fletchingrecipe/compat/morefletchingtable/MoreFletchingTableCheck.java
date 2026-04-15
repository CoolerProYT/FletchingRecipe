package com.coolerpromc.fletchingrecipe.compat.morefletchingtable;

import net.minecraft.world.level.block.Block;

public class MoreFletchingTableCheck {
    public static boolean checkBlock(Block block){
        try{
            Class<?> target = Class.forName("de.pnku.mft.block.MoreFletchingTablesBlock");
            return target.isInstance(block);
        }
        catch (ClassNotFoundException e){
            return false;
        }
    }
}
