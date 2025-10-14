# 📘 JSON Recipe Format
Each `fletchingrecipe:fletching` recipe uses the following properties:

| Property | Type                    | Description |
|-----------|-------------------------|-------------|
| `type` | string                  | Must be `"fletchingrecipe:fletching"` |
| `top` | `SizedIngredient`         | The item in the **top slot** |
| `middle` | `SizedIngredient`              | The item in the **middle slot** |
| `bottom` | `SizedIngredient` *(optional)* | The item in the **bottom slot**, may be omitted |
| `output` | `ItemStack`               | The resulting crafted item |


## 1.20.1
### SizedIngredient

| Property | Type                                          | Description                                                 |
|-----------|-----------------------------------------------|-------------------------------------------------------------|
| `count` | integer                                       | How many of this ingredient are required                    |
| `ingredient` | `Ingredient/NbtIngredient` | The Ingredient to be used (Reference to Ingredient section) |

---
### Ingredient

| Property       | Type | Description                                                                           |
|----------------|------|---------------------------------------------------------------------------------------|
| `item` / `tag` | string | The item ID or tag used as the ingredient                                             |

---
### NbtIngredient

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
### ItemStack

| Property           | Type    | Description                                                                                                                                          |
|--------------------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| `count`            | integer | Amount of the stack                                  |
| `item`             | string  | The item ID for the target item                                    |
| `nbt` *(Optional)* | `Map`        | A key-value pair of JSON Object for NBT name and value          |

---
### Example (1.20.1)
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
  "bottom": { // bottom - forge NBTIngredient example, bottom is optional field
    "count": 1,
    "ingredient": {
      "type": "forge:nbt",
      "count": 1,
      "item": "minecraft:lingering_potion",
      "nbt": "{Potion:\"minecraft:awkward\"}"
    }
  },
  "output": { // output - ItemStack to output
    "count": 8,
    "item": "minecraft:tipped_arrow",
    "nbt": { // nbt is optional
      "Potion": "minecraft:awkward"
    }
  }
}
```

## 1.21.1
### SizedIngredient

| Property     | Type                             | Description                                                 |
|--------------|----------------------------------|-------------------------------------------------------------|
| `count`      | integer                          | How many of this ingredient are required                    |
| `ingredient` | `Ingredient/ComponentIngredient` | The Ingredient to be used (Reference to Ingredient section) |

---
### Ingredient

| Property       | Type   | Description                               |
|----------------|--------|-------------------------------------------|
| `item` / `tag` | string | The item ID or tag used as the ingredient |

---
### ComponentIngredient

#### NeoForge
| Property     | Type    | Description                                                                                                                       |
|--------------|---------|-----------------------------------------------------------------------------------------------------------------------------------|
| `type`       | string  | Must be `neoforge:components`                                                                                                     |
| `components` | `Map`   | A key-value pair of JSON Object for Component Id and value, the value can me a string or a JSON Object depending on the component |
| `items`      | string  | The items of the ingredient (It is HolderSet of Item)                                                                             |
| `strict`     | boolean | Should be always `true`                                                                                                           |

#### Fabric
| Property      | Type         | Description                                                                                                                       |
|---------------|--------------|-----------------------------------------------------------------------------------------------------------------------------------|
| `fabric:type` | string       | Must be `fabric:components`                                                                                                       |
| `base`        | `Ingredient` | Object of `Ingredient`, can refer to `Ingredient` section above                                                                   |
| `components`  | `Map`        | A key-value pair of JSON Object for Component Id and value, the value can me a string or a JSON Object depending on the component |

---
### ItemStack

| Property                  | Type    | Description                                                                                                                       |
|---------------------------|---------|-----------------------------------------------------------------------------------------------------------------------------------|
| `count`                   | integer | Amount of the stack                                                                                                               |
| `id`                      | string  | The item ID for the target item                                                                                                   |
| `components` *(Optional)* | `Map`   | A key-value pair of JSON Object for Component Id and value, the value can me a string or a JSON Object depending on the component |

---
### Example (1.21.1)
```json5
{
  "type": "fletchingrecipe:fletching", // type
  "top": { // top - fabric ComponentIngredient example
    "count": 1,
    "ingredient": {
      "fabric:type": "fabric:components",
      "base": {
        "item": "minecraft:lingering_potion"
      },
      "components": {
        "minecraft:potion_contents": {
          "potion": "minecraft:awkward"
        }
      }
    }
  },
  "middle": { // middle - normal Ingredient example
    "count": 8,
    "ingredient": {
      "item": "minecraft:arrow"
    }
  },
  "bottom": { // bottom - neoforge ComponentIngredient example, bottom is optional field
    "count": 1,
    "ingredient": {
      "type": "neoforge:components",
      "components": {
        "minecraft:potion_contents": {
          "potion": "minecraft:awkward"
        }
      },
      "items": "minecraft:lingering_potion",
      "strict": true
    }
  },
  "output": { // output - ItemStack to output
    "components": {
      "minecraft:potion_contents": {
        "potion": "minecraft:awkward"
      }
    },
    "count": 8,
    "id": "minecraft:tipped_arrow"
  }
}
```

## 1.21.10
### SizedIngredient

| Property     | Type                             | Description                                                 |
|--------------|----------------------------------|-------------------------------------------------------------|
| `count`      | integer                          | How many of this ingredient are required                    |
| `ingredient` | `Ingredient/ComponentIngredient` | The Ingredient to be used (Reference to Ingredient section) |

---
### Ingredient

| Property     | Type   | Description                                       |
|--------------|--------|---------------------------------------------------|
| `ingredient` | string | The item ID or item tag id used as the ingredient |

---
### ComponentIngredient

#### NeoForge
| Property                   | Type    | Description                                                                                                                       |
|----------------------------|---------|-----------------------------------------------------------------------------------------------------------------------------------|
| `neoforge:ingredient_type` | string  | Must be `neoforge:components`                                                                                                     |
| `components`               | `Map`   | A key-value pair of JSON Object for Component Id and value, the value can me a string or a JSON Object depending on the component |
| `items`                    | string  | The items of the ingredient (It is HolderSet of Item)                                                                             |
| `strict`                   | boolean | Should be always `true`                                                                                                           |

#### Fabric
| Property      | Type   | Description                                                                                                                       |
|---------------|--------|-----------------------------------------------------------------------------------------------------------------------------------|
| `fabric:type` | string | Must be `fabric:components`                                                                                                       |
| `base`        | string | The item ID or item tag id used as the ingredient                                                                                 |
| `components`  | `Map`  | A key-value pair of JSON Object for Component Id and value, the value can me a string or a JSON Object depending on the component |

#### Forge
| Property     | Type    | Description                                                                                                                       |
|--------------|---------|-----------------------------------------------------------------------------------------------------------------------------------|
| `type`       | string  | Must be `fletchingrecipe:data_component`                                                                                          |
| `components` | `Map`   | A key-value pair of JSON Object for Component Id and value, the value can me a string or a JSON Object depending on the component |
| `items`      | string  | The items of the ingredient (It is HolderSet of Item)                                                                             |
| `strict`     | boolean | Should be always `true`                                                                                                           |

---
### ItemStack

| Property                  | Type    | Description                                                                                                                       |
|---------------------------|---------|-----------------------------------------------------------------------------------------------------------------------------------|
| `count`                   | integer | Amount of the stack                                                                                                               |
| `id`                      | string  | The item ID for the target item                                                                                                   |
| `components` *(Optional)* | `Map`   | A key-value pair of JSON Object for Component Id and value, the value can me a string or a JSON Object depending on the component |

---
### Example (1.21.10)
```json5
{
  "type": "fletchingrecipe:fletching", // type
  "top": { // top - fabric ComponentIngredient example
    "count": 1,
    "ingredient": {
      "fabric:type": "fabric:components",
      "base": "minecraft:lingering_potion",
      "components": {
        "minecraft:potion_contents": {
          "potion": "minecraft:awkward"
        }
      }
    }
  },
  "middle": { // middle - normal Ingredient example
    "count": 8,
    "ingredient": "minecraft:arrow"
  },
  "bottom": { // bottom - neoforge ComponentIngredient example, similar to forge, bottom is optional field
    "count": 1,
    "ingredient": {
      "neoforge:ingredient_type": "neoforge:components", // Forge - replace with "type": "fletchingrecipe:data_component"
      "components": {
        "minecraft:potion_contents": {
          "potion": "minecraft:awkward"
        }
      },
      "items": "minecraft:lingering_potion",
      "strict": true
    }
  },
  "output": { // output - ItemStack to output
    "components": {
      "minecraft:potion_contents": {
        "potion": "minecraft:awkward"
      }
    },
    "count": 8,
    "id": "minecraft:tipped_arrow"
  }
}
```
