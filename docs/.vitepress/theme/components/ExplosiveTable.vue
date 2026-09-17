<script setup lang="ts">
import { data, decimal, explosionPower } from '../fletchingrecipe'
import ItemSlot from './ItemSlot.vue'

const rows = Object.entries(data.explosives.defaults).sort((a, b) => a[1] - b[1])
</script>

<template>
  <table>
    <thead>
      <tr>
        <th>Ingredient</th>
        <th>Value</th>
        <th>Power, fully drawn</th>
        <th>Power, other shots</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="[id, value] in rows" :key="id">
        <td><ItemSlot :id="id" label /></td>
        <td><code>{{ value }}</code></td>
        <td>{{ decimal(explosionPower(value, true)) }}</td>
        <td>{{ decimal(explosionPower(value, false)) }}</td>
      </tr>
    </tbody>
  </table>
</template>
