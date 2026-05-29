package com.breedtimer.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BreedTimerHud {

    // Drag state
    private static String draggingMob = null;
    private static int dragOffsetX = 0;
    private static int dragOffsetY = 0;
    private static boolean mouseWasDown = false;

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getDebugHud().shouldShowDebugHud()) return;
        if (client.currentScreen != null && !(client.currentScreen instanceof BreedTimerConfigScreen)) return;

        Map<String, Long> timers = BreedTimerData.getActiveTimers();
        if (timers.isEmpty()) return;

        float scale = BreedTimerConfig.scale;
        List<String> mobNames = new ArrayList<>(timers.keySet());

        // Handle mouse dragging
        boolean mouseDown = client.mouse.wasLeftButtonClicked();
        double mouseX = client.mouse.getX() / client.getWindow().getScaleFactor();
        double mouseY = client.mouse.getY() / client.getWindow().getScaleFactor();

        int widgetW = (int)(120 * scale);
        int widgetH = (int)(18 * scale);

        if (mouseDown && !mouseWasDown) {
            // Check if clicking on any widget
            for (int i = 0; i < mobNames.size(); i++) {
                String mob = mobNames.get(i);
                int[] pos = BreedTimerConfig.getPositionFor(mob, i, client.getWindow().getScaledWidth());
                int wx = pos[0], wy = pos[1];
                if (mouseX >= wx && mouseX <= wx + widgetW && mouseY >= wy && mouseY <= wy + widgetH) {
                    draggingMob = mob;
                    dragOffsetX = (int)(mouseX - wx);
                    dragOffsetY = (int)(mouseY - wy);
                    break;
                }
            }
        }

        if (mouseDown && draggingMob != null) {
            int newX = (int)(mouseX - dragOffsetX);
            int newY = (int)(mouseY - dragOffsetY);
            // Clamp to screen
            newX = Math.max(0, Math.min(newX, client.getWindow().getScaledWidth() - widgetW));
            newY = Math.max(0, Math.min(newY, client.getWindow().getScaledHeight() - widgetH));
            BreedTimerConfig.setPositionFor(draggingMob, newX, newY);
        }

        if (!mouseDown) {
            if (draggingMob != null) {
                BreedTimerConfig.save();
            }
            draggingMob = null;
        }
        mouseWasDown = mouseDown;

        // Draw each widget
        context.getMatrices().push();
        for (int i = 0; i < mobNames.size(); i++) {
            String mob = mobNames.get(i);
            int remaining = BreedTimerData.getRemainingSeconds(mob);
            if (remaining <= 0) continue;

            int[] pos = BreedTimerConfig.getPositionFor(mob, i, client.getWindow().getScaledWidth());
            int wx = pos[0];
            int wy = pos[1];

            // Background (semi-transparent dark)
            context.fill(wx, wy, wx + widgetW, wy + widgetH, 0xAA000000);

            // Border - green when almost done, yellow mid, white normal
            int borderColor;
            if (remaining <= 30) borderColor = 0xFF55FF55;       // green
            else if (remaining <= 120) borderColor = 0xFFFFFF55; // yellow
            else borderColor = 0xFFAAAAAA;                        // gray

            // Draw border lines
            context.fill(wx, wy, wx + widgetW, wy + 1, borderColor);
            context.fill(wx, wy + widgetH - 1, wx + widgetW, wy + widgetH, borderColor);
            context.fill(wx, wy, wx + 1, wy + widgetH, borderColor);
            context.fill(wx + widgetW - 1, wy, wx + widgetW, wy + widgetH, borderColor);

            // Progress bar fill
            long endTick = BreedTimerData.getActiveTimers().get(mob);
            float progress = 1f - (float)(endTick - BreedTimerData.getCurrentTick()) / BreedTimerData.BREED_COOLDOWN_TICKS;
            int barWidth = (int)((widgetW - 2) * progress);
            context.fill(wx + 1, wy + widgetH - 3, wx + 1 + barWidth, wy + widgetH - 1, 0xAA4488FF);

            // Text: "Cow: 4:58"
            int mins = remaining / 60;
            int secs = remaining % 60;
            String label = String.format("%s: %d:%02d", mob, mins, secs);

            context.getMatrices().push();
            context.getMatrices().scale(scale, scale, 1f);
            int textX = (int)((wx + 3) / scale);
            int textY = (int)((wy + 5) / scale);
            context.drawTextWithShadow(client.textRenderer, label, textX, textY, 0xFFFFFFFF);
            context.getMatrices().pop();
        }
        context.getMatrices().pop();
    }
}
