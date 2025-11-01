package com.mightyracing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Math.*;

public class MightySelection {
    public static final Map<String,MightySelection> selections = new HashMap<>();
    private Vec3d pos1;
    private Vec3d pos2;
    private final ServerPlayerEntity player;
    private LocalDateTime expire;
    private ServerWorld world;
    private final List<EntityTrackerEntry> trackers;
    private static final int COLOR1 = 64 << 24 | 150 << 16 | 215 << 8 | 50;
    private static final int COLOR2 = 64 << 24 | 125 << 16 | 235 << 8 | 25;
    private static final int COLOR3 = 64 << 24 | 100 << 16 | 255 << 8;
    private static final int explus = 5;

    private MightySelection(String nickname, ServerPlayerEntity player, ServerWorld world){
        this.pos1 = null;
        this.pos2 = null;
        this.world = world;
        this.player = player;
        this.trackers = new ArrayList<>();
        selections.put(nickname,this);
    }

    public static void setPos(ServerPlayerEntity player, Vec3d vec3, int pos, ServerWorld world){
        String nickname = player.getGameProfile().getName();
        MightySelection selection = selections.get(nickname);
        if (selection == null){
            selection = new MightySelection(nickname, player, world);
        }
        selection.removeTrackers();
        if (world != selection.world){
            selection.pos1 = null;
            selection.pos2 = null;
            selection.world = world;
        }
        if (pos == 1){
            selection.pos1 = vec3;
        }else if (pos == 2){
            selection.pos2 = vec3;
        }
        double[] position = selection.getPos();
        if (position != null) {
            double x1 = position[0];
            double y1 = position[1];
            double z1 = position[2];
            double x2 = position[3];
            double y2 = position[4];
            double z2 = position[5];
            float xs = (float) position[6];
            float ys = (float) position[7];
            float zs = (float) position[8];

            selection.trackers.add(generateEntity(x2, y1, z1, world, xs, ys, 180f, 0f, player, COLOR1));
            selection.trackers.add(generateEntity(x1, y1, z2, world, xs, ys, 0f, 0f, player, COLOR1));
            selection.trackers.add(generateEntity(x1, y1, z1, world, xs, zs, 0f, 90f, player, COLOR2));
            selection.trackers.add(generateEntity(x1, y2, z2, world, xs, zs, 0f, -90f, player, COLOR2));
            selection.trackers.add(generateEntity(x1, y1, z1, world, zs, ys, 90f, 0f, player, COLOR3));
            selection.trackers.add(generateEntity(x2, y1, z2, world, zs, ys, -90f, 0f, player, COLOR3));

            selection.trackers.add(generateEntity(x1, y1, z1, world, xs, ys, 0f, 0f, player, COLOR1));
            selection.trackers.add(generateEntity(x2, y1, z2, world, xs, ys, 180f, 0f, player, COLOR1));
            selection.trackers.add(generateEntity(x1, y1, z2, world, xs, zs, 0f, -90f, player, COLOR2));
            selection.trackers.add(generateEntity(x1, y2, z1, world, xs, zs, 0f, 90f, player, COLOR2));
            selection.trackers.add(generateEntity(x1, y1, z2, world, zs, ys, -90f, 0f, player, COLOR3));
            selection.trackers.add(generateEntity(x2, y1, z1, world, zs, ys, 90f, 0f, player, COLOR3));
        }
        selection.updateExpiration();
    }

    private static NbtList floatList(float... values) {
        NbtList list = new NbtList();
        for (float value : values) {
            list.add(NbtFloat.of(value));
        }
        return list;
    }

    private static EntityTrackerEntry generateEntity(double xPos, double yPos, double zPos, ServerWorld world, float scalex, float scaley, float rotationV, float rotationH, ServerPlayerEntity player, int color){
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", "minecraft:text_display");
        NbtCompound transformation = new NbtCompound();
        transformation.put("left_rotation", floatList(0f, 0f, 0f, 1f));
        transformation.put("right_rotation", floatList(0f, 0f, 0f, 1f));
        transformation.put("scale", floatList(scalex * 40f, scaley * 40f, 1f));
        transformation.put("translation", floatList(0f, 0f, 0f));
        nbt.put("transformation", transformation);
        nbt.putString("alignment", "left");
        nbt.put("Rotation", floatList(rotationV, rotationH));
        nbt.putInt("background", color);
        nbt.putBoolean("see_through", true);
        nbt.putString("text","\u00A0");
        Entity entity = MightyDifferences.getEntity(nbt, world, xPos, yPos, zPos);
        EntityTrackerEntry tracker = null;
        if (entity != null) {
            tracker = new EntityTrackerEntry(world, entity, EntityType.TEXT_DISPLAY.getTrackTickInterval(), EntityType.TEXT_DISPLAY.alwaysUpdateVelocity(), packet -> player.networkHandler.sendPacket(packet));
            tracker.startTracking(player);
        }
        return tracker;
    }

    public static void checkExpire(){
        LocalDateTime now = LocalDateTime.now();
        Iterator<Map.Entry<String, MightySelection>> iter = selections.entrySet().iterator();
        while (iter.hasNext()) {
            MightySelection selection = iter.next().getValue();
            if (now.isAfter(selection.expire)) {
                selection.removeTrackers();
                iter.remove();
            }
        }
    }

    private void removeTrackers(){
        for(EntityTrackerEntry tracker : this.trackers){
            if (tracker != null) {
                tracker.stopTracking(this.player);
            }
        }
        this.trackers.clear();
    }

    public static void removeSelector(ServerPlayerEntity player){
        String nickname = player.getGameProfile().getName();
        MightySelection selection = selections.get(nickname);
        if (selection != null) {
            selection.removeTrackers();
            selections.remove(nickname);
        }
    }

    private double[] getPos(){
        if (this.pos1 == null && this.pos2 == null){
            return null;
        }
        Vec3d position1 = this.pos1 == null ? this.pos2 : this.pos1;
        Vec3d position2 = this.pos2 == null ? this.pos1 : this.pos2;
        double[] position = new double[9];
        position[0] = floor(min(position1.getX(), position2.getX()));
        position[1] = floor(min(position1.getY(), position2.getY()));
        position[2] = floor(min(position1.getZ(), position2.getZ()));
        position[3] = ceil(max(position1.getX(), position2.getX()));
        double pp = max(position1.getY(), position2.getY());
        position[4] = ceil(pp);
        if (position[4] == pp) {
            position[4] = round(position[4] + 1);
        }
        position[5] = ceil(max(position1.getZ(), position2.getZ()));
        position[6] = (float) (position[3] - position[0]);
        position[7] = (float) (position[4] - position[1]);
        position[8] = (float) (position[5] - position[2]);
        return position;
    }

    public static String getSelector(ServerPlayerEntity player){
        MightySelection selection = MightySelection.selections.get(player.getGameProfile().getName());
        if (selection == null){
            return "";
        }
        double[] position = selection.getPos();
        if (position == null){
            return "";
        }
        selection.updateExpiration();
        return "@a[x=" + position[0] + ",y=" + position[1] + ",z=" + position[2] + ",dx=" + (position[6]-1) + ",dy=" + (position[7]-1) + ",dz=" + (position[8]-1) + "]";
    }

    private void updateExpiration(){
        this.expire = LocalDateTime.now().plusMinutes(explus);
    }
}
