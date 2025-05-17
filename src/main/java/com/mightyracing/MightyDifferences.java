package com.mightyracing;

import net.minecraft.scoreboard.*;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.Text;

public class MightyDifferences {
    private static final ScoreboardDisplaySlot SIDEBAR_SLOT = ScoreboardDisplaySlot.SIDEBAR;
    public static void setRaceboardSidebar(Scoreboard scoreboard){
        ScoreboardObjective raceboard = scoreboard.getNullableObjective("MRM_raceboard");
        scoreboard.setObjectiveSlot(SIDEBAR_SLOT,raceboard);
    }
    public static void resetSlotSidebar(Scoreboard scoreboard){
        scoreboard.setObjectiveSlot(SIDEBAR_SLOT,null);
    }
    public static void addObjectiveRaceboard(Scoreboard scoreboard){
        scoreboard.addObjective("MRM_raceboard", ScoreboardCriterion.DUMMY, Text.literal("MRM_raceboard"), ScoreboardCriterion.RenderType.INTEGER,false,(NumberFormat)null);
    }
    public static void raceboardResetPlayer(String raceboardname, Scoreboard scoreboard){
        ScoreboardObjective raceboard = scoreboard.getNullableObjective("MRM_raceboard");
        ScoreHolder scoreHolder = ScoreHolder.fromName(raceboardname);
        scoreboard.removeScore(scoreHolder,raceboard);
    }
    public static int raceboardGetPlayer(String raceboardname, Scoreboard scoreboard){
        ScoreboardObjective raceboard = scoreboard.getNullableObjective("MRM_raceboard");
        ScoreHolder scoreHolder = ScoreHolder.fromName(raceboardname);
        return scoreboard.getOrCreateScore(scoreHolder,raceboard).getScore();
    }
    public static void raceboardSetPlayer(String raceboardname, Scoreboard scoreboard, int score){
        ScoreboardObjective raceboard = scoreboard.getNullableObjective("MRM_raceboard");
        ScoreHolder scoreHolder = ScoreHolder.fromName(raceboardname);
        ScoreAccess raceboardentry = scoreboard.getOrCreateScore(scoreHolder,raceboard);
        raceboardentry.setScore(score);
    }
}
