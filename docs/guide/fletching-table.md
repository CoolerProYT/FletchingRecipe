# The fletching table

Right-clicking a fletching table opens its crafting screen. Tables added by [More Fletching Tables](./compat#more-fletching-tables) open the same screen.

<FletchingGui />

| Slot | Position | Holds |
| --- | --- | --- |
| Explosive | Left | An [explosive ingredient](../config/explosive-ingredients): gunpowder, TNT or a fire charge by default. Other items do not fit. |
| Top, middle, bottom | Centre column | Recipe ingredients, arrows and lingering potions |
| Result | Right | What the current items make |

When explosive crafting is [turned off](../config/common), the explosive slot is covered and cannot be used:

<FletchingGui :explosive-slot="false" />

## What gets crafted

Every time the slots change, the table checks these crafts in order and shows the first one that matches:

1. **[Fletching recipes](./recipes)**, including ones from datapacks. Each ingredient must be in its own slot, in at least the amount the recipe asks for.
2. **[Tipped arrows](./tipped-arrows)**: a lingering potion plus enough plain arrows, with nothing else in the column.
3. **[Explosive arrows](./explosive-arrows)**: one stack of arrows in the column and an item in the explosive slot.

Taking the result uses up the ingredients for one craft. Anything left over stays in its slot, so you can take the result again.

## Crafting in bulk

Shift-click the result to craft repeatedly and move the arrows into your inventory. It stops when the ingredients run out or your inventory is full. If only part of a craft fits, the rest drops at your feet.

## Closing the screen

Items left in the table return to your inventory when you close the screen, or drop on the ground if your inventory is full. The table does not store anything.
