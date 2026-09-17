# Common config

File: `config/fletchingrecipe-common.toml`

## Options

<ConfigTable>
  <template #allowExplosiveCrafting>
    Whether the table has an <a href="../guide/explosive-arrows">explosive</a> slot. When <code>false</code>, the slot is covered and explosive arrows cannot be crafted.
  </template>
  <template #tippedArrowCraftingAmount>
    Arrows used and made by one <a href="../guide/tipped-arrows">tipped arrow</a> craft, for 1 lingering potion.
  </template>
  <template #explosiveArrowCraftingAmount>
    Arrows used and made by one <a href="../guide/explosive-arrows">explosive arrow</a> craft, for 1 explosive ingredient.
  </template>
</ConfigTable>

Values outside the range are clamped: `0` counts as 1 and `100` as 64.

## Generator

<CommonConfigBuilder />

## Examples

The layout differs by loader. NeoForge puts the options under a `["Fletching Recipe"]` table and adds default and range comments.

::: code-group

```toml [NeoForge]
["Fletching Recipe"]
	#Allow fletching table to craft explosive arrow
	allowExplosiveCrafting = true
	#Amount of tipped arrow it can craft with 1 lingering potion, if json recipe for particular tipped arrow are defined, it will not be controlled by this config
	# Default: 16
	# Range: 1 ~ 64
	tippedArrowCraftingAmount = 16
	#Amount of explosive arrow it can craft with 1 explosive ingredient
	# Default: 4
	# Range: 1 ~ 64
	explosiveArrowCraftingAmount = 4
```

```toml [Fabric]
#Allow fletching table to craft explosive arrow
allowExplosiveCrafting = true
#Amount of tipped arrow it can craft with 1 lingering potion, if json recipe for particular tipped arrow are defined, it will not be controlled by this config
tippedArrowCraftingAmount = 16
#Amount of explosive arrow it can craft with 1 explosive ingredient
explosiveArrowCraftingAmount = 4
```

```toml [No explosives, full stacks]
# NeoForge layout. On Fabric, leave out the first line and the indents.
["Fletching Recipe"]
	allowExplosiveCrafting = false
	tippedArrowCraftingAmount = 64
	explosiveArrowCraftingAmount = 4
```

:::

## Reloading and multiplayer

The game notices when the file is saved and applies the new values. Players get the server's amounts, for JEI, the next time they join or when `/reload` runs.

- **Open screens** keep the explosive slot they opened with. Close and reopen the table after changing `allowExplosiveCrafting`.
- **On a server**, give players the same file. Each player's table screen uses their own `allowExplosiveCrafting` to decide whether to show the explosive slot, so a mismatch with the server breaks the screen.
- **On Fabric in single player**, the file stops being watched after you leave a world. Restart the game to apply later changes.
