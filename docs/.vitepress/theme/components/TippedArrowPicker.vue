<script setup lang="ts">
import { ref } from 'vue'
import { POTIONS, TIPPED_AMOUNT, potionItemName, potionVariant } from '../fletchingrecipe'
import FletchingGui from './FletchingGui.vue'
import ItemSlot from './ItemSlot.vue'

const potion = ref<string>('poison')
const amount = ref(TIPPED_AMOUNT)

const label = (item: 'tipped_arrow' | 'lingering_potion', p: string) => {
  const variant = potionVariant(p)
  return variant ? `${potionItemName(item, p)} (${variant})` : potionItemName(item, p)
}
const safeAmount = () => Math.min(64, Math.max(1, Math.trunc(amount.value || 1)))
</script>

<template>
  <div class="fr-builder">
    <div class="fr-fields picker-fields">
      <label>
        <span>Lingering potion</span>
        <select v-model="potion">
          <option v-for="p in POTIONS" :key="p" :value="p">{{ label('lingering_potion', p) }}</option>
        </select>
      </label>
      <label>
        <span>
          Arrows per craft
          <span class="hint"><a href="../config/common">tippedArrowCraftingAmount</a>, default {{ TIPPED_AMOUNT }}</span>
        </span>
        <input v-model.number="amount" type="number" min="1" max="64" />
      </label>
    </div>
    <FletchingGui
      :top="{ id: 'minecraft:lingering_potion', potion }"
      :middle="{ id: 'minecraft:arrow', count: safeAmount() }"
      :result="{ id: 'minecraft:tipped_arrow', potion, count: safeAmount() }"
    >
      <p>
        1 {{ potionItemName('lingering_potion', potion) }} and {{ safeAmount() }} arrows make
        <strong>{{ safeAmount() }} × {{ potionItemName('tipped_arrow', potion) }}</strong>.
      </p>
      <p>The potion and the arrows can go in any two of the three slots.</p>
    </FletchingGui>
    <div class="all" aria-label="Every tipped arrow">
      <button
        v-for="p in POTIONS"
        :key="p"
        type="button"
        :class="{ active: p === potion }"
        :title="label('tipped_arrow', p)"
        @click="potion = p"
      >
        <ItemSlot id="minecraft:tipped_arrow" :potion="p" />
      </button>
    </div>
  </div>
</template>

<style scoped>
.picker-fields {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 10px;
}

.all {
  display: flex;
  flex-wrap: wrap;
  gap: 2px;
}

.all button {
  padding: 2px;
  border: 2px solid transparent;
  border-radius: 4px;
  line-height: 0;
}

.all button.active {
  border-color: var(--vp-c-brand-1);
}
</style>
