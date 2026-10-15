
package com.example.holdsprint;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.options.controls.ControlsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.player.LocalPlayer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class HoldSprintClient implements ClientModInitializer {
    private static final Path CONFIG_FILE =
            FabricLoader.getInstance().getConfigDir()
                    .resolve("hold_sprint.properties");

    // Keep the existing behavior enabled by default.
    private static boolean trueHoldEnabled = loadEnabled();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(
                HoldSprintClient::onEndClientTick
        );

        ScreenEvents.AFTER_INIT.register(
                (client, screen, width, height) -> {
                    if (screen instanceof ControlsScreen) {
                        Button button = Button.builder(
                                getButtonText(),
                                clicked -> {
                                    trueHoldEnabled = !trueHoldEnabled;
                                    saveEnabled();
                                    clicked.setMessage(getButtonText());
                                }
                        ).bounds(
                                width / 2 - 100,
                                height - 52,
                                200,
                                20
                        ).build();

                        Screens.getWidgets(screen).add(button);
                    }
                }
        );
    }

    private static Component getButtonText() {
        return Component.literal(
                "True Hold: " + (trueHoldEnabled ? "ON" : "OFF")
        );
    }

    private static void onEndClientTick(Minecraft client) {
        if (!trueHoldEnabled) {
            return;
        }

        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }

        boolean sprintHeld = client.options.keySprint.isDown();
        player.setSprinting(sprintHeld);
    }

    private static boolean loadEnabled() {
        Properties properties = new Properties();

        if (Files.exists(CONFIG_FILE)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_FILE)) {
                properties.load(reader);
                return Boolean.parseBoolean(
                        properties.getProperty("enabled", "true")
                );
            } catch (IOException ignored) {
                // Use the default if the config cannot be read.
            }
        }

        return true;
    }

    private static void saveEnabled() {
        Properties properties = new Properties();
        properties.setProperty(
                "enabled",
                Boolean.toString(trueHoldEnabled)
        );

        try {
            Files.createDirectories(CONFIG_FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_FILE)) {
                properties.store(writer, "HoldSprint settings");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
