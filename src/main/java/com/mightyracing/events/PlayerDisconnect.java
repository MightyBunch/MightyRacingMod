package com.mightyracing.events;

import com.mightyracing.MightyPlayer;
import com.mightyracing.MightyRacingCommand;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class PlayerDisconnect implements ServerPlayConnectionEvents.Disconnect{

    @Override
    public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        String name = handler.player.getGameProfile().getName();
        if (MightyPlayer.list.containsKey(name)) {
            MightyRacingCommand.raceboardRemoveSort(server.getScoreboard(), name);
            MightyPlayer.list.remove(name);
            MightyRacingCommand.checkQualiEnd(server);
            MightyRacingCommand.checkRaceEnd(server);
        }
    }
}
