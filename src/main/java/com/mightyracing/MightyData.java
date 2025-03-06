package com.mightyracing;

import com.mightyracing.util.IEntityDataSaver;
import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MightyData {
    public static void putTime(IEntityDataSaver player, String track, List<Integer> times){
        NbtCompound nbt = player.getPersistentData();
        nbt.putIntArray(track, times);
    }
    public static int[] getTime(IEntityDataSaver player, String track){
        NbtCompound nbt = player.getPersistentData();
        return nbt.getIntArray(track);
    }
    public static void removeTime(IEntityDataSaver player, String track){
        NbtCompound nbt = player.getPersistentData();
        nbt.remove(track);
    }
    public static void putName(IEntityDataSaver player, String cuttedname){
        NbtCompound nbt = player.getPersistentData();
        nbt.putString("name", cuttedname);
    }
    public static String getName(IEntityDataSaver player){
        NbtCompound nbt = player.getPersistentData();
        return nbt.getString("name");
    }
    public static void putStats(IEntityDataSaver player, Map<String, Integer> stats){
        NbtCompound nbt = player.getPersistentData();
        NbtCompound statsnbt = nbt.getCompound("stats");
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            statsnbt.putInt(entry.getKey(), entry.getValue());
        }
        nbt.put("stats",statsnbt);
    }
    public static Map<String, Integer> getStats(IEntityDataSaver player){
        NbtCompound nbt = player.getPersistentData();
        NbtCompound statsnbt = nbt.getCompound("stats");
        Set<String> keys = statsnbt.getKeys();
        Map<String, Integer> result = new HashMap<>();
        for (String key : keys) {
            result.put(key,statsnbt.getInt(key));
        }
        return result;
    }
}
