package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ExampleModClient implements ClientModInitializer {
    private static KeyBinding openMusicScreenKey;

    @Override
    public void onInitializeClient() {
        openMusicScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.musicmod.open",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_M, 
            "category.musicmod.title"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client == null) return;
            while (openMusicScreenKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new PremiumMusicScreen());
                }
            }
        });
    }
}
