<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { withBase } from 'vitepress'
import ItemSlot from './ItemSlot.vue'

/** An item drawn in a slot. */
export interface GuiItem {
  id: string
  count?: number
  potion?: string | null
  explosive?: string | null
}

const props = withDefaults(
  defineProps<{
    top?: GuiItem | null
    middle?: GuiItem | null
    bottom?: GuiItem | null
    /** Item in the explosive slot on the left. */
    explosive?: GuiItem | null
    result?: GuiItem | null
    /** False draws the table as it looks with allowExplosiveCrafting = false. */
    explosiveSlot?: boolean
  }>(),
  { top: null, middle: null, bottom: null, explosive: null, result: null, explosiveSlot: true },
)

// The empty explosive slot cycles between a gunpowder and a TNT outline, as in game.
const ghosts = ['empty_slot_gunpowder', 'empty_slot_tnt']
const ghost = ref(0)
let timer: ReturnType<typeof setInterval> | undefined
onMounted(() => (timer = setInterval(() => (ghost.value = (ghost.value + 1) % ghosts.length), 1600)))
onBeforeUnmount(() => clearInterval(timer))

/** Slot positions in GUI pixels, from FletchingTableMenu. */
const SLOTS = {
  explosive: [17, 35],
  top: [48, 17],
  middle: [48, 35],
  bottom: [48, 53],
  result: [124, 35],
} as const

const at = (key: keyof typeof SLOTS) => ({
  left: `calc(${SLOTS[key][0]}px * var(--fr-scale))`,
  top: `calc(${SLOTS[key][1]}px * var(--fr-scale))`,
})

const inputs = ['top', 'middle', 'bottom'] as const
</script>

<template>
  <div class="fr-gui" :style="{ '--fr-gui': `url(${withBase('/gui/fletching_table.png')})` }">
    <div class="texture" role="img" aria-label="Fletching table">
      <span v-if="!explosiveSlot" class="covered" />
      <img
        v-else-if="!explosive"
        class="ghost pixelated"
        :style="at('explosive')"
        :src="withBase(`/gui/${ghosts[ghost]}.png`)"
        alt=""
      />
      <span v-else class="at" :style="at('explosive')">
        <ItemSlot bare v-bind="props.explosive!" />
      </span>
      <template v-for="key in inputs" :key="key">
        <span v-if="props[key]" class="at" :style="at(key)">
          <ItemSlot bare v-bind="props[key]!" />
        </span>
      </template>
      <span v-if="result" class="at" :style="at('result')">
        <ItemSlot bare v-bind="result" />
      </span>
    </div>
    <div v-if="$slots.default" class="caption"><slot /></div>
  </div>
</template>

<style scoped>
.fr-gui {
  --fr-scale: 2;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 20px;
  margin: 16px 0;
}

@media (max-width: 420px) {
  .fr-gui {
    --fr-scale: 1.75;
  }
}

/* The top 176x80 of the 256x256 texture: the crafting area without the player inventory. */
.texture {
  position: relative;
  flex: none;
  width: calc(176px * var(--fr-scale));
  height: calc(80px * var(--fr-scale));
  background-image: var(--fr-gui);
  background-size: calc(256px * var(--fr-scale)) calc(256px * var(--fr-scale));
  background-repeat: no-repeat;
  image-rendering: pixelated;
}

/* The texture's bottom border sits under the inventory; this redraws it below the crafting area. */
.texture::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: calc(4px * var(--fr-scale));
  background-image: var(--fr-gui);
  background-size: calc(256px * var(--fr-scale)) calc(256px * var(--fr-scale));
  background-position: 0 calc(-162px * var(--fr-scale));
}

.at,
.ghost {
  position: absolute;
  width: calc(16px * var(--fr-scale));
  height: calc(16px * var(--fr-scale));
}

.ghost {
  opacity: 0.9;
}

/* Matches the fill the mod draws over the slot when explosive crafting is off. */
.covered {
  position: absolute;
  left: calc(16px * var(--fr-scale));
  top: calc(34px * var(--fr-scale));
  width: calc(19px * var(--fr-scale));
  height: calc(19px * var(--fr-scale));
  background: #c6c6c6;
}

.caption {
  flex: 1 1 200px;
  min-width: 0;
  font-size: 0.9em;
  color: var(--vp-c-text-2);
}

.caption :deep(p) {
  margin: 0 0 6px;
}
</style>
