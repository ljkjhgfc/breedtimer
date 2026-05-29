package com.breedtimer.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BreedTimerClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("breedtimer");
    public static KeyBinding openConfigKey;

    @Override
    public void onInitializeClient() {
        // Register config screen keybind (default: K)
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.breedtimer.openconfig",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.breedtimer"
        ));

        // Register HUD renderer
        HudRenderCallback.EVENT.register(BreedTimerHud::render);

        // Register tick event to check keybind
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new BreedTimerConfigScreen(null));
                }
            }
        });

        LOGGER.info("BreedTimer loaded!");
    }
}
