package com.mightyracing.events;

import com.mightyracing.MightyPlayer;
import com.mightyracing.MightyRacingCommand;
import com.mightyracing.MightyRacingMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class PlayerDisconnect implements ServerPlayConnectionEvents.Disconnect{

    @Override
    public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        String name = handler.player.getGameProfile().getName();
        MightyRacingCommand.raceboardRemoveSort(server.getScoreboard(),name);
        MightyPlayer.list.remove(name);
        MightyRacingMod.LOGGER.info("Player " + name + " was removed from the racing system!");
    }
}
