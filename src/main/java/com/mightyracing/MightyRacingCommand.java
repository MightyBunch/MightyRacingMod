package com.mightyracing;

import com.mightyracing.config.MightyConfig;
import com.mightyracing.util.IEntityDataSaver;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class MightyRacingCommand {
    //RACE STATUS
    public static final int OFFLINE = 0;
    public static final int PRACTICE = 1;
    public static final int QUALI = 2;
    public static final int RACING = 3;
    //QUALI STAGE
    public static final int QSTARTING = 0;
    public static final int QDURING = 1;
    public static final int QENDING = 2;
    public static final int QENDED = 3;
    //RACING STAGE
    public static final int RSTARTING = 0;
    public static final int RDURING = 1;
    public static final int RENDING = 2;
    public static final int RENDED = 3;
    //COLORS
    public static final String CPURPLE = "§d";
    public static final String CDPURPLE = "§5";
    public static final String CGREEN = "§a";
    public static final String CRED = "§c";
    public static final String CBOLD = "§l";
    public static final String CWHITE = "§f";
    public static final String CLGRAY = "§7";
    public static final String CGRAY = "§8";

    public static int qualitime = 0;
    public static String track = null;
    public static int qualistage = 0;
    public static int racestage = 0;
    public static int racelaps = 0;
    public static int racecurlap = 1;
    public static MightyPlayer fastest = null;
    public static int racingstatus = 0;
    public static LocalDateTime qualiend = null;
    public static int racestops = 0;
    public static String raceboarddisplayname = "MRM_raceboard";
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess ignoredCommandRegistryAccess, CommandManager.RegistrationEnvironment ignoredRegistrationEnvironment) {
        dispatcher.register(CommandManager.literal("mightyracing")
                .then(CommandManager.literal("track").requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                .then(CommandManager.literal("sector")
                                        .then(CommandManager.argument("number", IntegerArgumentType.integer(1, 99))
                                                .executes(context -> sector(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "number")))
                                        )
                                )
                                .then(CommandManager.literal("lap")
                                        .then(CommandManager.argument("number", IntegerArgumentType.integer(2, 100))
                                                .executes(context -> lap(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "number")))
                                        )
                                )
                                .then(CommandManager.literal("pitentry")
                                        .then(CommandManager.argument("number", IntegerArgumentType.integer(1, 99))
                                                .executes(context -> pitentry(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "number")))
                                        )
                                )
                                .then(CommandManager.literal("pitexit")
                                        .then(CommandManager.argument("number", IntegerArgumentType.integer(2, 100))
                                                .executes(context -> pitexit(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "number")))
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("system").requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("status")
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .then(CommandManager.literal("driver")
                                                .executes(context -> driver(context.getSource(), EntityArgumentType.getPlayers(context, "targets")))
                                        )
                                        .then(CommandManager.literal("normal")
                                                .executes(context -> normal(context.getSource(), EntityArgumentType.getPlayers(context, "targets")))
                                        )
                                )
                        )
                        .then(CommandManager.literal("racestatus")
                                .then(CommandManager.literal("offline")
                                        .executes(context -> racestatus(context.getSource(), OFFLINE,null, 0, 0,0))
                                )
                                .then(CommandManager.literal("practice")
                                        .then(CommandManager.argument("track", StringArgumentType.string())
                                                .executes(context -> racestatus(context.getSource(), PRACTICE, StringArgumentType.getString(context,"track"), 0, 0,0))
                                        )
                                )
                                .then(CommandManager.literal("quali")
                                        .then(CommandManager.argument("minutes", IntegerArgumentType.integer(1, 60))
                                                .executes(context -> racestatus(context.getSource(), QUALI,null,IntegerArgumentType.getInteger(context, "minutes"),0,0))
                                        )
                                )
                                .then(CommandManager.literal("racing")
                                        .then(CommandManager.argument("laps", IntegerArgumentType.integer(1, 99))
                                                .executes(context -> racestatus(context.getSource(), RACING,null,0,IntegerArgumentType.getInteger(context, "laps"),0))
                                                .then(CommandManager.argument("pitstops", IntegerArgumentType.integer(1, 10))
                                                        .executes(context -> racestatus(context.getSource(), RACING,null,0,IntegerArgumentType.getInteger(context, "laps"),IntegerArgumentType.getInteger(context, "pitstops")))
                                                )
                                        )
                                )
                        )
                        .then(CommandManager.literal("timereset")
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .then(CommandManager.argument("track", StringArgumentType.string())
                                                .executes(context -> timereset(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), StringArgumentType.getString(context,"track")))
                                        )
                                )
                        )
                        .then(CommandManager.literal("race")
                                .then(CommandManager.literal("start")
                                        .executes(context -> racestart(context.getSource()))
                                )
                                .then(CommandManager.literal("change")
                                        .then(CommandManager.literal("laps")
                                                .then(CommandManager.argument("laps", IntegerArgumentType.integer(1, 99))
                                                        .executes(context -> changelaps(context.getSource(),IntegerArgumentType.getInteger(context, "laps")))
                                                )
                                        )
                                        .then(CommandManager.literal("pitstops")
                                                .then(CommandManager.argument("pitstops", IntegerArgumentType.integer(0, 10))
                                                        .executes(context -> changestops(context.getSource(),IntegerArgumentType.getInteger(context, "pitstops")))
                                                )
                                        )
                                )
                        )
                        .then(CommandManager.literal("quali")
                                .then(CommandManager.literal("start")
                                        .executes(context -> qualistart(context.getSource()))
                                )
                                .then(CommandManager.literal("change")
                                        .then(CommandManager.argument("minutes", IntegerArgumentType.integer(1, 60))
                                                .executes(context -> changeminutes(context.getSource(),IntegerArgumentType.getInteger(context, "minutes")))
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("name")
                        .then(CommandManager.argument("name",StringArgumentType.string())
                                .executes(context -> setName(context.getSource(), StringArgumentType.getString(context,"name")))
                        )
                )
        );
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private static int sector(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int number){
        if (racingstatus == OFFLINE) {
            return 0;
        }
        int calls = 0;
        LocalDateTime now = null;
        for (ServerPlayerEntity player : targets) {
            String name = player.getGameProfile().getName();
            if (!MightyPlayer.list.containsKey(name) || racingstatus == OFFLINE) {
                continue;
            }
            MightyPlayer mightyplayer = MightyPlayer.list.get(name);
            if (mightyplayer.sector >= number || mightyplayer.sector + 1 + MightyConfig.getInteger("checkpoint_precision") < number) {
                continue;
            }
            LocalDateTime start = mightyplayer.starttime;
            if (now == null) {
                now = LocalDateTime.now();
            }
            Duration delta;
            MightyTime mightydelta;
            if (start != null) {
                mightyplayer.sector = number;
                delta = Duration.between(start, now);
                mightydelta = new MightyTime(delta);
                int prev=mightyplayer.currenttimes.size();
                mightyplayer.currenttimes.addAll(Collections.nCopies(number-prev-1,null));
                mightyplayer.currenttimes.add(number-1, mightydelta);
                if (mightyplayer.besttimes.size() > number && mightyplayer.besttimes.get(number) != null) {
                    mightyplayer.interval = MightyTime.interval(mightydelta, mightyplayer.besttimes.get(number));
                }
                if (racingstatus == RACING && (racestage == RDURING || racestage == RENDING)){
                    raceboardPutSort(source.getServer().getScoreboard(), name, mightyplayer.namecolor);
                }
            }
            calls+=1;
        }
        return calls;
    }
    private static int lap(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int number){
        if (racingstatus == OFFLINE) {
            return 0;
        }
        int calls = 0;
        LocalDateTime now = null;
        Scoreboard scoreboard = null;
        for (ServerPlayerEntity player : targets) {
            String name = player.getGameProfile().getName();
            if (!MightyPlayer.list.containsKey(name)) {
                continue;
            }
            MightyPlayer mightyplayer = MightyPlayer.list.get(name);
            LocalDateTime start = mightyplayer.starttime;
            if ((mightyplayer.sector >= number || mightyplayer.sector == 0 || mightyplayer.sector + 1 + MightyConfig.getInteger("checkpoint_precision") < number) && start != null) {
                continue;
            }
            if (now == null) {
                now = LocalDateTime.now();
            }
            if (scoreboard == null) {
                scoreboard = source.getServer().getScoreboard();
            }
            boolean fast = false;
            MightyTime mightydelta = null;
            mightyplayer.sector = 0;
            if (start != null) {
                Duration delta;
                delta = Duration.between(start, now);
                mightydelta = new MightyTime(delta);
                mightyplayer.currenttimes.add(0,mightydelta);
                if (!mightyplayer.besttimes.isEmpty()) {
                    mightyplayer.interval = MightyTime.interval(mightydelta, mightyplayer.besttimes.get(0));
                    fast = MightyTime.compare(mightydelta, mightyplayer.besttimes.get(0));
                }else {
                    fast=true;
                }
            }
            switch (racingstatus) {
                case PRACTICE -> {
                    if (start != null) {
                        if (fast) {
                            trackBestSet(track, name, mightyplayer.currenttimes);
                            player.sendMessageToClient(Text.translatable("info.time.personal",Text.literal(CGREEN + CBOLD + mightydelta.getString())),false);
                            raceboardPutSort(scoreboard, name, CWHITE);
                        } else {
                            player.sendMessageToClient(Text.translatable("info.time.bad",Text.literal(CRED + CBOLD + mightydelta.getString())),false);
                        }
                    }
                    mightyplayer.starttime = now;
                }
                case QUALI -> {
                    switch (qualistage){
                        case QDURING -> {
                            if (start != null) {
                                if (fast) {
                                    bestSet(name,mightyplayer.currenttimes);
                                    player.sendMessageToClient(Text.translatable("info.time.personal",Text.literal(CGREEN + CBOLD + mightydelta.getString())),false);
                                    raceboardPutSort(scoreboard, name, CWHITE);
                                } else {
                                    player.sendMessageToClient(Text.translatable("info.time.bad",Text.literal(CRED + CBOLD + mightydelta.getString())),false);
                                }
                            }
                            mightyplayer.starttime = now;
                        }
                        case QENDING -> {
                            if (start != null) {
                                if (fast) {
                                    bestSet(name,mightyplayer.currenttimes);
                                    player.sendMessageToClient(Text.translatable("info.time.personal",Text.literal(CGREEN + CBOLD + mightydelta.getString())),false);
                                    raceboardPutSort(scoreboard, name, CLGRAY);
                                } else {
                                    player.sendMessageToClient(Text.translatable("info.time.bad",Text.literal(CRED + CBOLD + mightydelta.getString())),false);
                                    raceboardPutOnlyNamecolor(scoreboard, name, CLGRAY);
                                }
                                mightyplayer.starttime = null;
                                checkQualiEnd(source.getServer());
                            }
                        }
                    }
                }
                case RACING -> {
                    switch (racestage){
                        case RDURING -> {
                            mightyplayer.lap += 1;
                            if (start != null) {
                                if (fast) {
                                    bestSet(name,mightyplayer.currenttimes);
                                    if (fastest != null && fastest != mightyplayer) {
                                        if (MightyTime.compare(mightydelta, fastest.besttimes.get(0))) {
                                            raceboardPutOnlyNamecolor(scoreboard, fastest.player.getGameProfile().getName(), CWHITE);
                                            fastest = mightyplayer;
                                            broadcastToDrivers(source.getServer(),Text.translatable("info.time.fastest",Text.literal(mightyplayer.cuttedname + " " + CPURPLE + CBOLD + mightydelta.getString())));
                                        } else {
                                            player.sendMessageToClient(Text.translatable("info.time.personal",Text.literal(CGREEN + CBOLD + mightydelta.getString())),false);
                                        }
                                    }else{
                                        broadcastToDrivers(source.getServer(),Text.translatable("info.time.fastest",Text.literal(mightyplayer.cuttedname + " " + CPURPLE + CBOLD + mightydelta.getString())));
                                        fastest = mightyplayer;
                                    }
                                } else {
                                    player.sendMessageToClient(Text.translatable("info.time.bad",Text.literal(CRED + CBOLD + mightydelta.getString())),false);
                                }
                                raceboardPutSort(scoreboard, name, (fastest == mightyplayer) ? CPURPLE : CWHITE);
                                if (mightyplayer.lap >= racelaps){
                                    if (MightyConfig.getBoolean("auto_finish")) {
                                        racestage = RENDING;
                                    }
                                    raceboardDisplay(scoreboard,I18n.translate("shortcut.racing") + CGRAY + "  " + racelaps + "/" + racelaps);
                                    player.sendMessageToClient(Text.translatable("info.race.finish"),false);
                                    raceboardPutOnlyNamecolor(scoreboard, name, (fastest == mightyplayer) ? CDPURPLE : CLGRAY);
                                    mightyplayer.starttime = null;
                                    checkRaceEnd(source.getServer());
                                }else{
                                    if (mightyplayer.lap + 1 > racecurlap) {
                                        racecurlap = mightyplayer.lap + 1;
                                        if (racecurlap == racelaps) {
                                            raceboardDisplay(scoreboard, I18n.translate("shortcut.racing") + CRED + "  " + racecurlap + "/" + racelaps);
                                        }else{
                                            raceboardDisplay(scoreboard, I18n.translate("shortcut.racing") + CWHITE + "  " + racecurlap + "/" + racelaps);
                                        }
                                    }
                                    mightyplayer.starttime = now;
                                }
                            }else {
                                mightyplayer.starttime = now;
                            }
                        }
                        case RENDING -> {
                            mightyplayer.lap+=1;
                            if (start != null) {
                                if (fast) {
                                    bestSet(name,mightyplayer.currenttimes);
                                    if (fastest != null && fastest != mightyplayer) {
                                        if (MightyTime.compare(mightydelta, fastest.besttimes.get(0))) {
                                            raceboardPutOnlyNamecolor(scoreboard, fastest.player.getGameProfile().getName(), (fastest.namecolor.equals(CDPURPLE)) ? CLGRAY : CWHITE);
                                            fastest = mightyplayer;
                                            broadcastToDrivers(source.getServer(),Text.translatable("info.time.fastest",Text.literal(mightyplayer.cuttedname + " " + CPURPLE + CBOLD + mightydelta.getString())));
                                        } else {
                                            player.sendMessageToClient(Text.translatable("info.time.personal",Text.literal(CGREEN + CBOLD + mightydelta.getString())),false);
                                        }
                                    }else{
                                        broadcastToDrivers(source.getServer(),Text.translatable("info.time.fastest",Text.literal(mightyplayer.cuttedname + " " + CPURPLE + CBOLD + mightydelta.getString())));
                                        fastest = mightyplayer;
                                    }
                                } else {
                                    player.sendMessageToClient(Text.translatable("info.time.bad",Text.literal(CRED + CBOLD + mightydelta.getString())),false);
                                }
                                raceboardPutSort(scoreboard, name, (fastest == mightyplayer) ? CDPURPLE : CLGRAY);
                                player.sendMessageToClient(Text.translatable("info.race.finish"),false);
                                mightyplayer.starttime = null;
                                checkRaceEnd(source.getServer());
                            }
                        }
                    }
                }
            }
            mightyplayer.currenttimes.clear();
            calls+=1;
        }
        return calls;
    }
    private static int pitentry(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int number){
        if (racingstatus==OFFLINE){
            return 0;
        }
        int calls = 0;
        Scoreboard scoreboard = null;
        for (ServerPlayerEntity player : targets) {
            String name = player.getGameProfile().getName();
            if (!MightyPlayer.list.containsKey(name)) {
                continue;
            }
            MightyPlayer mightyplayer = MightyPlayer.list.get(name);
            if (scoreboard == null) {
                scoreboard = source.getServer().getScoreboard();
            }
            switch (racingstatus) {
                case PRACTICE -> {
                    if (mightyplayer.sector == 0) {
                        continue;
                    }
                    mightyplayer.sector = 0;
                    mightyplayer.starttime = null;
                    mightyplayer.interval = CRED + "+0:00:00";
                }
                case QUALI -> {
                    if (mightyplayer.sector == 0) {
                        continue;
                    }
                    switch (qualistage){
                        case QDURING -> {
                            mightyplayer.sector = 0;
                            mightyplayer.starttime = null;
                            mightyplayer.interval = CRED + "+0:00:00";
                        }
                        case QENDING -> {
                            mightyplayer.sector = 0;
                            mightyplayer.starttime = null;
                            mightyplayer.interval = CRED + "+0:00:00";
                            checkQualiEnd(source.getServer());
                            raceboardPutOnlyNamecolor(scoreboard, name, CLGRAY);
                        }
                        case QENDED -> {
                            mightyplayer.sector = 0;
                            mightyplayer.interval = CRED + "+0:00:00";
                        }
                    }
                }
                case RACING -> {
                    Collection<ServerPlayerEntity> target1 = new ArrayList<>(){};
                    target1.add(player);
                    sector(source, target1, number);
                }
            }
            calls+=1;
        }
        return calls;
    }
    private static int pitexit(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int number){
        if (racingstatus==OFFLINE){
            return 0;
        }
        int calls = 0;
        for (ServerPlayerEntity player : targets) {
            String name = player.getGameProfile().getName();
            if (!MightyPlayer.list.containsKey(name)) {
                continue;
            }
            MightyPlayer mightyplayer = MightyPlayer.list.get(name);
            switch (racingstatus) {
                case PRACTICE, QUALI -> {
                    //
                }
                case RACING -> {
                    Collection<ServerPlayerEntity> target1 = new ArrayList<>(){};
                    target1.add(player);
                    lap(source,target1,number);
                    if (mightyplayer.lap > 0 || mightyplayer.lap < racelaps){
                        mightyplayer.stops += 1;
                    }
                }
            }
            calls+=1;
        }
        return calls;
    }
    private static int driver(ServerCommandSource source, Collection<ServerPlayerEntity> targets){
        int calls = 0;
        for (ServerPlayerEntity player : targets) {
            String name = player.getGameProfile().getName();
            if (MightyPlayer.list.containsKey(name)) {
                continue;
            }
            if (racingstatus==QUALI && qualistage!=QSTARTING){
                player.sendMessageToClient(Text.translatable("error.status.during",Text.translatable("shortcut.quali")),false);
                continue;
            }else if (racingstatus==RACING && racestage!=RSTARTING){
                player.sendMessageToClient(Text.translatable("error.status.during",Text.translatable("shortcut.racing")),false);
                continue;
            }
            MightyPlayer mightyplayer = new MightyPlayer(player);
            String cuttedname = MightyData.getName((IEntityDataSaver) player);
            if (Objects.equals(cuttedname, "")){
                cuttedname = cutName(name);
                MightyData.putName(((IEntityDataSaver) player),cuttedname);
            }
            mightyplayer.cuttedname = cuttedname;
            Scoreboard scoreboard = source.getServer().getScoreboard();
            if (racingstatus == PRACTICE) {
                trackBestLoad(track, name);
                raceboardPutSort(scoreboard, name, CWHITE);
            }else if (racingstatus == RACING) {
                raceboardPutSort(scoreboard, name, CWHITE);
            }if (racingstatus == QUALI) {
                raceboardPutSort(scoreboard, name, CWHITE);
            }
            MightyRacingMod.LOGGER.info("Player " + name + " was added to the racing system!");
            player.sendMessageToClient(Text.translatable("info.status.switch",Text.translatable("shortcut.driver")),false);
            calls+=1;
        }
        return calls;
    }
    private static int normal(ServerCommandSource source, Collection<ServerPlayerEntity> targets){
        int calls = 0;
        for (ServerPlayerEntity player : targets) {
            String name = player.getGameProfile().getName();
            if (!MightyPlayer.list.containsKey(name)) {
                continue;
            }
            Scoreboard scoreboard = source.getServer().getScoreboard();
            raceboardRemoveSort(scoreboard, name);
            MightyPlayer.list.remove(name);
            MightyRacingMod.LOGGER.info("Player " + name + " was removed from the racing system!");
            player.sendMessageToClient(Text.translatable("info.status.switch",Text.translatable("shortcut.normal")),false);
            calls+=1;
        }
        return calls;
    }
    private static int racestatus(ServerCommandSource source, int status, String trackname, int minutes, int laps, int stops){
        if (racingstatus == status || Objects.equals(trackname, "name")){
            return 0;
        }
        MightyPlayer.allToZero();
        racingstatus = status;
        Scoreboard scoreboard = source.getServer().getScoreboard();
        clearRaceboard(scoreboard);
        switch (status) {
            case OFFLINE -> {
                raceboardNotDisplay(scoreboard);
                broadcastToDrivers(source.getServer(),Text.translatable("info.racestatus.switch",Text.translatable("shortcut.offline")));
            }
            case PRACTICE -> {
                track = trackname;
                trackBestLoadAll(trackname);
                for (Map.Entry<String, MightyPlayer> listentry : MightyPlayer.list.entrySet()){
                    String name = listentry.getKey();
                    raceboardPutSort(scoreboard, name, CWHITE);
                }
                raceboardDisplay(scoreboard,I18n.translate("shortcut.practice"));
                broadcastToDrivers(source.getServer(),Text.translatable("info.racestatus.switch",Text.translatable("shortcut.practice")));
            }
            case QUALI -> {
                qualistage = QSTARTING;
                qualitime = minutes;
                MightyQualiTime mightydelta = new MightyQualiTime(minutes);
                bestReset();
                for (Map.Entry<String, MightyPlayer> listentry : MightyPlayer.list.entrySet()) {
                    String name = listentry.getKey();
                    raceboardPutSort(scoreboard, name, CWHITE);
                }
                raceboardDisplay(scoreboard, I18n.translate("shortcut.quali") + " " + mightydelta.getString());
                broadcastToDrivers(source.getServer(),Text.translatable("info.racestatus.switch",Text.translatable("shortcut.quali")));
            }
            case RACING -> {
                racestage = RSTARTING;
                fastest = null;
                racecurlap = 0;
                racelaps = laps;
                racestops = stops;
                bestReset();
                for (Map.Entry<String, MightyPlayer> listentry : MightyPlayer.list.entrySet()) {
                    String name = listentry.getKey();
                    raceboardPutSort(scoreboard, name, CWHITE);
                }
                raceboardDisplay(scoreboard,I18n.translate("shortcut.racing") + CWHITE + "  1/" + laps);
                broadcastToDrivers(source.getServer(),Text.translatable("info.racestatus.switch",Text.translatable("shortcut.racing")));
            }
        }
        return 1;
    }
    private static int qualistart(ServerCommandSource source) {
        if (racingstatus != QUALI || qualistage != QSTARTING){
            return 0;
        }
        qualistage = QDURING;
        LocalDateTime now = LocalDateTime.now();
        qualiend = now.plusMinutes(qualitime).plusSeconds(1);
        return 1;
    }
    private static int racestart(ServerCommandSource source) {
        if (racingstatus != RACING || racestage != RSTARTING){
            return 0;
        }
        racestage = RDURING;
        return 1;
    }
    private static int timereset(ServerCommandSource source, Collection<ServerPlayerEntity> targets ,String trackname) {
        int calls = 0;
        if (Objects.equals(trackname, "name")){
            return 0;
        }
        for (ServerPlayerEntity player : targets) {
            String name = player.getGameProfile().getName();
            trackBestReset(trackname, player);
            if (racingstatus == PRACTICE && MightyPlayer.list.containsKey(name)) {
                MightyPlayer.list.get(name).besttimes.clear();
                Scoreboard scoreboard = source.getServer().getScoreboard();
                raceboardRemoveSort(scoreboard, name);
                raceboardPutSort(scoreboard, name, CWHITE);
            }
            player.sendMessageToClient(Text.translatable("info.time.reset",Text.literal(trackname)),false);
            calls+=1;
        }
        return calls;
    }
    private static int setName(ServerCommandSource source, String cuttedname) {
        if (!source.isExecutedByPlayer()){
            return 0;
        }
        String name = Objects.requireNonNull(source.getPlayer()).getGameProfile().getName();
        if (MightyPlayer.list.containsKey(name) && racingstatus != OFFLINE){
            Objects.requireNonNull(source.getPlayer()).sendMessageToClient(Text.translatable("error.name.condition",Text.translatable("shortcut.driver"),Text.translatable("shortcut.offline")),false);
            return 0;
        }
        if (cuttedname.length() < 3){
            Objects.requireNonNull(source.getPlayer()).sendMessageToClient(Text.translatable("error.name.short"),false);
            return 0;
        }
        cuttedname = cutName(cuttedname);
        if (MightyPlayer.list.containsKey(name)){
            MightyPlayer mightyplayer = MightyPlayer.list.get(name);
            mightyplayer.cuttedname=cuttedname;
        }
        MightyData.putName(((IEntityDataSaver)source.getPlayer()),cuttedname);
        Objects.requireNonNull(source.getPlayer()).sendMessageToClient(Text.translatable("info.name.change",Text.literal(cuttedname)),false);
        return 1;
    }
    private static int changelaps(ServerCommandSource source, int laps){
        if (racingstatus != RACING || racestage != RSTARTING){
            return 0;
        }
        racelaps = laps;
        Scoreboard scoreboard = source.getServer().getScoreboard();
        raceboardDisplay(scoreboard,I18n.translate("shortcut.racing") + CWHITE + "  1/" + laps);
        return 1;
    }
    private static int changestops(ServerCommandSource source, int stops){
        if (racingstatus != RACING || racestage != RSTARTING){
            return 0;
        }
        racestops = stops;
        return 1;
    }
    private static int changeminutes(ServerCommandSource source, int minutes){
        if (racingstatus != QUALI || racestage != QSTARTING){
            return 0;
        }
        qualitime = minutes;
        MightyQualiTime mightydelta = new MightyQualiTime(minutes);
        Scoreboard scoreboard = source.getServer().getScoreboard();
        raceboardDisplay(scoreboard,I18n.translate("shortcut.quali") + " " + mightydelta.getString());
        return 1;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private static void clearRaceboard(Scoreboard scoreboard){
        for (Map.Entry<String, MightyPlayer> listentry : MightyPlayer.list.entrySet()){
            MightyPlayer mightyplayer = listentry.getValue();
            if (mightyplayer.raceboardname == null){
                continue;
            }
            MightyScoreBoard.raceboardResetPlayer(mightyplayer.raceboardname,scoreboard);
            mightyplayer.raceboardname = null;
        }
    }
    public static void raceboardPutOnlyNamecolor(Scoreboard scoreboard, String name, String namecolor) {
        MightyPlayer mightyplayer = MightyPlayer.list.get(name);
        if (mightyplayer.raceboardname == null){
            return;
        }
        int scr = MightyScoreBoard.raceboardGetPlayer(mightyplayer.raceboardname,scoreboard);
        MightyScoreBoard.raceboardResetPlayer(mightyplayer.raceboardname,scoreboard);
        mightyplayer.namecolor = namecolor;
        int len = MightyPlayer.list.size();
        int number = (scr - len) * -1;
        if (racingstatus == RACING){
            mightyplayer.raceboardname = raceboardFormatter(number, mightyplayer.namecolor, mightyplayer.cuttedname, (mightyplayer.lap < 10 ? "0" : "") + (mightyplayer.lap == -1 ? 0 : mightyplayer.lap) + "l " + (mightyplayer.sector < 10 ? "0" : "") + mightyplayer.sector + "s");
        }else {
            mightyplayer.raceboardname = raceboardFormatter(number, mightyplayer.namecolor, mightyplayer.cuttedname, (mightyplayer.besttimes.isEmpty() ? I18n.translate("shortcut.notime") : mightyplayer.besttimes.get(0).getString()));
        }
        MightyScoreBoard.raceboardSetPlayer(mightyplayer.raceboardname,scoreboard,scr);
    }
    public static void raceboardPutSort(Scoreboard scoreboard, String name, String namecolor){
        MightyPlayer mightyplayer1 = MightyPlayer.list.get(name);
        mightyplayer1.namecolor = namecolor;
        int completed1 = mightyplayer1.lap * 100 + mightyplayer1.sector;
        int len = 1;
        int scr = 0;
        for (MightyPlayer mightyplayer2 : MightyPlayer.list.values()){
            if (mightyplayer1 == mightyplayer2 || mightyplayer2.raceboardname == null){
                continue;
            }
            len += 1;
        }
        for (MightyPlayer mightyplayer2 : MightyPlayer.list.values()){
            if (mightyplayer1 == mightyplayer2 || mightyplayer2.raceboardname == null){
                continue;
            }
            if (mightyplayer1.raceboardname != null){
                int score1 = MightyScoreBoard.raceboardGetPlayer(mightyplayer1.raceboardname,scoreboard);
                int score2 = MightyScoreBoard.raceboardGetPlayer(mightyplayer2.raceboardname,scoreboard);
                if (score1 > score2){
                    scr += 1;
                    continue;
                }
            }
            boolean result;
            if (racingstatus == RACING){
                int completed2 = mightyplayer2.lap * 100 + mightyplayer2.sector;
                result = (completed1 > completed2);
            }else{
                result = mightyplayer1.besttimes.isEmpty() || mightyplayer2.besttimes.isEmpty() || MightyTime.compare(mightyplayer1.besttimes.get(0),mightyplayer2.besttimes.get(0));
            }
            if (result){
                scr += 1;
                int score = MightyScoreBoard.raceboardGetPlayer(mightyplayer2.raceboardname,scoreboard);
                if (mightyplayer1.raceboardname != null) {
                    score -= 1;
                }
                MightyScoreBoard.raceboardResetPlayer(mightyplayer2.raceboardname,scoreboard);
                int number = (score - len) * -1;
                if (racingstatus == RACING){
                    mightyplayer2.raceboardname = raceboardFormatter(number, mightyplayer2.namecolor, mightyplayer2.cuttedname, (mightyplayer2.lap < 10 ? "0" : "") + (mightyplayer2.lap == -1 ? 0 : mightyplayer2.lap) + "l " + (mightyplayer2.sector < 10 ? "0" : "") + mightyplayer2.sector + "s");
                }else {
                    mightyplayer2.raceboardname = raceboardFormatter(number, mightyplayer2.namecolor, mightyplayer2.cuttedname, (mightyplayer2.besttimes.isEmpty() ? I18n.translate("shortcut.notime") : mightyplayer2.besttimes.get(0).getString()));
                }
                MightyScoreBoard.raceboardSetPlayer(mightyplayer2.raceboardname,scoreboard,score);
            }else{
                if (mightyplayer1.raceboardname == null) {
                    int score = MightyScoreBoard.raceboardGetPlayer(mightyplayer2.raceboardname,scoreboard);
                    MightyScoreBoard.raceboardSetPlayer(mightyplayer2.raceboardname,scoreboard,score+1);
                }
            }
        }
        if (mightyplayer1.raceboardname != null) {
            MightyScoreBoard.raceboardResetPlayer(mightyplayer1.raceboardname,scoreboard);
        }
        int number = (scr - len) * -1;
        if (racingstatus == RACING){
            mightyplayer1.raceboardname = raceboardFormatter(number, mightyplayer1.namecolor, mightyplayer1.cuttedname, (mightyplayer1.lap < 10 ? "0" : "") + (mightyplayer1.lap == -1 ? 0 : mightyplayer1.lap) + "l " + (mightyplayer1.sector < 10 ? "0" : "") + mightyplayer1.sector + "s");
        }else {
            mightyplayer1.raceboardname = raceboardFormatter(number, mightyplayer1.namecolor, mightyplayer1.cuttedname, (mightyplayer1.besttimes.isEmpty() ? I18n.translate("shortcut.notime") : mightyplayer1.besttimes.get(0).getString()));
        }
        MightyScoreBoard.raceboardSetPlayer(mightyplayer1.raceboardname,scoreboard,scr);
    }
    public static void raceboardRemoveSort(Scoreboard scoreboard, String name){
        MightyPlayer mightyplayer1 = MightyPlayer.list.get(name);
        if (mightyplayer1.raceboardname == null) {
            return;
        }
        int len = MightyPlayer.list.size();
        for (MightyPlayer mightyplayer2 : MightyPlayer.list.values()){
            if (mightyplayer1 == mightyplayer2){
                continue;
            }
            if (mightyplayer2.raceboardname != null){
                int score1 = MightyScoreBoard.raceboardGetPlayer(mightyplayer1.raceboardname,scoreboard);
                int score2 = MightyScoreBoard.raceboardGetPlayer(mightyplayer2.raceboardname,scoreboard);
                if (score1 > score2){
                    MightyScoreBoard.raceboardResetPlayer(mightyplayer2.raceboardname,scoreboard);
                    int number = (score2 - len + 1) * -1;
                    if (racingstatus == RACING){
                        mightyplayer2.raceboardname = raceboardFormatter(number, mightyplayer2.namecolor, mightyplayer2.cuttedname, (mightyplayer2.lap < 10 ? "0" : "") + (mightyplayer2.lap == -1 ? 0 : mightyplayer2.lap) + "l " + (mightyplayer2.sector < 10 ? "0" : "") + mightyplayer2.sector + "s");
                    }else{
                        mightyplayer2.raceboardname = raceboardFormatter(number, mightyplayer2.namecolor, mightyplayer2.cuttedname, (mightyplayer2.besttimes.isEmpty() ? I18n.translate("shortcut.notime") : mightyplayer2.besttimes.get(0).getString()));
                    }
                    MightyScoreBoard.raceboardSetPlayer(mightyplayer2.raceboardname,scoreboard,score2);
                }else{
                    MightyScoreBoard.raceboardSetPlayer(mightyplayer2.raceboardname,scoreboard,score2-1);
                }

            }
        }
        MightyScoreBoard.raceboardResetPlayer(mightyplayer1.raceboardname,scoreboard);
        mightyplayer1.raceboardname = null;
    }
    public static void raceboardDisplay(Scoreboard scoreboard, String name){
        ScoreboardObjective raceboard = scoreboard.getNullableObjective("MRM_raceboard");
        if (raceboard != null) {
            raceboard.setDisplayName(Text.literal(name));
            MightyScoreBoard.setRaceboardSidebar(scoreboard);
            raceboarddisplayname = name;
        }
    }
    private static void raceboardNotDisplay(Scoreboard scoreboard){
        MightyScoreBoard.resetSlotSidebar(scoreboard);
    }
    private static void bestReset() {
        for (MightyPlayer mightyplayer : MightyPlayer.list.values()) {
            mightyplayer.besttimes.clear();
        }
    }
    private static void bestSet(String name, List<MightyTime> times) {
        MightyPlayer mightyplayer = MightyPlayer.list.get(name);
        mightyplayer.besttimes.clear();
        mightyplayer.besttimes.addAll(times);
    }
    private static void trackBestLoad(String trackname, String name){
        MightyPlayer mightyplayer = MightyPlayer.list.get(name);
        mightyplayer.besttimes.clear();
        int[] fromTimes = MightyData.getTime(((IEntityDataSaver) mightyplayer.player),trackname);
        for (int i : fromTimes){
            mightyplayer.besttimes.add(i==-1 ? null : new MightyTime(i));
        }
    }
    private static void trackBestLoadAll(String trackname){
        for (Map.Entry<String, MightyPlayer> listentry : MightyPlayer.list.entrySet()) {
            MightyPlayer mightyplayer = listentry.getValue();
            String name = listentry.getKey();
            mightyplayer.besttimes.clear();
            int[] fromTimes = MightyData.getTime(((IEntityDataSaver) mightyplayer.player), trackname);
            for (int i : fromTimes){
                mightyplayer.besttimes.add(i==-1 ? null : new MightyTime(i));
            }
        }
    }
    private static void trackBestReset(String trackname, ServerPlayerEntity player) {
        MightyData.removeTime(((IEntityDataSaver) player),trackname);
    }
    private static void trackBestSet(String trackname, String name, List<MightyTime> times) {
        MightyPlayer mightyplayer = MightyPlayer.list.get(name);
        mightyplayer.besttimes.clear();
        List<Integer> toTimes = new ArrayList<>();
        times.forEach(entry -> toTimes.add((entry==null ? -1 : entry.getInt())));
        mightyplayer.besttimes.addAll(times);
        MightyData.putTime(((IEntityDataSaver) mightyplayer.player),trackname,toTimes);
    }
    public static void broadcastToDrivers(MinecraftServer server, Text message) {
        if (MightyConfig.getBoolean("broadcast_only_to_drivers")){
            for (MightyPlayer mightyplayer : MightyPlayer.list.values()){
                mightyplayer.player.sendMessageToClient(message,false);
            }
        }else{
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()){
                player.sendMessageToClient(message,false);
            }
        }
    }
    public static void checkQualiEnd(MinecraftServer server){
        for (Map.Entry<String, MightyPlayer> listentry : MightyPlayer.list.entrySet()){
            MightyPlayer mightyplayer = listentry.getValue();
            if (mightyplayer.starttime != null){
                return;
            }
        }
        qualistage = QENDED;
        broadcastToDrivers(server,Text.translatable("info.ended",Text.translatable("shortcut.quali")));
    }
    public static void checkRaceEnd(MinecraftServer server){
        for (Map.Entry<String, MightyPlayer> listentry : MightyPlayer.list.entrySet()){
            MightyPlayer mightyplayer = listentry.getValue();
            if (mightyplayer.starttime != null){
                return;
            }
        }
        racestage = RENDED;
        broadcastToDrivers(server,Text.translatable("info.ended",Text.translatable("shortcut.racing")));
        StringBuilder stopstring = new StringBuilder();
        int required = MightyConfig.getInteger("mandatory_pit_stops");
        for (Map.Entry<String, MightyPlayer> listentry : MightyPlayer.list.entrySet()){
            MightyPlayer mightyplayer = listentry.getValue();
            if (mightyplayer.stops < required){
                stopstring.append(mightyplayer.cuttedname).append(", ");
            }
        }
        if (!stopstring.isEmpty()) {
            stopstring = new StringBuilder(stopstring.substring(0, stopstring.length() - 2));
            broadcastToDrivers(server,Text.translatable("info.pitstops.completed",Text.literal(stopstring.toString())));
        }
    }
    private static String cutName(String cuttedname){
        cuttedname = cuttedname.toUpperCase();
        if (cuttedname.length()>3){
            cuttedname = cuttedname.substring(0,3);
        }
        return cuttedname;
    }
    public static String raceboardFormatter(int number, String namecolor, String cuttedname, String value){
        return (number < 10 ? " " : "") + number + CGRAY + " | " + namecolor + cuttedname + "      " + CWHITE + CBOLD + value;
    }
}
