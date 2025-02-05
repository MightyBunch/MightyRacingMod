package com.mightyracing.events;

import com.mightyracing.*;
import com.mightyracing.config.MightyConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static com.mightyracing.MightyText.*;
import static com.mightyracing.config.MightyConfig.*;
import static java.lang.Math.round;

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
                    String name = I18n.translate("shortcut.quali") + " " + mightydelta.getString();
                    if (!name.equals(MightyRacingCommand.raceboarddisplayname)) {
                        if (mightydelta.m == 0 && mightydelta.s == 0) {
                            MightyRacingCommand.qualistage = QENDING;
                            MightyRacingCommand.checkQualiEnd(server);
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
                durability();
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
            String dur = "";
            if (MightyRacingCommand.maxdurability != 0 && MightyRacingCommand.racingstatus == RACING ){
                int percent = (mightyplayer.durability <= 0 ? 0 : (1 + (int) round((mightyplayer.durability / MightyRacingCommand.maxdurability) * 99)));
                String col = percent <= 10 ? MightyRacingCommand.CRED : MightyRacingCommand.CWHITE;
                dur = MightyRacingCommand.CGRAY + "  |  " + col + percent + "%";
            }
            String actionbar = currenttime + " " + mightyplayer.interval + dur;
            mightyplayer.player.sendMessageToClient(Text.literal(actionbar), true);
        }
    }
    private static void durability(){
        if (MightyRacingCommand.maxdurability == 0){
            return;
        }
        Set<String> blockSet = new HashSet<>(Arrays.asList(MightyConfig.getString(SPECIAL_BLOCKS).replace(" ","").split(",")));
        for (MightyPlayer mightyplayer : MightyPlayer.list.values()){
            if (mightyplayer.starttime == null){
                continue;
            }

            BlockPos pos;
            if (mightyplayer.player.hasVehicle()){
                pos = Objects.requireNonNull(mightyplayer.player.getVehicle()).getBlockPos().down();
            }else{
                pos = mightyplayer.player.getBlockPos().down();
            }
            World world = mightyplayer.player.getWorld();
            Block block = world.getBlockState(pos).getBlock();
            boolean on_block = blockSet.contains(Registries.BLOCK.getId(block).toString());

            Vec3d newPos = new Vec3d(
                    mightyplayer.player.getX(),
                    mightyplayer.player.getY(),
                    mightyplayer.player.getZ()
            );
            float speed = 0;
            Vec3d oldPos = mightyplayer.oldPos;
            if (mightyplayer.oldPos != null){
                speed = (float) Math.sqrt(Math.pow(newPos.x - oldPos.x, 2) + Math.pow(newPos.y - oldPos.y, 2) + Math.pow(newPos.z - oldPos.z, 2));
            }
            mightyplayer.oldPos = newPos;
            
            if (on_block){
                mightyplayer.durability -= Math.pow(speed * MightyConfig.getFloat(SPECIAL_MODIF),MightyConfig.getFloat(SPECIAL_POWER));
            }else{
                mightyplayer.durability -= Math.pow(speed * 100 * MightyConfig.getFloat(DEFAULT_MODIF),MightyConfig.getFloat(DEFAULT_POWER));
            }
            if (mightyplayer.durability <= 0){
                mightyplayer.durability = 0;
                if (MightyConfig.getBoolean(DESTROY_VEHICLE) && mightyplayer.player.hasVehicle()) {
                    Entity vehicle = Objects.requireNonNull(mightyplayer.player.getVehicle());
                    vehicle.remove(Entity.RemovalReason.KILLED);
                    vehicle.emitGameEvent(GameEvent.ENTITY_DIE);
                }
                mightyplayer.starttime = null;
                MightyRacingCommand.raceboardPutOnlyNamecolor(mightyplayer.player.getScoreboard(),mightyplayer.player.getGameProfile().getName(),MightyRacingCommand.CRED);
                MightyRacingCommand.broadcastToDrivers(mightyplayer.player.server,Text.literal(String.format(info_durability_zero,mightyplayer.cuttedname)));
                mightyplayer.finished = true;
                MightyRacingCommand.checkRaceEnd(mightyplayer.player.server);
            }
        }
    }
}