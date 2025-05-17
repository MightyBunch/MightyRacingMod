package com.mightyracing;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.Text;

public class MightyDifferences {
    private static final int SIDEBAR_SLOT = 1;
    public static void setRaceboardSidebar(Scoreboard scoreboard){
        ScoreboardObjective raceboard = scoreboard.getNullableObjective("MRM_raceboard");
        scoreboard.setObjectiveSlot(SIDEBAR_SLOT,raceboard);
    }
    public static void resetSlotSidebar(Scoreboard scoreboard){
        scoreboard.setObjectiveSlot(SIDEBAR_SLOT,null);
    }
    public static void addObjectiveRaceboard(Scoreboard scoreboard){
        scoreboard.addObjective("MRM_raceboard", ScoreboardCriterion.DUMMY, Text.literal("MRM_raceboard"), ScoreboardCriterion.RenderType.INTEGER);
    }
    public static void raceboardResetPlayer(String raceboardname, Scoreboard scoreboard){
        ScoreboardObjective raceboard = scoreboard.getNullableObjective("MRM_raceboard");
        scoreboard.resetPlayerScore(raceboardname,raceboard);
    }
    public static int raceboardGetPlayer(String raceboardname, Scoreboard scoreboard){
        ScoreboardObjective raceboard = scoreboard.getNullableObjective("MRM_raceboard");
        return scoreboard.getPlayerScore(raceboardname,raceboard).getScore();
    }
    public static void raceboardSetPlayer(String raceboardname, Scoreboard scoreboard, int score){
        ScoreboardObjective raceboard = scoreboard.getNullableObjective("MRM_raceboard");
        ScoreboardPlayerScore raceboardentry = scoreboard.getPlayerScore(raceboardname,raceboard);
        raceboardentry.setScore(score);
    }
}
