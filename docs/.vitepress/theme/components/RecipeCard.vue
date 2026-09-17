<script setup lang="ts">
import { computed } from 'vue'
import { data, itemName, type RecipeSlot } from '../fletchingrecipe'
import FletchingGui from './FletchingGui.vue'

const props = defineProps<{ id: string }>()

const recipe = computed(() => data.recipes.find((r) => r.id === props.id || r.id === `fletchingrecipe:${props.id}`))

const item = (slot: RecipeSlot | null) => (slot ? { id: slot.items[0], count: slot.count } : null)
const describe = (slot: RecipeSlot) => `${slot.count} × ${slot.items.map((id) => itemName(id)).join(' or ')}`
</script>

<template>
  <FletchingGui
    v-if="recipe"
    :top="item(recipe.top)"
    :middle="item(recipe.middle)"
    :bottom="item(recipe.bottom)"
    :result="recipe.output"
  >
    <ul class="fr-recipe-list">
      <li><strong>Top:</strong> {{ describe(recipe.top) }}</li>
      <li><strong>Middle:</strong> {{ describe(recipe.middle) }}</li>
      <li><strong>Bottom:</strong> {{ recipe.bottom ? describe(recipe.bottom) : 'empty' }}</li>
      <li><strong>Makes:</strong> {{ recipe.output.count }} × {{ itemName(recipe.output.id) }}</li>
    </ul>
    <code class="fr-recipe-id">{{ recipe.id }}</code>
  </FletchingGui>
  <p v-else class="fr-muted">Recipe {{ id }} not found.</p>
</template>

<style scoped>
.fr-recipe-list {
  margin: 0 0 6px;
  padding-left: 18px;
  list-style: disc;
}

.fr-recipe-list li {
  margin: 2px 0;
}

.fr-recipe-id {
  font-size: 12px;
}
</style>
