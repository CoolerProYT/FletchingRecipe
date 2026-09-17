import DefaultTheme from 'vitepress/theme'
import type { Theme } from 'vitepress'
import CommonConfigBuilder from './components/CommonConfigBuilder.vue'
import ConfigTable from './components/ConfigTable.vue'
import ExplosiveArrowPicker from './components/ExplosiveArrowPicker.vue'
import ExplosiveConfigBuilder from './components/ExplosiveConfigBuilder.vue'
import ExplosiveTable from './components/ExplosiveTable.vue'
import FletchingGui from './components/FletchingGui.vue'
import ItemSlot from './components/ItemSlot.vue'
import RecipeBuilder from './components/RecipeBuilder.vue'
import RecipeCard from './components/RecipeCard.vue'
import TippedArrowPicker from './components/TippedArrowPicker.vue'
import './style.css'

export default {
  extends: DefaultTheme,
  enhanceApp({ app }) {
    app.component('CommonConfigBuilder', CommonConfigBuilder)
    app.component('ConfigTable', ConfigTable)
    app.component('ExplosiveArrowPicker', ExplosiveArrowPicker)
    app.component('ExplosiveConfigBuilder', ExplosiveConfigBuilder)
    app.component('ExplosiveTable', ExplosiveTable)
    app.component('FletchingGui', FletchingGui)
    app.component('ItemSlot', ItemSlot)
    app.component('RecipeBuilder', RecipeBuilder)
    app.component('RecipeCard', RecipeCard)
    app.component('TippedArrowPicker', TippedArrowPicker)
  },
} satisfies Theme
