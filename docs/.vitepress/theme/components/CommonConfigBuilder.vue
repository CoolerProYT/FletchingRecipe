<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { data } from '../fletchingrecipe'
import BuilderOutput from './BuilderOutput.vue'

const loader = ref<'neoforge' | 'fabric'>('neoforge')
const values = reactive(Object.fromEntries(data.config.options.map((o) => [o.key, o.default])) as Record<string, boolean | number>)

const reset = () => data.config.options.forEach((o) => (values[o.key] = o.default))

/** Same layout the loaders write: NeoForge nests the options in a section with default and range comments. */
const text = computed(() => {
  const neoforge = loader.value === 'neoforge'
  const indent = neoforge ? '\t' : ''
  const lines: string[] = neoforge && data.config.section ? [`["${data.config.section}"]`] : []
  for (const option of data.config.options) {
    lines.push(`${indent}#${option.comment}`)
    if (neoforge && option.type === 'integer') {
      lines.push(`${indent}# Default: ${option.default}`, `${indent}# Range: ${option.min} ~ ${option.max}`)
    }
    lines.push(`${indent}${option.key} = ${values[option.key]}`)
  }
  return lines.join('\n') + '\n'
})

const problems = computed(() =>
  data.config.options
    .filter((o) => o.type === 'integer')
    .filter((o) => {
      const value = values[o.key] as number
      return !Number.isInteger(value) || value < o.min! || value > o.max!
    })
    .map((o) => `${o.key} should be a whole number from ${o.min} to ${o.max}.`),
)
</script>

<template>
  <div class="fr-builder">
    <div class="fr-grid">
      <div class="fr-fields">
        <label>
          <span>Loader</span>
          <select v-model="loader">
            <option value="neoforge">NeoForge</option>
            <option value="fabric">Fabric</option>
          </select>
        </label>
        <template v-for="option in data.config.options" :key="option.key">
          <label v-if="option.type === 'boolean'" class="check">
            <input v-model="values[option.key]" type="checkbox" />
            <code>{{ option.key }}</code>
          </label>
          <label v-else>
            <span>
              <code>{{ option.key }}</code>
              <span class="hint">{{ option.min }} to {{ option.max }}, default {{ option.default }}</span>
            </span>
            <input v-model.number="values[option.key]" type="number" :min="option.min" :max="option.max" />
          </label>
        </template>
        <button type="button" class="small-button" @click="reset">Reset to defaults</button>
      </div>
      <BuilderOutput file="config/fletchingrecipe-common.toml" :text="text" :problems="problems">
        Replace the whole file, on the server and on every player's game. See
        <a href="#reloading-and-multiplayer">reloading and multiplayer</a>.
      </BuilderOutput>
    </div>
  </div>
</template>
