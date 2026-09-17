# Tipped arrows

One lingering potion tips a whole stack of arrows at once: **16** by default, where the crafting table makes 8.

<TippedArrowPicker />

## How to craft

1. Put a lingering potion in any slot of the centre column.
2. Put plain arrows in another slot of the column. You need at least as many as one craft makes.
3. Leave the third slot empty, then take the result.

Each craft uses 1 potion and the same number of arrows as it makes. Any lingering potion works, including a lingering water bottle, which makes Arrows of Splashing.

::: tip
Only plain arrows can be tipped. Tipped arrows cannot be tipped again, and spectral arrows cannot be tipped at all.
:::

With [Arrow+](./compat#arrow) installed, its arrows can be tipped too. They keep their type and gain the potion.

## Changing the amount

The number of arrows per craft is `tippedArrowCraftingAmount` in the [common config](../config/common), from 1 to 64. The table only offers the craft when the arrow slot holds at least that many.

[Datapack recipes](../datapacks/recipes) are checked first. If one uses a lingering potion and its slots match, the table makes that recipe instead of this craft.

## Making them explosive

Tipped arrows can be turned into [explosive arrows](./explosive-arrows) afterwards. They keep their potion, and the mob they hit still gets the effect.
