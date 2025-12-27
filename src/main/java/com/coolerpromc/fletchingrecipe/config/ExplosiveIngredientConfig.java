package com.coolerpromc.fletchingrecipe.config;

import com.coolerpromc.fletchingrecipe.FletchingRecipe;
import com.coolerpromc.fletchingrecipe.config.exception.ItemNotFoundException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ExplosiveIngredientConfig {
    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Float>>(){}.getType();

    private static Map<String, Float> explosiveIngredientMap = new ConcurrentHashMap<>();
    public static final Map<RegistryEntry<Item>, Float> explosiveIngredients = new ConcurrentHashMap<>();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "fletchingrecipe-explosive-ingredient.json");

    private static WatchService watchService;
    private static Thread watchThread;

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            saveDefaults();
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Map<String, Float> loaded = GSON.fromJson(reader, MAP_TYPE);
            if (loaded != null) {
                explosiveIngredientMap = new ConcurrentHashMap<>(loaded);
            } else {
                explosiveIngredientMap = new ConcurrentHashMap<>();
            }
        } catch (Exception e) {
            System.out.println("Failed to load explosive ingredient config! " + e.getMessage());
            explosiveIngredientMap = new ConcurrentHashMap<>();
        }

        explosiveIngredients.clear();

        for (Map.Entry<String, Float> entry : explosiveIngredientMap.entrySet()){
            try {
                Identifier id = Identifier.of(entry.getKey());
                Optional<RegistryEntry.Reference<Item>> holder = Registries.ITEM.getEntry(id);

                if (holder.isEmpty()){
                    throw new ItemNotFoundException();
                }
                explosiveIngredients.put(holder.get(), entry.getValue());
            }
            catch (InvalidIdentifierException e){
                FletchingRecipe.LOGGER.error("[Explosive Ingredient Config] Invalid item id defined: {}", entry.getKey());
            }
            catch (ItemNotFoundException e){
                FletchingRecipe.LOGGER.error("[Explosive Ingredient Config] Defined item not found: {}", entry.getKey());
            }
        }
    }

    public static void saveDefaults() {
        explosiveIngredientMap.put("minecraft:tnt", 2f);
        explosiveIngredientMap.put("minecraft:fire_charge", 4f);
        explosiveIngredientMap.put("minecraft:gunpowder", 0.5f);
        save();
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(explosiveIngredientMap, writer);
        } catch (Exception e) {
            System.out.println("Failed to explosive ingredient config! " + e.getMessage());
        }
    }

    public static synchronized void startWatcher() {
        if (watchThread != null && watchThread.isAlive()) {
            return;
        }

        try {
            watchService = FileSystems.getDefault().newWatchService();
            Path configDir = CONFIG_FILE.getParentFile().toPath();
            configDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            watchThread = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        WatchKey key = watchService.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            Path changed = (Path) event.context();
                            if (changed.toString().equals(CONFIG_FILE.getName())) {
                                System.out.println("[FletchingRecipe] Explosive ingredient config file changed, reloading...");
                                load();
                            }
                        }
                        key.reset();
                    }
                } catch (ClosedWatchServiceException cwse) {
                    // Normal shutdown
                } catch (Exception e) {
                    System.out.println("Error watching config file: " + e.getMessage());
                }
            }, "Explosive Ingredient Config Watcher");

            watchThread.setDaemon(true);
            watchThread.start();
        } catch (Exception e) {
            System.out.println("Error hot reloading explosive ingredient config: " + e.getMessage());
        }
    }

    public static synchronized void stopWatcher() {
        try {
            if (watchService != null) {
                watchService.close();
            }
        } catch (Exception ignored) {}
        if (watchThread != null) {
            watchThread.interrupt();
        }
        watchService = null;
        watchThread = null;

        System.out.println("[FletchingRecipe] Explosive ingredient config watcher stopped.");
    }
}