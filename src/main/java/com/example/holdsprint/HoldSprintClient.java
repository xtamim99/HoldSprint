package com.example.holdsprint;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/**
 * Makes sprint behave as hold-to-sprint: sprint while the sprint key is held,
 * and stop sprinting when it is released. Client-side only; no mixins required.
 */
public final class HoldSprintClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(HoldSprintClient::onEndClientTick);
    }

    private static void onEndClientTick(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }

        // Don't keep sprinting while a menu/chat screen is open.
        boolean sprintHeld = client.options.keySprint.isDown();
        player.setSprinting(sprintHeld);
    }
}
