# Explosive ingredients

File: `config/fletchingrecipe-explosive-ingredient.json`

This file lists the items that fit in the table's explosive slot, and how strong an explosion each one gives an [explosive arrow](../guide/explosive-arrows).

## Format

One JSON object. Each key is an item id and each value is the item's explosion value:

```json
{
  "minecraft:tnt": 2.0,
  "minecraft:fire_charge": 4.0,
  "minecraft:gunpowder": 0.5
}
```

| Part | Type | Meaning |
| --- | --- | --- |
| Key | Item id | An item that can go in the explosive slot. Any item works. |
| Value | Number | Explosion power of a critical arrow made with it. Other shots get half. Decimals are allowed. |

Ids that are not valid, or that name an item that does not exist, are skipped. The game log says which ones with a line starting `[Explosive Ingredient Config]`.

::: warning Items from other mods
The file is first read while mods are still loading, so items from other mods can be reported as not found at startup. Save the file again once the game has loaded and they are picked up.
:::

## Default values

<ExplosiveTable />

For scale, vanilla explosions have these powers:

| Explosion | Power |
| --- | --- |
| Ghast fireball | 1 |
| Creeper | 3 |
| TNT | 4 |
| Bed in the Nether | 5 |
| End crystal | 6 |

## Generator

<ExplosiveConfigBuilder />

## Changing the file

- The file is read when the game starts. While the game is running, saving the file reloads it.
- On a server, give players the same file. The slot on their screen and the JEI pages use their own copy.
- In single player, the file stops being watched after you leave a world. Restart the game to apply later changes.

::: danger Removing an ingredient
Explosive arrows remember their ingredient. Before removing an item from this file, use up or throw away the explosive arrows made with it: when such an arrow hits something, the game looks up a value that is no longer there.
:::
