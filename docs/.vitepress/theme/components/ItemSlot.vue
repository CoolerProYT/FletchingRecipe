<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { itemIcon, itemName } from '../fletchingrecipe'

const props = withDefaults(
  defineProps<{
    id?: string | null
    count?: number
    label?: boolean
    /** Potion of a tipped arrow or lingering potion. */
    potion?: string | null
    /** Explosive ingredient drawn in the corner, as the mod does for explosive arrows. */
    explosive?: string | null
    /** Bare icon without the slot frame, for use on top of the GUI texture. */
    bare?: boolean
  }>(),
  { id: null, count: 1, label: false, potion: null, explosive: null, bare: false },
)

const name = computed(() => {
  if (!props.id) return ''
  const base = itemName(props.id, props.potion)
  return props.explosive ? `Explosive ${base} (${itemName(props.explosive)})` : base
})
const src = computed(() => (props.id ? itemIcon(props.id, props.potion) : null))
const badge = computed(() => (props.explosive ? itemIcon(props.explosive) : null))

// Falls back to initials when an item has no icon or the hosted icon fails to load.
const failed = ref(false)
watch(src, () => (failed.value = false))
const initials = computed(() =>
  itemName(props.id ?? '')
    .split(' ')
    .filter((word) => /^[A-Z]/.test(word))
    .slice(0, 2)
    .map((word) => word[0])
    .join(''),
)
</script>

<template>
  <span class="fr-item" :class="{ 'with-label': label }">
    <span class="fr-slot" :class="{ bare }" :title="name" :aria-label="name" role="img">
      <img v-if="src && !failed" class="pixelated" :src="src" alt="" loading="lazy" @error="failed = true" />
      <span v-else-if="id" class="fr-initials">{{ initials }}</span>
      <img v-if="id && badge" class="fr-badge pixelated" :src="badge" alt="" loading="lazy" />
      <span v-if="id && count > 1" class="fr-count">{{ count }}</span>
    </span>
    <span v-if="label && id" class="fr-label">{{ name }}</span>
  </span>
</template>

<style scoped>
.fr-item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  vertical-align: middle;
}

.fr-slot {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  flex: none;
  background: var(--fr-slot-bg);
  border: 2px solid;
  border-color: var(--fr-slot-dark) var(--fr-slot-light) var(--fr-slot-light) var(--fr-slot-dark);
}

.fr-slot.bare {
  width: calc(16px * var(--fr-scale, 2));
  height: calc(16px * var(--fr-scale, 2));
  background: none;
  border: 0;
}

.fr-slot.bare > img:first-child {
  width: 100%;
  height: 100%;
}

.fr-slot img {
  width: 32px;
  height: 32px;
}

.fr-slot .fr-badge {
  position: absolute;
  top: 0;
  left: 0;
  width: 16px;
  height: 16px;
}

.fr-slot.bare .fr-badge {
  width: 50%;
  height: 50%;
}

.fr-slot.bare .fr-count {
  right: -1px;
  bottom: -2px;
  font-size: calc(7px * var(--fr-scale, 2));
}

.fr-initials {
  font: 600 12px/1 var(--vp-font-family-mono);
  color: #fff;
  text-shadow: 1px 1px 0 #3f3f3f;
}

.fr-count {
  position: absolute;
  right: 1px;
  bottom: -1px;
  font: 700 12px/1 var(--vp-font-family-mono);
  color: #fff;
  text-shadow: 1px 1px 0 #3f3f3f;
}

.fr-label {
  font-weight: 500;
}
</style>
