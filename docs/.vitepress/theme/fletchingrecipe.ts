// @ts-ignore
import raw from '../data/data.json'

export interface RecipeSlot {
  /** Item ids, or a single `#tag`. */
  items: string[]
  count: number
}

export interface Recipe {
  id: string
  top: RecipeSlot
  middle: RecipeSlot
  bottom: RecipeSlot | null
  output: { id: string; count: number }
}

export interface ConfigOption {
  key: string
  type: 'boolean' | 'integer'
  default: boolean | number
  min?: number
  max?: number
  comment: string
}

export const data = raw as unknown as {
  lang: Record<string, string>
  recipes: Recipe[]
  config: { section: string | null; options: ConfigOption[] }
  explosives: { file: string; defaults: Record<string, number> }
}

export const ID = /^[a-z0-9_.-]+:[a-z0-9_./-]+$/
export const TAG = /^#[a-z0-9_.-]+:[a-z0-9_./-]+$/

export const option = (key: string) => data.config.options.find((o) => o.key === key)!
export const TIPPED_AMOUNT = option('tippedArrowCraftingAmount').default as number
export const EXPLOSIVE_AMOUNT = option('explosiveArrowCraftingAmount').default as number

/** Vanilla potions, in creative menu order. Each has a hosted tipped arrow and lingering potion render. */
export const POTIONS = [
  'water', 'mundane', 'thick', 'awkward',
  'night_vision', 'long_night_vision', 'invisibility', 'long_invisibility',
  'leaping', 'long_leaping', 'strong_leaping', 'fire_resistance', 'long_fire_resistance',
  'swiftness', 'long_swiftness', 'strong_swiftness', 'slowness', 'long_slowness', 'strong_slowness',
  'turtle_master', 'long_turtle_master', 'strong_turtle_master', 'water_breathing', 'long_water_breathing',
  'healing', 'strong_healing', 'harming', 'strong_harming', 'poison', 'long_poison', 'strong_poison',
  'regeneration', 'long_regeneration', 'strong_regeneration', 'strength', 'long_strength', 'strong_strength',
  'weakness', 'long_weakness', 'luck', 'slow_falling', 'long_slow_falling',
  'wind_charged', 'weaving', 'oozing', 'infested',
] as const

export type Potion = (typeof POTIONS)[number]

const EFFECT_NAMES: Record<string, string> = {
  turtle_master: 'the Turtle Master',
  wind_charged: 'Wind Charging',
  infested: 'Infestation',
}

const title = (path: string) =>
  path
    .split('_')
    .map((word) => (['of', 'the', 'on', 'a'].includes(word) ? word : word.charAt(0).toUpperCase() + word.slice(1)))
    .join(' ')

const effect = (potion: string) => {
  const base = potion.replace(/^(long|strong)_/, '')
  return EFFECT_NAMES[base] ?? title(base)
}

/** Level and duration marker shown after a potion name, as in the creative menu tooltip. */
export function potionVariant(potion: string): string {
  if (potion.startsWith('long_')) return 'extended'
  if (potion.startsWith('strong_')) return 'level II'
  return ''
}

/** In-game names for tipped arrows and lingering potions. */
export function potionItemName(item: 'tipped_arrow' | 'lingering_potion', potion: string): string {
  const plain = ['mundane', 'thick', 'awkward']
  if (item === 'tipped_arrow') {
    if (potion === 'water') return 'Arrow of Splashing'
    if (plain.includes(potion)) return 'Tipped Arrow'
    return `Arrow of ${effect(potion)}`
  }
  if (potion === 'water') return 'Lingering Water Bottle'
  if (plain.includes(potion)) return `${title(potion)} Lingering Potion`
  return `Lingering Potion of ${effect(potion)}`
}

const TAG_NAMES: Record<string, string> = {
  '#minecraft:arrows': 'Any arrow',
  '#minecraft:logs': 'Any log',
  '#minecraft:planks': 'Any planks',
  '#minecraft:wool': 'Any wool',
}

/** Item shown for a tag ingredient. */
const TAG_ICONS: Record<string, string> = {
  '#minecraft:arrows': 'minecraft:arrow',
  '#minecraft:logs': 'minecraft:oak_log',
  '#minecraft:planks': 'minecraft:oak_planks',
  '#minecraft:wool': 'minecraft:white_wool',
}

/** Vanilla names that do not follow from the id. */
const ITEM_NAMES: Record<string, string> = {
  'minecraft:tnt': 'TNT',
}

/** Vanilla ids are turned into readable names. */
export function itemName(id: string, potion?: string | null): string {
  if (ITEM_NAMES[id]) return ITEM_NAMES[id]
  if (potion && (id === 'minecraft:tipped_arrow' || id === 'minecraft:lingering_potion')) {
    return potionItemName(id.split(':')[1] as 'tipped_arrow', potion)
  }
  if (TAG_NAMES[id]) return TAG_NAMES[id]
  if (id.startsWith('#')) return `Any ${title(id.split(':').pop() ?? id).toLowerCase()}`
  return title(id.split(':').pop() ?? id)
}

/** Hosted renders of vanilla items, one PNG per item id; tinted items have one per potion. Mojang's textures are not bundled here. */
export const VANILLA_ICONS = 'https://storage.googleapis.com/coolerpromc/textures'

/** Where to load an item's icon from, or null for items without a hosted render. */
export function itemIcon(id: string, potion?: string | null): string | null {
  const itemId = TAG_ICONS[id] ?? id
  if (!ID.test(itemId)) return null
  const [namespace, path] = itemId.split(':')
  if (namespace !== 'minecraft') return null
  if (path === 'tipped_arrow' || path === 'lingering_potion') {
    return `${VANILLA_ICONS}/minecraft/${path}/${potion ?? 'water'}.png`
  }
  return `${VANILLA_ICONS}/minecraft/${path}.png`
}

/** Vanilla explosion powers, for comparison with explosive ingredients. Names read mid-sentence. */
export const VANILLA_EXPLOSIONS: { name: string; power: number }[] = [
  { name: 'a ghast fireball', power: 1 },
  { name: 'a creeper', power: 3 },
  { name: 'TNT', power: 4 },
  { name: 'a bed in the Nether', power: 5 },
  { name: 'an end crystal', power: 6 },
]

/** A fully drawn bow or a crossbow fires a critical arrow, which explodes at full power; any other shot at half power. */
export function explosionPower(value: number, critical: boolean): number {
  return critical ? value : value / 2
}

export function decimal(value: number): string {
  return `${Math.round(value * 100) / 100}`
}
