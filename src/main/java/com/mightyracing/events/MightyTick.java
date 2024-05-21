package com.mightyracing.events;

import com.mightyracing.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;


public class MightyTick implements ServerTickEvents.EndTick{
    public static final int OFFLINE = 0;
    public static final int PRACTICE = 1;
    public static final int QUALI = 2;
    public static final int RACING = 3;

    public static final int QSTARTING = 0;
    public static final int QDURING = 1;
    public static final int QENDING = 2;
    public static final int QENDED = 3;
    @Override
    public void onEndTick(MinecraftServer server) {
        switch (MightyRacingCommand.racingstatus) {
            case PRACTICE -> {
                sendActionTime();
            }
            case QUALI -> {
                if (MightyRacingCommand.qualistage == QDURING) {
                    LocalDateTime now = LocalDateTime.now();
                    Duration delta = Duration.between(now, MightyRacingCommand.qualiend);
                    MightyQualiTime mightydelta = new MightyQualiTime(delta);
                    String name = MightyRacingCommand.QUALINAME + " " + mightydelta.getString();
                    if (!name.equals(MightyRacingCommand.raceboarddisplayname)) {
                        if (mightydelta.m == 0 && mightydelta.s == 0) {
                            MightyRacingCommand.qualistage = QENDING;
                            MightyRacingCommand.checkQualiEnd();
                            for (Map.Entry<String, MightyPlayer> listentry : MightyPlayer.list.entrySet()){
                                MightyPlayer mightyplayer = listentry.getValue();
                                if (mightyplayer.starttime == null){
                                    MightyRacingCommand.raceboardPutOnlyNamecolor(server.getScoreboard(), listentry.getKey(), MightyRacingCommand.CLGRAY);
                                }
                            }
                        }
                        MightyRacingCommand.raceboardDisplay(server.getScoreboard(), name);
                    }
                }
                sendActionTime();
            }
            case RACING -> {
                sendActionTime();
            }
        }
    }
    private static void sendActionTime(){
        LocalDateTime now = null;
        for (MightyPlayer mightyplayer : MightyPlayer.list.values()) {
            String currenttime;
            if (mightyplayer.starttime != null) {
                LocalDateTime start = mightyplayer.starttime;
                if (now == null) {
                    now = LocalDateTime.now();
                }
                Duration delta = Duration.between(start, now);
                currenttime = (new MightyTime(delta).getString());
            } else {
                currenttime = "0:00:00";
            }
            String actionbar = currenttime + " " + mightyplayer.interval;
            mightyplayer.player.sendMessage(Text.literal(actionbar), true);
        }
    }
}