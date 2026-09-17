# Arrow recipes

These are the fletching recipes the mod ships with. Datapacks can [add more or replace these](../datapacks/recipes).

## Arrow

<RecipeCard id="fletching/arrow" />

A crafting table makes 4 arrows from the same ingredients.

## Spectral arrow

<RecipeCard id="fletching/spectral_arrow" />

The bottom slot must be empty. A crafting table makes 2 spectral arrows from 4 glowstone dust and 1 arrow.

## How recipe slots work

- Each ingredient goes in its own slot. Flint in the middle slot does not count as the top ingredient.
- A slot can hold more than the recipe needs. Each craft takes only the listed amount, so a stack of 64 flint covers 64 crafts of arrows.
- If a recipe has no bottom ingredient, the bottom slot must be empty.

## Other crafts

Two crafts are built into the table instead of being recipes. Their amounts come from the [common config](../config/common).

- [Tipped arrows](./tipped-arrows): a lingering potion and arrows
- [Explosive arrows](./explosive-arrows): arrows and an explosive ingredient

## Unlocking

Each recipe unlocks when you pick up one of its ingredients, like vanilla recipes. This only matters when the limited crafting game rule is on: then the table only crafts recipes you have unlocked. Tipped and explosive arrows are never locked.

The fletching table has no recipe book.
