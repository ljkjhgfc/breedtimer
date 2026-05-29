package com.breedtimer.client;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tracks active breed cooldown timers.
 * Key: mob name (e.g. "Cow"), Value: game tick timestamp when cooldown expires
 */
public class BreedTimerData {

    // In-game breed cooldown is 5 minutes = 6000 ticks
    public static final int BREED_COOLDOWN_TICKS = 6000;

    // Map of mob display name -> tick time when cooldown ends
    private static final Map<String, Long> activeTimers = new LinkedHashMap<>();

    // Current client world tick count (updated each tick)
    private static long currentTick = 0;

    public static void tick() {
        currentTick++;
        // Remove expired timers
        activeTimers.entrySet().removeIf(e -> e.getValue() <= currentTick);
    }

    public static void addTimer(String mobName) {
        activeTimers.put(mobName, currentTick + BREED_COOLDOWN_TICKS);
    }

    public static Map<String, Long> getActiveTimers() {
        return activeTimers;
    }

    public static long getCurrentTick() {
        return currentTick;
    }

    /**
     * Returns remaining seconds for a mob's cooldown.
     */
    public static int getRemainingSeconds(String mobName) {
        if (!activeTimers.containsKey(mobName)) return 0;
        long remaining = activeTimers.get(mobName) - currentTick;
        return (int) Math.ceil(remaining / 20.0);
    }

    public static void reset() {
        activeTimers.clear();
        currentTick = 0;
    }
}
