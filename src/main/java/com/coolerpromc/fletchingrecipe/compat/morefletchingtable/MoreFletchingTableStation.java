package com.coolerpromc.fletchingrecipe.compat.morefletchingtable;

import de.pnku.mft.init.MftBlockInit;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemConvertible;

import java.util.ArrayList;
import java.util.List;

public class MoreFletchingTableStation {
    public static ItemConvertible[] get(){
        List<ItemConvertible> items = new ArrayList<>(MftBlockInit.more_fletching_tables);
        items.add(Blocks.FLETCHING_TABLE);
        return items.toArray(new ItemConvertible[0]);
    }
}
