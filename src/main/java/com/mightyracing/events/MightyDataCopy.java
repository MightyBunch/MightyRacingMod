package com.mightyracing.events;

import com.mightyracing.util.IEntityDataSaver;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

public class MightyDataCopy implements ServerPlayerEvents.CopyFrom{

    @Override
    public void copyFromPlayer(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        NbtCompound oldNbt = ((IEntityDataSaver) oldPlayer).getPersistentData();
        NbtCompound newNbt = ((IEntityDataSaver) newPlayer).getPersistentData();
        for (String key : oldNbt.getKeys()){
            if (Objects.equals(key, "name")){
                newNbt.putString("name",oldNbt.getString("name"));
            }else{
                newNbt.putIntArray(key,oldNbt.getIntArray(key));
            }
        }
    }
}
