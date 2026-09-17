<script setup lang="ts">
import { computed, ref } from 'vue'
import {
  EXPLOSIVE_AMOUNT,
  POTIONS,
  VANILLA_EXPLOSIONS,
  data,
  decimal,
  explosionPower,
  itemName,
  potionItemName,
} from '../fletchingrecipe'
import FletchingGui from './FletchingGui.vue'

const defaults = Object.entries(data.explosives.defaults)
const explosive = ref(defaults[0][0])
const arrow = ref<'minecraft:arrow' | 'minecraft:spectral_arrow' | 'minecraft:tipped_arrow'>('minecraft:arrow')
const potion = ref('poison')
const amount = ref(EXPLOSIVE_AMOUNT)

const safeAmount = computed(() => Math.min(64, Math.max(1, Math.trunc(amount.value || 1))))
const value = computed(() => data.explosives.defaults[explosive.value])
const arrowPotion = computed(() => (arrow.value === 'minecraft:tipped_arrow' ? potion.value : null))
const arrowName = computed(() => itemName(arrow.value, arrowPotion.value))

/** The closest vanilla explosion at or below a power, for a sense of scale. */
const compare = (power: number) => {
  const below = VANILLA_EXPLOSIONS.filter((e) => e.power <= power).pop()
  return below ? `${below.power === power ? 'as strong as' : 'stronger than'} ${below.name} (${below.power})` : 'weaker than a ghast fireball (1)'
}
</script>

<template>
  <div class="fr-builder">
    <div class="fr-fields picker-fields">
      <label>
        <span>Explosive ingredient</span>
        <select v-model="explosive">
          <option v-for="[id, power] in defaults" :key="id" :value="id">{{ itemName(id) }} ({{ power }})</option>
        </select>
      </label>
      <label>
        <span>Arrow</span>
        <select v-model="arrow">
          <option value="minecraft:arrow">Arrow</option>
          <option value="minecraft:spectral_arrow">Spectral Arrow</option>
          <option value="minecraft:tipped_arrow">Tipped Arrow</option>
        </select>
      </label>
      <label v-if="arrow === 'minecraft:tipped_arrow'">
        <span>Potion</span>
        <select v-model="potion">
          <option v-for="p in POTIONS" :key="p" :value="p">{{ potionItemName('tipped_arrow', p) }} ({{ p }})</option>
        </select>
      </label>
      <label>
        <span>
          Arrows per craft
          <span class="hint"><a href="../config/common">explosiveArrowCraftingAmount</a>, default {{ EXPLOSIVE_AMOUNT }}</span>
        </span>
        <input v-model.number="amount" type="number" min="1" max="64" />
      </label>
    </div>
    <FletchingGui
      :explosive="{ id: explosive }"
      :middle="{ id: arrow, potion: arrowPotion, count: safeAmount }"
      :result="{ id: arrow, potion: arrowPotion, count: safeAmount, explosive }"
    >
      <p>
        1 {{ itemName(explosive) }} and {{ safeAmount }} × {{ arrowName }} make
        <strong>{{ safeAmount }} × explosive {{ arrowName }}</strong>.
      </p>
      <ul class="power">
        <li>Fully drawn bow or crossbow: power <strong>{{ decimal(explosionPower(value, true)) }}</strong>, {{ compare(explosionPower(value, true)) }}</li>
        <li>Any other shot: power <strong>{{ decimal(explosionPower(value, false)) }}</strong>, {{ compare(explosionPower(value, false)) }}</li>
      </ul>
    </FletchingGui>
  </div>
</template>

<style scoped>
.picker-fields {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 10px;
}

.power {
  margin: 0;
  padding-left: 18px;
  list-style: disc;
}
</style>
