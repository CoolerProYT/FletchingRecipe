<script setup lang="ts">
import { computed, reactive } from 'vue'
import { ID, POTIONS, TAG, data, itemName, potionItemName, type Recipe } from '../fletchingrecipe'
import BuilderOutput from './BuilderOutput.vue'
import FletchingGui from './FletchingGui.vue'

/** `items` is a comma-separated list of item ids, or a single #tag. */
interface SlotForm {
  used: boolean
  items: string
  count: number
}

const POTION_ITEMS = ['minecraft:tipped_arrow', 'minecraft:lingering_potion', 'minecraft:potion', 'minecraft:splash_potion']

const form = reactive({
  namespace: 'mypack',
  path: 'fletching/arrow_from_bone',
  top: { used: true, items: 'minecraft:bone', count: 1 } as SlotForm,
  middle: { used: true, items: 'minecraft:stick', count: 1 } as SlotForm,
  bottom: { used: true, items: 'minecraft:feather', count: 1 } as SlotForm,
  output: { id: 'minecraft:arrow', count: 4, potion: '' },
})

const slots = ['top', 'middle', 'bottom'] as const

function load(recipe: Recipe) {
  const [namespace, path] = recipe.id.split(':')
  form.namespace = namespace
  form.path = path
  for (const key of slots) {
    const slot = recipe[key]
    form[key] = { used: !!slot, items: slot ? slot.items.join(', ') : '', count: slot?.count ?? 1 }
  }
  form.output = { id: recipe.output.id, count: recipe.output.count, potion: '' }
}

const list = (slot: SlotForm) =>
  slot.items
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean)

const ingredient = (slot: SlotForm) => {
  const items = list(slot)
  return items.length === 1 ? items[0] : items
}

const hasPotion = computed(() => POTION_ITEMS.includes(form.output.id))

const json = computed(() => {
  const recipe: Record<string, unknown> = { type: 'fletchingrecipe:fletching' }
  for (const key of slots) {
    const slot = form[key]
    if (slot.used) recipe[key] = { ingredient: ingredient(slot), count: slot.count }
  }
  const output: Record<string, unknown> = { id: form.output.id, count: form.output.count }
  if (hasPotion.value && form.output.potion) {
    output.components = { 'minecraft:potion_contents': { potion: `minecraft:${form.output.potion}` } }
  }
  recipe.output = output
  return JSON.stringify(recipe, null, 2)
})

const file = computed(() => `data/${form.namespace}/recipe/${form.path}.json`)

const problems = computed(() => {
  const out: string[] = []
  if (!/^[a-z0-9_.-]+$/.test(form.namespace)) out.push('The namespace should be lowercase letters, digits, _ . or -.')
  if (!/^[a-z0-9_./-]+$/.test(form.path)) out.push('The recipe name should be lowercase letters, digits, _ . - or /.')
  for (const key of slots) {
    const slot = form[key]
    if (!slot.used) continue
    const items = list(slot)
    if (!items.length) out.push(`${key}: add an item id or a #tag.`)
    else if (items.some((id) => id.startsWith('#'))) {
      if (items.length > 1 || !TAG.test(items[0])) out.push(`${key}: a tag is written alone, like #minecraft:planks.`)
    } else if (!items.every((id) => ID.test(id))) out.push(`${key}: item ids look like minecraft:feather.`)
    if (!Number.isInteger(slot.count) || slot.count < 1 || slot.count > 99) out.push(`${key}: count should be 1 to 99.`)
  }
  if (!ID.test(form.output.id)) out.push('Output: the item should be an id like minecraft:arrow.')
  if (!Number.isInteger(form.output.count) || form.output.count < 1 || form.output.count > 99) {
    out.push('Output: count should be 1 to 99.')
  }
  return out
})

const preview = (slot: SlotForm) => {
  if (!slot.used) return null
  const first = list(slot)[0]
  return first ? { id: first, count: slot.count } : null
}
</script>

<template>
  <div class="fr-builder">
    <div class="presets">
      <span class="hint">Start from:</span>
      <button v-for="recipe in data.recipes" :key="recipe.id" type="button" class="small-button" @click="load(recipe)">
        {{ recipe.id }}
      </button>
    </div>
    <FletchingGui
      :explosive-slot="false"
      :top="preview(form.top)"
      :middle="preview(form.middle)"
      :bottom="preview(form.bottom)"
      :result="{ id: form.output.id, count: form.output.count, potion: form.output.potion || null }"
    />
    <div class="fr-grid">
      <div class="fr-fields">
        <div class="row">
          <label>
            <span>Namespace</span>
            <input v-model.trim="form.namespace" spellcheck="false" />
          </label>
          <label>
            <span>Recipe name</span>
            <input v-model.trim="form.path" spellcheck="false" />
          </label>
        </div>
        <fieldset v-for="key in slots" :key="key">
          <legend>
            <code>{{ key }}</code>
            <label v-if="key === 'bottom'" class="check">
              <input v-model="form.bottom.used" type="checkbox" />
              used
            </label>
          </legend>
          <template v-if="form[key].used">
            <div class="slot-row">
              <label>
                <span>Items <span class="hint">ids separated by commas, or one #tag</span></span>
                <input v-model="form[key].items" spellcheck="false" />
              </label>
              <label class="count">
                <span>Count</span>
                <input v-model.number="form[key].count" type="number" min="1" max="99" />
              </label>
            </div>
          </template>
          <span v-else class="hint">Left out: the recipe only matches when the bottom slot is empty.</span>
        </fieldset>
        <fieldset>
          <legend><code>output</code></legend>
          <div class="slot-row">
            <label>
              <span>Item</span>
              <input v-model.trim="form.output.id" spellcheck="false" />
            </label>
            <label class="count">
              <span>Count</span>
              <input v-model.number="form.output.count" type="number" min="1" max="99" />
            </label>
          </div>
          <label v-if="hasPotion">
            <span>Potion <span class="hint">sets minecraft:potion_contents</span></span>
            <select v-model="form.output.potion">
              <option value="">None</option>
              <option v-for="p in POTIONS" :key="p" :value="p">{{ p }}</option>
            </select>
          </label>
          <span class="hint">
            Makes {{ form.output.count }} ×
            {{ hasPotion && form.output.potion && form.output.id === 'minecraft:tipped_arrow' ? potionItemName('tipped_arrow', form.output.potion) : itemName(form.output.id) }}
          </span>
        </fieldset>
      </div>
      <BuilderOutput :file="file" :text="json" :problems="problems">
        Put it in a datapack and run <code>/reload</code>. A file at the same path as a built-in recipe replaces it.
      </BuilderOutput>
    </div>
  </div>
</template>

<style scoped>
.presets {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px;
}

.presets .hint {
  font-size: 13px;
  color: var(--vp-c-text-2);
}

.slot-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 80px;
  gap: 8px;
}

legend .check {
  display: inline-flex;
  flex-direction: row;
  align-items: center;
  gap: 6px;
  font-weight: 400;
}
</style>
