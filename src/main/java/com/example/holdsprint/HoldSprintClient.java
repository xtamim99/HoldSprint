
package com.example.holdsprint;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.options.controls.ControlsScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@Environment(EnvType.CLIENT)
public final class HoldSprintClient implements ClientModInitializer {

    private static final Path CONFIG_FILE =
            FabricLoader.getInstance()
                    .getConfigDir()
                    .resolve("hold_sprint.properties");

    private static boolean trueHoldEnabled = loadEnabled();

    @Override
    public void onInitializeClient() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!trueHoldEnabled) {
                return;
            }

            LocalPlayer player = client.player;
            if (player == null) {
                return;
            }

            InputConstants.Key sprintKey =
                    KeyMappingHelper.getBoundKeyOf(
                            client.options.keySprint
                    );

            boolean physicallyHeld =
                    sprintKey.getType() == InputConstants.Type.KEYSYM
                            && InputConstants.isKeyDown(
                                    client.getWindow(),
                                    sprintKey.getValue()
                            );

            player.setSprinting(physicallyHeld);
        });

        ScreenEvents.AFTER_INIT.register(
                (client, screen, width, height) -> {
                    if (!(screen instanceof ControlsScreen)) {
                        return;
                    }

                    Button button = Button.builder(
                            buttonText(),
                            clicked -> {
                                trueHoldEnabled = !trueHoldEnabled;
                                saveEnabled();
                                clicked.setMessage(buttonText());
                            }
                    ).bounds(
                            width / 2 - 100,
                            height - 76,
                            200,
                            20
                    ).build();

                    Screens.getWidgets(screen).add(button);
                }
        );
    }

    private static Component buttonText() {
        return Component.literal(
                "True Hold: " +
                (trueHoldEnabled ? "ON" : "OFF")
        );
    }

    private static boolean loadEnabled() {
        Properties properties = new Properties();

        try {
            if (Files.exists(CONFIG_FILE)) {
                try (var reader =
                             Files.newBufferedReader(CONFIG_FILE)) {
                    properties.load(reader);
                }

                return Boolean.parseBoolean(
                        properties.getProperty("enabled", "true")
                );
            }
        } catch (IOException exception) {
            exception.printStackTrace();
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

            try (var writer =
                         Files.newBufferedWriter(CONFIG_FILE)) {
                properties.store(
                        writer,
                        "HoldSprint settings"
                );
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
