<script setup lang="ts">
import { computed, reactive } from 'vue'
import { ID, data, decimal, explosionPower } from '../fletchingrecipe'
import BuilderOutput from './BuilderOutput.vue'
import ItemSlot from './ItemSlot.vue'

interface Row {
  id: string
  value: number
}

const defaults = () => Object.entries(data.explosives.defaults).map(([id, value]) => ({ id, value }))
const rows = reactive<Row[]>(defaults())

const reset = () => rows.splice(0, rows.length, ...defaults())
const add = () => rows.push({ id: 'minecraft:wind_charge', value: 1 })

// Gson writes the file with 4-space indents and no space after the colon; either style loads.
const text = computed(() => JSON.stringify(Object.fromEntries(rows.map((row) => [row.id, row.value])), null, 4))

const problems = computed(() => {
  const list: string[] = []
  const seen = new Set<string>()
  rows.forEach((row, i) => {
    const n = `Row ${i + 1}`
    if (!ID.test(row.id)) list.push(`${n}: the item should be an id like minecraft:tnt.`)
    else if (seen.has(row.id)) list.push(`${n}: ${row.id} is listed twice; only the last value would be kept.`)
    seen.add(row.id)
    if (typeof row.value !== 'number' || !(row.value > 0)) list.push(`${n}: the value should be a number above 0.`)
  })
  return list
})
</script>

<template>
  <div class="fr-builder">
    <div class="fr-grid">
      <div class="fr-fields">
        <fieldset>
          <legend>Explosive ingredients</legend>
          <div v-for="(row, i) in rows" :key="i" class="row-item">
            <ItemSlot :id="row.id" />
            <div class="row-fields">
              <div class="line">
                <input v-model.trim="row.id" spellcheck="false" aria-label="Item id" />
                <input v-model.number="row.value" class="value" type="number" min="0" step="0.5" aria-label="Value" />
                <button type="button" class="remove" :aria-label="`Remove ${row.id}`" @click="rows.splice(i, 1)">✕</button>
              </div>
              <span class="hint">
                power {{ decimal(explosionPower(row.value || 0, true)) }} fully drawn,
                {{ decimal(explosionPower(row.value || 0, false)) }} otherwise
              </span>
            </div>
          </div>
          <p v-if="!rows.length" class="hint">No ingredients: the explosive slot accepts nothing.</p>
          <div class="buttons">
            <button type="button" class="small-button" @click="add">+ Add ingredient</button>
            <button type="button" class="small-button" @click="reset">Reset to defaults</button>
          </div>
        </fieldset>
      </div>
      <BuilderOutput :file="`config/${data.explosives.file}`" :text="text" :problems="problems">
        Replace the whole file. Give players the same file as the server, so the slot and JEI accept the same items.
      </BuilderOutput>
    </div>
  </div>
</template>

<style scoped>
.row-item {
  display: flex;
  gap: 10px;
  padding-bottom: 10px;
  border-bottom: 1px dashed var(--vp-c-divider);
}

.row-fields {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.line {
  display: flex;
  gap: 6px;
}

.line .value {
  flex: 0 0 90px;
}

.remove {
  flex: none;
  width: 30px;
  border-radius: 6px;
  color: var(--vp-c-text-3);
}

.remove:hover {
  background: var(--vp-c-danger-soft);
  color: var(--vp-c-danger-1);
}

.buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
</style>
