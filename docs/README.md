# Fletching Recipe wiki

VitePress site for the mod. Recipes, config defaults and the GUI texture are read from the mod itself, so regenerate the mod's data before building when recipes change.

```bash
./gradlew :neoforge:runData   # from the repository root, when mod recipes changed
cd docs
npm install
npm run dev                   # syncs data, then serves http://localhost:5173/FletchingRecipe/
npm run build                 # syncs data, then builds to .vitepress/dist
```

`npm run sync` (run automatically by `dev` and `build`) writes `.vitepress/data/data.json` and copies the fletching table GUI textures to `public/gui/`. Both are git-ignored. It reads:

- recipes from `common/src/generated/resources/data/fletchingrecipe/recipe/`
- config options and defaults from `NeoForgeFletchingRecipeConfig.java`
- default explosive ingredients from `ExplosiveIngredientConfig.saveDefaults()`

Item icons are the hosted vanilla renders at `https://storage.googleapis.com/coolerpromc/textures/minecraft/`, set in `.vitepress/theme/fletchingrecipe.ts`. Tipped arrows and lingering potions use the per-potion renders in `tipped_arrow/` and `lingering_potion/`.

The site is published by `.github/workflows/docs.yml`. See the comments there for the one-time GitHub setup.
