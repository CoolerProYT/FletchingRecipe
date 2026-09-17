import { defineConfig } from 'vitepress'

// GitHub Pages serves a project site from /<repository>/. For a custom domain or a user site, build with DOCS_BASE=/.
const base = process.env.DOCS_BASE ?? '/FletchingRecipe/'
const ICON = 'https://storage.googleapis.com/coolerpromc/textures/minecraft/arrow.png'

export default defineConfig({
  title: 'Fletching Recipe',
  description: 'The fletching table crafts arrows, tipped arrows and explosive arrows. Wiki for the Minecraft mod Fletching Recipe.',
  base,
  cleanUrls: true,
  lastUpdated: true,
  srcExclude: ['README.md', 'scripts/**'],
  head: [['link', { rel: 'icon', type: 'image/png', href: ICON }]],
  themeConfig: {
    logo: { src: ICON, alt: '' },
    nav: [
      { text: 'Guide', link: '/guide/getting-started', activeMatch: '^/guide/' },
      { text: 'Configuration', link: '/config/', activeMatch: '^/config/' },
      { text: 'Datapacks', link: '/datapacks/recipes', activeMatch: '^/datapacks/' },
    ],
    sidebar: [
      {
        text: 'Guide',
        items: [
          { text: 'Getting started', link: '/guide/getting-started' },
          { text: 'The fletching table', link: '/guide/fletching-table' },
          { text: 'Arrow recipes', link: '/guide/recipes' },
          { text: 'Tipped arrows', link: '/guide/tipped-arrows' },
          { text: 'Explosive arrows', link: '/guide/explosive-arrows' },
          { text: 'Other mods', link: '/guide/compat' },
        ],
      },
      {
        text: 'Configuration',
        items: [
          { text: 'Overview', link: '/config/' },
          { text: 'Common config', link: '/config/common' },
          { text: 'Explosive ingredients', link: '/config/explosive-ingredients' },
        ],
      },
      {
        text: 'Datapacks',
        items: [{ text: 'Fletching recipes', link: '/datapacks/recipes' }],
      },
      { text: 'FAQ', link: '/faq' },
    ],
    socialLinks: [
      {
        icon: {
          svg: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="currentColor" d="M4 19v-9q0-.475.213-.9t.587-.7l6-4.5q.525-.4 1.2-.4t1.2.4l6 4.5q.375.275.588.7T20 10v9q0 .825-.588 1.413T18 21h-3q-.425 0-.712-.288T14 20v-5q0-.425-.288-.712T13 14h-2q-.425 0-.712.288T10 15v5q0 .425-.288.713T9 21H6q-.825 0-1.412-.587T4 19"/></svg>',
        },
        link: 'https://coolerpromc.com/',
        ariaLabel: 'CoolerProMC website',
      },
      { icon: 'github', link: 'https://github.com/CoolerProYT/FletchingRecipe' },
      { icon: 'discord', link: 'https://discord.gg/hvFfqsqQm8' },
    ],
    editLink: {
      pattern: 'https://github.com/CoolerProYT/FletchingRecipe/edit/26.3/docs/:path',
      text: 'Edit this page on GitHub',
    },
    search: { provider: 'local' },
    outline: { level: [2, 3] },
    footer: {
      message: 'All Rights Reserved.',
      copyright: 'Copyright © CoolerProMC',
    },
  },
})
