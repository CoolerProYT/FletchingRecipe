# Explosive arrows

An explosive arrow blows up where it lands. Any arrow can be made explosive: plain, spectral or tipped.

<ExplosiveArrowPicker />

## How to craft

1. Put an explosive ingredient in the slot on the left. By default that is gunpowder, TNT or a fire charge.
2. Put a stack of arrows in any slot of the centre column. You need at least **4**, and nothing else can be in the column.
3. Take the result.

Each craft uses 1 explosive ingredient and 4 arrows, and makes 4 explosive arrows of the same kind. Tipped arrows keep their potion.

An explosive arrow shows its ingredient in the corner of its icon and in its tooltip, for example **Explosive Material: TNT**. Arrows that are already explosive cannot be crafted again.

## Explosion strength

Each explosive ingredient has a value in the [explosive ingredient config](../config/explosive-ingredients). How hard the arrow is shot decides how much of it you get:

- A **critical** arrow explodes with the full value. A fully drawn bow shoots critical arrows, and a crossbow always does.
- Any other shot, such as a half-drawn bow or a dispenser, explodes with **half** the value.

<ExplosiveTable />

For comparison, a creeper explodes with power 3 and TNT with power 4.

## When it hits

- The arrow explodes at the point it hits, whether that is a block or a mob.
- The explosion breaks blocks and hurts everything nearby, including you if you are close.
- A mob hit directly still gets the arrow's effects, such as a tipped arrow's potion or a spectral arrow's glowing.
- The arrow is used up. It cannot be picked back up.

::: warning
Explosive arrows break blocks the same way TNT does. Be careful near your own builds.
:::

## Turning it off

Set `allowExplosiveCrafting` to `false` in the [common config](../config/common) to remove the explosive slot. Explosive arrows that already exist still explode.

The amount per craft is `explosiveArrowCraftingAmount` in the same file.
