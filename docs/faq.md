# FAQ

## Nothing shows up in the result slot

Check the items against the craft you want:

- **Recipes** need each ingredient in its own slot: for arrows, flint at the top, the stick in the middle and the feather at the bottom. See [Arrow recipes](./guide/recipes).
- **Tipped arrows** need a lingering potion (not a normal or splash potion), enough plain arrows, and an empty third slot. See [Tipped arrows](./guide/tipped-arrows).
- **Explosive arrows** need enough arrows as the only stack in the column, and an item in the explosive slot. See [Explosive arrows](./guide/explosive-arrows).

If the limited crafting game rule is on, you also need to have unlocked the recipe.

## The explosive slot is grey

Explosive crafting is turned off with `allowExplosiveCrafting` in the [common config](./config/common). On a server, your own config also has to allow it.

## An item will not go in the explosive slot

Only items listed in the [explosive ingredient config](./config/explosive-ingredients) fit. On a server, your own copy of that file needs the item too.

## Can I still use the crafting table?

Yes. The vanilla arrow, spectral arrow and tipped arrow recipes still work in the crafting table, with their usual amounts.

## Do explosive arrows grief?

Yes. They break blocks like TNT, whoever shoots them. Turn off `allowExplosiveCrafting` on servers where that is a problem. Arrows that were already made still explode.

## Can dispensers fire explosive arrows?

Yes. Dispensers do not shoot critical arrows, so the arrows explode at half power.

## Can I add a recipe for a modded arrow?

Yes, with a [datapack recipe](./datapacks/recipes). Modded arrows can also be made explosive if they are normal arrow items.
