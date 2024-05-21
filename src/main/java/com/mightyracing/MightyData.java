package com.mightyracing;

import com.mightyracing.util.IEntityDataSaver;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

public class MightyData {
    public static void putTime(IEntityDataSaver player, String track, List<Integer> times){
        NbtCompound nbt = player.getPersistentData();
        nbt.putIntArray(track, times);
    }
    public static int[] getTime(IEntityDataSaver player, String track){
        NbtCompound nbt = player.getPersistentData();
        return nbt.getIntArray(track);
    }
    public static void putName(IEntityDataSaver player, String cuttedname){
        NbtCompound nbt = player.getPersistentData();
        nbt.putString("name", cuttedname);
    }
    public static String getName(IEntityDataSaver player){
        NbtCompound nbt = player.getPersistentData();
        return nbt.getString("name");
    }
}
