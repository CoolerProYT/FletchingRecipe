// Pulls wiki data straight from the mod so the docs never drift from the game:
// datagen recipes, the lang file, config defaults from the config classes, and the GUI textures.
// Run `./gradlew :neoforge:runData` first when the mod's recipes change.
import { copyFileSync, existsSync, mkdirSync, readdirSync, readFileSync, statSync, writeFileSync } from 'node:fs'
import { dirname, join, relative } from 'node:path'
import { fileURLToPath } from 'node:url'

const docs = join(dirname(fileURLToPath(import.meta.url)), '..')
const root = join(docs, '..')
const generated = join(root, 'common/src/generated/resources')
const assets = join(root, 'common/src/main/resources/assets/fletchingrecipe')
const java = join(root, 'common/src/main/java/com/coolerpromc/fletchingrecipe')
const neoforgeJava = join(root, 'neoforge/src/main/java/com/coolerpromc/fletchingrecipe')
const recipeDir = join(generated, 'data/fletchingrecipe/recipe')

if (!existsSync(recipeDir)) {
  console.error(`No datagen recipes at ${recipeDir}. Run ./gradlew :neoforge:runData first.`)
  process.exit(1)
}

const readJson = (file) => JSON.parse(readFileSync(file, 'utf8'))
const walk = (dir) =>
  readdirSync(dir)
    .sort()
    .flatMap((name) => {
      const path = join(dir, name)
      return statSync(path).isDirectory() ? walk(path) : name.endsWith('.json') ? [path] : []
    })

const lang = readJson(join(assets, 'lang/en_us.json'))

/** An ingredient is an item id, a `#tag`, or a list of item ids. */
const ingredient = (value) => {
  if (typeof value === 'string') return [value]
  if (Array.isArray(value)) return value.flatMap(ingredient)
  return ['?']
}
const slot = (value) => (value ? { items: ingredient(value.ingredient), count: value.count ?? 1 } : null)

const recipes = walk(recipeDir)
  .map((file) => ({ file, json: readJson(file) }))
  .filter(({ json }) => json.type === 'fletchingrecipe:fletching')
  .map(({ file, json }) => ({
    id: `fletchingrecipe:${relative(recipeDir, file).replace(/\\/g, '/').replace(/\.json$/, '')}`,
    top: slot(json.top),
    middle: slot(json.middle),
    bottom: slot(json.bottom),
    output: { id: json.output.id, count: json.output.count ?? 1 },
  }))

// Config defaults are read from the code that writes them, so a changed default shows up here on the next sync.
const source = (file) => readFileSync(file, 'utf8')
const neoConfig = source(join(neoforgeJava, 'config/NeoForgeFletchingRecipeConfig.java'))
const options = []
for (const match of neoConfig.matchAll(/comment\("([^"]*)"\)\s*\.define(InRange)?\("(\w+)",\s*([^,)]+)(?:,\s*(\d+),\s*(\d+))?\)/g)) {
  const [, comment, ranged, key, value, min, max] = match
  options.push(
    ranged
      ? { key, type: 'integer', default: Number(value), min: Number(min), max: Number(max), comment }
      : { key, type: 'boolean', default: value.trim() === 'true', comment },
  )
}
const section = neoConfig.match(/builder\.push\("([^"]+)"\)/)?.[1] ?? null
if (options.length === 0) {
  console.error('Could not read the config options from NeoForgeFletchingRecipeConfig.java.')
  process.exit(1)
}

const explosiveConfig = source(join(java, 'config/ExplosiveIngredientConfig.java'))
const explosives = Object.fromEntries(
  [...explosiveConfig.matchAll(/explosiveIngredientMap\.put\("([^"]+)",\s*([\d.]+)f\)/g)].map(([, id, power]) => [id, Number(power)]),
)
const configFile = explosiveConfig.match(/getConfigDir\(\)\.toFile\(\),\s*"([^"]+)"/)?.[1]

mkdirSync(join(docs, 'public/gui'), { recursive: true })
copyFileSync(join(assets, 'textures/gui/fletching_table.png'), join(docs, 'public/gui/fletching_table.png'))
for (const sprite of ['empty_slot_gunpowder', 'empty_slot_tnt']) {
  copyFileSync(join(assets, `textures/gui/sprites/${sprite}.png`), join(docs, `public/gui/${sprite}.png`))
}

mkdirSync(join(docs, '.vitepress/data'), { recursive: true })
writeFileSync(
  join(docs, '.vitepress/data/data.json'),
  JSON.stringify({ lang, recipes, config: { section, options }, explosives: { file: configFile, defaults: explosives } }, null, 2),
)
console.log(`Synced ${recipes.length} recipes, ${options.length} config options, ${Object.keys(explosives).length} explosive ingredients.`)
