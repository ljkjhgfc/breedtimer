package com.breedtimer.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class BreedTimerConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("breedtimer.json");

    // Default HUD position (top-left corner start)
    public static int defaultX = 4;
    public static int defaultY = 4;
    public static float scale = 1.0f;

    // Per-mob widget positions: mob name -> [x, y]
    public static Map<String, int[]> mobPositions = new HashMap<>();

    // ---- Serialization container ----
    static class ConfigData {
        int defaultX = 4;
        int defaultY = 4;
        float scale = 1.0f;
        Map<String, int[]> mobPositions = new HashMap<>();
    }

    public static void load() {
        if (!CONFIG_PATH.toFile().exists()) {
            save();
            return;
        }
        try (Reader r = new FileReader(CONFIG_PATH.toFile())) {
            ConfigData data = GSON.fromJson(r, ConfigData.class);
            if (data != null) {
                defaultX = data.defaultX;
                defaultY = data.defaultY;
                scale = data.scale;
                mobPositions = data.mobPositions != null ? data.mobPositions : new HashMap<>();
            }
        } catch (IOException e) {
            BreedTimerClient.LOGGER.error("Failed to load BreedTimer config", e);
        }
    }

    public static void save() {
        ConfigData data = new ConfigData();
        data.defaultX = defaultX;
        data.defaultY = defaultY;
        data.scale = scale;
        data.mobPositions = mobPositions;
        try (Writer w = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(data, w);
        } catch (IOException e) {
            BreedTimerClient.LOGGER.error("Failed to save BreedTimer config", e);
        }
    }

    public static int[] getPositionFor(String mobName, int index, int screenWidth) {
        if (mobPositions.containsKey(mobName)) {
            return mobPositions.get(mobName);
        }
        // Stack them vertically at default position by default
        int widgetHeight = (int)(20 * scale);
        return new int[]{defaultX, defaultY + index * (widgetHeight + 2)};
    }

    public static void setPositionFor(String mobName, int x, int y) {
        mobPositions.put(mobName, new int[]{x, y});
    }
}
