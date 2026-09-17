<script setup lang="ts">
import { data } from '../fletchingrecipe'
</script>

<template>
  <table>
    <thead>
      <tr>
        <th>Option</th>
        <th>Type</th>
        <th>Default</th>
        <th>Range</th>
        <th>What it does</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="option in data.config.options" :key="option.key">
        <td><code>{{ option.key }}</code></td>
        <td>{{ option.type === 'boolean' ? 'Boolean' : 'Integer' }}</td>
        <td><code>{{ option.default }}</code></td>
        <td>{{ option.type === 'integer' ? `${option.min} to ${option.max}` : '' }}</td>
        <td><slot :name="option.key">{{ option.comment }}</slot></td>
      </tr>
    </tbody>
  </table>
</template>
