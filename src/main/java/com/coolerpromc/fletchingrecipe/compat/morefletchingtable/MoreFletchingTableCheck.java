package com.coolerpromc.fletchingrecipe.compat.morefletchingtable;

import de.pnku.mft.block.MoreFletchingTablesBlock;
import net.minecraft.block.Block;

public class MoreFletchingTableCheck {
    public static boolean checkBlock(Block block){
        return block instanceof MoreFletchingTablesBlock;
    }
}
