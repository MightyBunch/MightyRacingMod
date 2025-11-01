package com.mightyracing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.scoreboard.*;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.world.ServerWorld;
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
    public static Entity getEntity(ServerWorld world, double xPos, double yPos, double zPos, float scalex, float scaley, float rotationV, float rotationH, int color){
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", "minecraft:text_display");
        NbtCompound transformation = new NbtCompound();
        transformation.put("left_rotation", floatList(0f, 0f, 0f, 1f));
        transformation.put("right_rotation", floatList(0f, 0f, 0f, 1f));
        transformation.put("scale", floatList(scalex * 40f / 6f, scaley * 40f / 10f, 1f));
        transformation.put("translation", floatList(scalex * 5f / 12f, 0f, 0f));
        nbt.put("transformation", transformation);
        nbt.putString("alignment", "center");
        nbt.put("Rotation", floatList(rotationV, rotationH));
        nbt.putInt("background", color);
        nbt.putBoolean("see_through", true);
        nbt.putString("text","{\"text\":\"\u00A0\"}");
                return EntityType.loadEntityWithPassengers(nbt, world, SpawnReason.COMMAND, lentity -> {
            lentity.refreshPositionAfterTeleport(xPos, yPos, zPos);
            return lentity;
        });
    }
    private static NbtList floatList(float... values) {
        NbtList list = new NbtList();
        for (float value : values) {
            list.add(NbtFloat.of(value));
        }
        return list;
    }
}
