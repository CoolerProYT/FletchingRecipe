package com.coolerpromc.fletchingrecipe.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FletchingRecipeConfig {
    private static final String CONFIG_FILE_NAME = "fletchingrecipe-common.toml";
    private static CommentedFileConfig config;
    private static CommentedFileConfig serverConfig;
    private static ScheduledExecutorService fileWatcher;
    private static long lastModifiedTime = 0;
    private static long lastServerModifiedTime = 0;

    private static boolean allowExplosiveCrafting = true;
    private static int tippedArrowCraftingAmount = 16;
    private static int explosiveArrowCraftingAmount = 4;

    public static void init() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME);
        config = CommentedFileConfig.builder(configPath)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        config.load();
        setupConfig(config);
        config.save();

        loadConfigValues(config);
        lastModifiedTime = getFileModifiedTime(configPath);

        startFileWatcher(configPath, false);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            setupServerConfig();
        }

        ServerLifecycleEvents.SERVER_STARTED.register(server -> setupServerConfig());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            if (serverConfig != null) {
                serverConfig.close();
                serverConfig = null;
            }
        });
    }

    private static void setupServerConfig() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            Path serverConfigPath = FabricLoader.getInstance().getGameDir()
                    .resolve("saves")
                    .resolve("world")
                    .resolve("serverconfig")
                    .resolve(CONFIG_FILE_NAME);

            if (serverConfigPath.toFile().exists()) {
                serverConfig = CommentedFileConfig.builder(serverConfigPath)
                        .sync()
                        .autosave()
                        .writingMode(WritingMode.REPLACE)
                        .build();
                serverConfig.load();
                setupConfig(serverConfig);
                loadConfigValues(serverConfig);
                lastServerModifiedTime = getFileModifiedTime(serverConfigPath);
                startFileWatcher(serverConfigPath, true);
            }
        }
    }

    private static void setupConfig(CommentedFileConfig config) {
        config.setComment("allowExplosiveCrafting",
                "Allow fletching table to craft explosive arrow");
        if (!config.contains("allowExplosiveCrafting")) {
            config.set("allowExplosiveCrafting", true);
        }

        config.setComment("tippedArrowCraftingAmount",
                "Amount of tipped arrow it can craft with 1 lingering potion, if json recipe for particular tipped arrow are defined, it will not be controlled by this config");
        if (!config.contains("tippedArrowCraftingAmount")) {
            config.set("tippedArrowCraftingAmount", 16);
        }

        config.setComment("explosiveArrowCraftingAmount",
                "Amount of explosive arrow it can craft with 1 explosive ingredient");
        if (!config.contains("explosiveArrowCraftingAmount")) {
            config.set("explosiveArrowCraftingAmount", 4);
        }
    }

    private static void loadConfigValues(CommentedFileConfig config) {
        allowExplosiveCrafting = config.getOrElse("allowExplosiveCrafting", true);

        int tippedAmount = config.getIntOrElse("tippedArrowCraftingAmount", 16);
        tippedArrowCraftingAmount = clamp(tippedAmount, 1, 64);

        int explosiveAmount = config.getIntOrElse("explosiveArrowCraftingAmount", 4);
        explosiveArrowCraftingAmount = clamp(explosiveAmount, 1, 64);
    }

    private static void startFileWatcher(Path configPath, boolean isServerConfig) {
        if (fileWatcher == null) {
            fileWatcher = Executors.newScheduledThreadPool(1, r -> {
                Thread thread = new Thread(r, "Config File Watcher");
                thread.setDaemon(true);
                return thread;
            });
        }

        fileWatcher.scheduleAtFixedRate(() -> {
            try {
                long currentModifiedTime = getFileModifiedTime(configPath);
                long lastTime = isServerConfig ? lastServerModifiedTime : lastModifiedTime;

                if (currentModifiedTime > lastTime) {
                    CommentedFileConfig targetConfig = isServerConfig ? serverConfig : config;
                    if (targetConfig != null) {
                        targetConfig.load();
                        loadConfigValues(targetConfig);

                        if (isServerConfig) {
                            lastServerModifiedTime = currentModifiedTime;
                        } else {
                            lastModifiedTime = currentModifiedTime;
                        }

                        System.out.println("[FletchingRecipe] Config file reloaded: " + configPath.getFileName());
                    }
                }
            } catch (Exception e) {
                System.err.println("[FletchingRecipe] Error watching config file: " + e.getMessage());
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

    private static long getFileModifiedTime(Path path) {
        try {
            return path.toFile().lastModified();
        } catch (Exception e) {
            return 0;
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static void close() {
        if (fileWatcher != null) {
            fileWatcher.shutdown();
        }
        if (config != null) {
            config.close();
        }
        if (serverConfig != null) {
            serverConfig.close();
        }
    }

    public static boolean allowExplosiveCrafting() {
        if (serverConfig != null) {
            return serverConfig.getOrElse("allowExplosiveCrafting", allowExplosiveCrafting);
        }
        return allowExplosiveCrafting;
    }

    public static int tippedArrowCraftingAmount() {
        if (serverConfig != null) {
            return clamp(serverConfig.getIntOrElse("tippedArrowCraftingAmount", tippedArrowCraftingAmount), 1, 64);
        }
        return tippedArrowCraftingAmount;
    }

    public static int explosiveArrowCraftingAmount() {
        if (serverConfig != null) {
            return clamp(serverConfig.getIntOrElse("explosiveArrowCraftingAmount", explosiveArrowCraftingAmount), 1, 64);
        }
        return explosiveArrowCraftingAmount;
    }
}