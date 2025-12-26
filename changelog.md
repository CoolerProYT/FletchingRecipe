- Moved arrow in Fletching Table GUI to middle
- Fixed JEI not showing TagKey Ingredient for Fletching Recipe
- Added a new slot to Fletching Table GUI for Explosive Ingredient
- All arrows that are using `AbstractArrow` class (Basically all vanilla arrow, and some modded arrow) can become explosive arrow by crafting in Fletching Table
- Added new config `fletchingrecipe-explosive-ingredient.json` to define explosive ingredient and explosive radius
- Added common config to control `Fletching Recipe` behaviour
- Removed json recipe for `Tipped Arrow`
- Spectral Arrow and Arrow output amount is doubled from crafting table output amount
- Tipped arrow recipe is now hardcoded to have better support for modded potion (If json recipe is defined, it will use json recipe first, it has the highest priority)
- Tipped arrow crafting amount with 1 potion can be set in common config, default value is `16`

This version onwards only support `Arrow+` above `v3.1.0`, which is still W.I.P when `Fletching Recipe` `v1.2.0` release 

The reason behind `double output amount` is because Fletching Table meant to be powerful than Crafting Table for Arrow related recipe, same as stonecutter
