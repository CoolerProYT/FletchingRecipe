# Configuration

Fletching Recipe has two config files, both in the game's `config` folder. The game creates them with default values the first time it starts.

| File | Format | Controls | Details |
| --- | --- | --- | --- |
| `fletchingrecipe-common.toml` | TOML | Arrows per tipped and explosive craft, and whether explosive crafting is on | [Common config](./common) |
| `fletchingrecipe-explosive-ingredient.json` | JSON | Which items go in the explosive slot, and how strong each one is | [Explosive ingredients](./explosive-ingredients) |

Both pages have a generator that writes the file for you.

Fletching recipes are not in the config. They come from datapacks: see [Fletching recipes](../datapacks/recipes).

## On a server

Put the same two files on the server and in every player's `config` folder. The server decides what gets crafted, but each player's game builds the table screen and the JEI pages from its own files, so different files can make the screen or JEI disagree with the server.
