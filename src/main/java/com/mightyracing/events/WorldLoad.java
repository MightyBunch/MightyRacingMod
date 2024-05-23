package com.mightyracing.events;

import com.mightyracing.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

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
            scoreboard.addObjective("MRM_raceboard", ScoreboardCriterion.DUMMY, Text.literal("MRM_raceboard"), ScoreboardCriterion.RenderType.INTEGER);
        }
    }
}
