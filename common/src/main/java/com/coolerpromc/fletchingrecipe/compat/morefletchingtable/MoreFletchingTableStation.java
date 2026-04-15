package com.coolerpromc.fletchingrecipe.compat.morefletchingtable;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class MoreFletchingTableStation {
    public static ItemLike[] get(){
        List<ItemLike> items = new ArrayList<>();
        items.add(Blocks.FLETCHING_TABLE);
        try {
            Class<?> clazz = Class.forName("de.pnku.mft.init.MftBlockInit");
            List<?> moreTables = (List<?>) clazz.getField("more_fletching_tables").get(null);
            for (Object obj : moreTables) {
                items.add((ItemLike) obj);
            }
        } catch (Exception ignored) {}
        return items.toArray(new ItemLike[0]);
    }
}
