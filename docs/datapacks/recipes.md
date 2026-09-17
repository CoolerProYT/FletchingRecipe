# Fletching recipes

Fletching recipes are datapack JSON files, so a datapack can add new ones or replace the [built-in ones](../guide/recipes) without code.

## Generator

Fill in the slots and copy the result. Use the buttons to start from a built-in recipe.

<RecipeBuilder />

## Where the file goes

```text
my_pack/
├── pack.mcmeta
└── data/
    └── <namespace>/
        └── recipe/
            └── <name>.json
```

Any namespace and name work. Subfolders are allowed, so `data/mypack/recipe/fletching/bone_arrow.json` has the id `mypack:fletching/bone_arrow`.

Put the datapack in the world's `datapacks` folder and run `/reload`. Players' JEI updates straight away.

## Format

```json
{
  "type": "fletchingrecipe:fletching",
  "top": {
    "ingredient": "minecraft:flint",
    "count": 1
  },
  "middle": {
    "ingredient": "minecraft:stick",
    "count": 1
  },
  "bottom": {
    "ingredient": "minecraft:feather",
    "count": 1
  },
  "output": {
    "id": "minecraft:arrow",
    "count": 8
  }
}
```

| Field | Required | Meaning |
| --- | --- | --- |
| `type` | Yes | Always `fletchingrecipe:fletching` |
| `top` | Yes | Ingredient for the top slot |
| `middle` | Yes | Ingredient for the middle slot |
| `bottom` | No | Ingredient for the bottom slot. Leave it out and the recipe only matches when the bottom slot is empty. |
| `output` | Yes | The item stack the recipe makes |

### Slot ingredients

| Field | Type | Meaning |
| --- | --- | --- |
| `ingredient` | Ingredient | What the slot accepts. See below. |
| `count` | Positive integer, optional (default `1`) | How many items the slot needs, and how many one craft takes |

An ingredient can be written three ways:

::: code-group

```json [One item]
"ingredient": "minecraft:feather"
```

```json [Any of several items]
"ingredient": ["minecraft:feather", "minecraft:phantom_membrane"]
```

```json [Item tag]
"ingredient": "#minecraft:planks"
```

:::

### Output

| Field | Type | Meaning |
| --- | --- | --- |
| `id` | Item id | The item made |
| `count` | Positive integer, optional (default `1`) | How many one craft makes |
| `components` | Object, optional | Data components on the item, such as a potion |

For example, a recipe that makes Arrows of Poison:

```json
"output": {
  "id": "minecraft:tipped_arrow",
  "count": 8,
  "components": {
    "minecraft:potion_contents": {
      "potion": "minecraft:poison"
    }
  }
}
```

## Replacing a built-in recipe

Add a file with the same id to your datapack. The built-in recipes are:

| Recipe | File |
| --- | --- |
| Arrow | `data/fletchingrecipe/recipe/fletching/arrow.json` |
| Spectral arrow | `data/fletchingrecipe/recipe/fletching/spectral_arrow.json` |

## Things to know

- Recipes are checked before [tipped](../guide/tipped-arrows) and [explosive](../guide/explosive-arrows) arrows. A recipe that matches the same items takes their place.
- If two recipes match the same items, the game picks one of them. Give each recipe a distinct combination.
- Plain ingredients ignore components: `"minecraft:lingering_potion"` accepts every lingering potion.
- The output is always the same item. It does not copy anything from the ingredients.
