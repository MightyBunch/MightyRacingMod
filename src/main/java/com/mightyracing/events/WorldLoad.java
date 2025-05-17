package com.mightyracing.events;

import com.mightyracing.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;

public class WorldLoad implements ServerLifecycleEvents.ServerStarted{
    public static final int OFFLINE = 0;
    @Override
    public void onServerStarted(MinecraftServer server) {
        if (MightyRacingCommand.racingstatus == OFFLINE) {
            Scoreboard scoreboard = server.getScoreboard();
            ScoreboardObjective raceboard = scoreboard.getNullableObjective("MRM_raceboard");
            if (raceboard != null) {
                scoreboard.removeObjective(raceboard);
            }
            MightyDifferences.addObjectiveRaceboard(scoreboard);
        }
    }
}
