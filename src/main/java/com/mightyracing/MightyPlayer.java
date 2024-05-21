package com.mightyracing;

import net.minecraft.server.network.ServerPlayerEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MightyPlayer {
   public ServerPlayerEntity player;
   public LocalDateTime starttime;
   public List<MightyTime> besttimes;
   public Map<Integer,MightyTime> currenttimes;
   public int sector;
   public int lap;
   public static Map<String, MightyPlayer> list = new HashMap<>();
   public String raceboardname;
   public String interval;
   public String cuttedname;
   public String namecolor;
   public int stops;

   public MightyPlayer(ServerPlayerEntity player) {
      this.player = player;
      this.sector = 0;
      this.lap = -1;
      this.starttime = null;
      this.raceboardname = null;
      this.interval = "§c+0:00:00";
      this.besttimes = new ArrayList<>();
      this.currenttimes = new HashMap<>();
      this.cuttedname = null;
      this.namecolor = "§f";
      this.stops = 0;
      MightyPlayer.list.put(player.getEntityName(),this);
   }
   public static void allToZero(){
      for(MightyPlayer mightyplayer : MightyPlayer.list.values()) {
         mightyplayer.sector = 0;
         mightyplayer.lap = -1;
         mightyplayer.starttime = null;
         mightyplayer.currenttimes.clear();
         mightyplayer.besttimes.clear();
         mightyplayer.interval = "§c+0:00:00";
         mightyplayer.namecolor = "§f";
         mightyplayer.stops = 0;
      }
   }
}
