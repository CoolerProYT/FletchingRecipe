## 📘 JSON Recipe Format
Each `fletchingrecipe:fletching` recipe uses the following properties:

| Property | Type                    | Description |
|-----------|-------------------------|-------------|
| `type` | string                  | Must be `"fletchingrecipe:fletching"` |
| `top` | `SizedIngredient`         | The item in the **top slot** |
| `middle` | `SizedIngredient`              | The item in the **middle slot** |
| `bottom` | `SizedIngredient` *(optional)* | The item in the **bottom slot**, may be omitted |
| `output` | `ItemStack`               | The resulting crafted item |


### 1.20.1
`SizedIngredient` lets you specify **both** the ingredient and the **stack size** required in the recipe.

| Property | Type                                          | Description                                                 |
|-----------|-----------------------------------------------|-------------------------------------------------------------|
| `count` | integer                                       | How many of this ingredient are required                    |
| `ingredient` | `Ingredient/NbtIngredient` | The Ingredient to be used (Reference to Ingredient section) |

---
`Ingredient` lets you specify the ingredient.

| Property       | Type | Description                                                                           |
|----------------|------|---------------------------------------------------------------------------------------|
| `item` / `tag` | string | The item ID or tag used as the ingredient                                             |

---
`NbtIngredient` lets you specify the ingredient with NBT data.

#### Forge
| Property | Type    | Description                                                                                                                                          |
|----------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| `type`   | string  | Must be `forge:nbt`                                                                                                                                  |
| `count`  | integer | Same value as `count` in `SizedIngredient` will get rid of this extra field if i implemented my own NBTIngredient                                    |
| `item`   | string  | The item ID used as the ingredient, forge doesn't support tag, I might implement custom NBTIngredient in future                                      |
| `nbt`    | string  | A string of nbt data, e.g. `"{Potion:\"minecraft:awkward\"}"` . I know this is weird, will change to proper json if I implement custom NBTIngredient |

#### Fabric
| Property      | Type         | Description                                                     |
|---------------|--------------|-----------------------------------------------------------------|
| `fabric:type` | string       | Must be `fabric:nbt`                                            |
| `base`        | `Ingredient` | Object of `Ingredient`, can refer to `Ingredient` section above |
| `nbt`         | `Map`        | A key-value pair of JSON Object for NBT name and value          |
| `strict`      | boolean      | Should be always `true`                                          |

---
`ItemStack` lets you to define

### Example
```json5
{
  "type": "fletchingrecipe:fletching", // type
  "top": { // top - fabric NBTIngredient example
    "count": 1,
    "ingredient": {
      "fabric:type": "fabric:nbt",
      "base": {
        "item": "minecraft:lingering_potion"
      },
      "nbt": {
        "Potion": "minecraft:awkward"
      },
      "strict": true
    }
  },
  "middle": { // middle - normal Ingredient example
    "count": 8,
    "ingredient": {
      "item": "minecraft:arrow"
    }
  },
  "bottom": { // bottom - forge NBTIngredient example
    "count": 1,
    "ingredient": {
      "type": "forge:nbt",
      "count": 1,
      "item": "minecraft:lingering_potion",
      "nbt": "{Potion:\"minecraft:awkward\"}"
    }
  },
  "output": {
    "count": 8,
    "item": "minecraft:tipped_arrow",
    "nbt": {
      "Potion": "minecraft:awkward"
    }
  }
}
```