package com.mightyracing;

import com.mightyracing.config.Config;
import com.mightyracing.util.IEntityDataSaver;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

import static com.mightyracing.MightyText.*;

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
    public static int maxdurability = 0;
    public static List<String> trackname_blacklist = List.of("name","stats");
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess ignoredCommandRegistryAccess, CommandManager.RegistrationEnvironment ignoredRegistrationEnvironment) {
        CommandNode<ServerCommandSource> node = dispatcher.register(CommandManager.literal("mightyracing")
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
                                .then(CommandManager.literal("pitstop")
                                        .then(CommandManager.argument("durability",IntegerArgumentType.integer(0,10000000))
                                                .executes(context -> pitstop(context.getSource(), EntityArgumentType.getPlayers(context,"targets"), IntegerArgumentType.getInteger(context, "durability")))
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
                                        .executes(context -> racestatus(context.getSource(), OFFLINE, context))
                                )
                                .then(CommandManager.literal("practice")
                                        .then(CommandManager.argument("track", StringArgumentType.string())
                                                .executes(context -> racestatus(context.getSource(), PRACTICE, context))
                                        )
                                )
                                .then(CommandManager.literal("quali")
                                        .then(CommandManager.argument("minutes", IntegerArgumentType.integer(1, 60))
                                                .executes(context -> racestatus(context.getSource(), QUALI, context))
                                        )
                                )
                                .then(CommandManager.literal("racing")
                                        .then(CommandManager.argument("laps", IntegerArgumentType.integer(1, 99))
                                                .executes(context -> racestatus(context.getSource(), RACING, context))
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
                        .then(CommandManager.literal("statsreset")
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .executes(context -> statsreset(context.getSource(), EntityArgumentType.getPlayers(context, "targets")))
                                )
                        )
                        .then(CommandManager.literal("start")
                                .then(CommandManager.literal("quali")
                                        .executes(context -> qualistart(context.getSource()))
                                )
                                .then(CommandManager.literal("race")
                                        .executes(context -> racestart(context.getSource()))
                                )
                        )
                )
                .then(CommandManager.literal("name")
                        .executes(context -> setName(context.getSource(), null))
                        .then(CommandManager.argument("name",StringArgumentType.string())
                                .executes(context -> setName(context.getSource(), StringArgumentType.getString(context,"name")))
                        )
                )
                .then(CommandManager.literal("stats")
                        .executes(context -> showStats(context.getSource(), context.getSource().getPlayer()))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(context -> showStats(context.getSource(),EntityArgumentType.getPlayer(context, "player")))
                        )
                )
                .then(CommandManager.literal("select").requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> select(context.getSource(), " "))
                        .then(CommandManager.argument(" ", StringArgumentType.word())
                                .executes(context -> select(context.getSource(), StringArgumentType.getString(context," ")))
                        )
                )
        );
        addOptional(node.getChild("system").getChild("racestatus").getChild("racing").getChild("laps"), Map.of(
                "maxdurability", IntegerArgumentType.integer(0, 10000000),
                "pitstops", IntegerArgumentType.integer(0,10)
        ), context -> racestatus(context.getSource(), RACING, context));
        dispatcher.register(CommandManager.literal("mr").redirect(node));
    }

    private static void addOptional(CommandNode<ServerCommandSource> base, Map<String, ArgumentType<?>> args, Command<ServerCommandSource> command){
        List<Supplier<CommandNode<ServerCommandSource>>> supps = new ArrayList<>();
        for (Map.Entry<String, ArgumentType<?>> entry : args.entrySet()){
            String name = entry.getKey();
            ArgumentType<?> arg = entry.getValue();
            supps.add(() -> CommandManager.literal(name).then(CommandManager.argument(name,arg).executes(command)).build());
        }
        subTree(base, supps);
    }
    private static void subTree(CommandNode<ServerCommandSource> base, List<Supplier<CommandNode<ServerCommandSource>>> args){
        Optional<CommandNode<ServerCommandSource>> base2 = base.getChildren().stream().findAny();
        if (base2.isPresent()){
            base = base2.get();
        }
        for (Supplier<CommandNode<ServerCommandSource>> entry : args) {
            CommandNode<ServerCommandSource> entryNode = entry.get();
            base.addChild(entryNode);
            List<Supplier<CommandNode<ServerCommandSource>>> remainingArgs = new ArrayList<>(args);
            remainingArgs.remove(entry);
            if (!remainingArgs.isEmpty()) {
                subTree(entryNode, remainingArgs);
            }
        }
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
            if (mightyplayer.sector >= number || mightyplayer.sector + 1 + Config.CHECKPOINT_PRECISION.get() < number) {
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
            if (((mightyplayer.sector >= number || mightyplayer.sector == 0 || mightyplayer.sector + 1 + Config.CHECKPOINT_PRECISION.get() < number) && start != null) || mightyplayer.finished) {
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
                            player.sendMessageToClient(Text.literal(String.format(info_time_personal,CGREEN + CBOLD + mightydelta.getString())),false);
                            raceboardPutSort(scoreboard, name, CWHITE);
                        } else {
                            player.sendMessageToClient(Text.literal(String.format(info_time_bad,CRED + CBOLD + mightydelta.getString())),false);
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
                                    player.sendMessageToClient(Text.literal(String.format(info_time_personal,CGREEN + CBOLD + mightydelta.getString())),false);
                                    raceboardPutSort(scoreboard, name, CWHITE);
                                } else {
                                    player.sendMessageToClient(Text.literal(String.format(info_time_bad,CRED + CBOLD + mightydelta.getString())),false);
                                }
                            }
                            mightyplayer.starttime = now;
                        }
                        case QENDING -> {
                            if (start != null) {
                                if (fast) {
                                    bestSet(name,mightyplayer.currenttimes);
                                    player.sendMessageToClient(Text.literal(String.format(info_time_personal,CGREEN + CBOLD + mightydelta.getString())),false);
                                    raceboardPutSort(scoreboard, name, CLGRAY);
                                } else {
                                    player.sendMessageToClient(Text.literal(String.format(info_time_bad,CRED + CBOLD + mightydelta.getString())),false);
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
                                            raceboardPutOnlyNamecolor(scoreboard, fastest.player.getGameProfile().getName(), (fastest.namecolor.equals(CDPURPLE)) ? CLGRAY : CWHITE);
                                            fastest = mightyplayer;
                                            broadcastToDrivers(source.getServer(),Text.literal(String.format(info_time_fastest,mightyplayer.cuttedname + " " + CPURPLE + CBOLD + mightydelta.getString())),null);
                                        } else {
                                            player.sendMessageToClient(Text.literal(String.format(info_time_personal,CGREEN + CBOLD + mightydelta.getString())),false);
                                        }
                                    }else{
                                        broadcastToDrivers(source.getServer(),Text.literal(String.format(info_time_fastest,mightyplayer.cuttedname + " " + CPURPLE + CBOLD + mightydelta.getString())),null);
                                        fastest = mightyplayer;
                                    }
                                } else {
                                    player.sendMessageToClient(Text.literal(String.format(info_time_bad,CRED + CBOLD + mightydelta.getString())),false);
                                }
                                raceboardPutSort(scoreboard, name, (fastest == mightyplayer) ? CPURPLE : CWHITE);
                                if (mightyplayer.lap >= racelaps){
                                    if (Config.AUTO_FINISH.get()) {
                                        racestage = RENDING;
                                    }
                                    raceboardDisplay(scoreboard,shortcut_racing + CGRAY + "  " + racelaps + "/" + racelaps);
                                    player.sendMessageToClient(Text.literal(info_race_finish),false);
                                    if (mightyplayer.stops >= racestops) {
                                        raceboardPutOnlyNamecolor(scoreboard, name, (fastest == mightyplayer) ? CDPURPLE : CLGRAY);
                                    }else{
                                        broadcastToDrivers(source.getServer(),Text.literal(String.format(info_pitstops_completed,mightyplayer.cuttedname)),null);
                                        raceboardPutToEnd(scoreboard, name, CRED);
                                    }
                                    mightyplayer.starttime = null;
                                    mightyplayer.finished = true;
                                    checkRaceEnd(source.getServer());
                                }else{
                                    if (mightyplayer.lap + 1 > racecurlap) {
                                        racecurlap = mightyplayer.lap + 1;
                                        if (racecurlap == racelaps) {
                                            raceboardDisplay(scoreboard, shortcut_racing + CRED + "  " + racecurlap + "/" + racelaps);
                                        }else{
                                            raceboardDisplay(scoreboard, shortcut_racing + CWHITE + "  " + racecurlap + "/" + racelaps);
                                        }
                                    }
                                    mightyplayer.starttime = now;
                                }
                            }else {
                                if (maxdurability == 0 || mightyplayer.durability > 0) {
                                    mightyplayer.starttime = now;
                                }
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
                                            broadcastToDrivers(source.getServer(),Text.literal(String.format(info_time_fastest,mightyplayer.cuttedname + " " + CPURPLE + CBOLD + mightydelta.getString())),null);
                                        } else {
                                            player.sendMessageToClient(Text.literal(String.format(info_time_personal,CGREEN + CBOLD + mightydelta.getString())),false);
                                        }
                                    }else{
                                        broadcastToDrivers(source.getServer(),Text.literal(String.format(info_time_fastest,mightyplayer.cuttedname + " " + CPURPLE + CBOLD + mightydelta.getString())),null);
                                        fastest = mightyplayer;
                                    }
                                } else {
                                    player.sendMessageToClient(Text.literal(String.format(info_time_bad,CRED + CBOLD + mightydelta.getString())),false);
                                }
                                raceboardPutSort(scoreboard, name, (fastest == mightyplayer) ? CDPURPLE : CLGRAY);
                                player.sendMessageToClient(Text.literal(info_race_finish),false);
                                if (mightyplayer.stops < racestops) {
                                    broadcastToDrivers(source.getServer(),Text.literal(String.format(info_pitstops_completed,mightyplayer.cuttedname)),null);
                                    raceboardPutToEnd(scoreboard, name, CRED);
                                }
                                mightyplayer.starttime = null;
                                mightyplayer.finished = true;
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
            if (mightyplayer.finished){
                continue;
            }
            switch (racingstatus) {
                case PRACTICE, QUALI -> {
                    //
                }
                case RACING -> {
                    Collection<ServerPlayerEntity> target1 = new ArrayList<>(){};
                    target1.add(player);
                    lap(source,target1,number);
                    if (mightyplayer.lap > 0 && mightyplayer.lap < racelaps){
                        mightyplayer.stops += 1;
                    }
                }
            }
            calls+=1;
        }
        return calls;
    }
    private static int pitstop(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int dur){
        if (maxdurability == 0 || racingstatus != RACING || (racestage != RDURING && racestage != RENDING)){
            return 0;
        }
        int calls = 0;
        for (ServerPlayerEntity player : targets) {
            String name = player.getGameProfile().getName();
            if (!MightyPlayer.list.containsKey(name)) {
                continue;
            }
            MightyPlayer mightyplayer = MightyPlayer.list.get(name);
            if (mightyplayer.starttime == null){
                continue;
            }
            mightyplayer.durability += dur;
            if (mightyplayer.durability >= maxdurability){
                mightyplayer.durability = maxdurability;
            }
            calls+=1;
        }
        return calls;
    }
    private static int driver(ServerCommandSource source, Collection<ServerPlayerEntity> targets){
        int calls = 0;
        if (racingstatus==QUALI && qualistage!=QSTARTING){
            source.sendError(Text.literal(String.format(error_status_during,shortcut_quali)));
            return 0;
        }else if (racingstatus==RACING && racestage!=RSTARTING){
            source.sendError(Text.literal(String.format(error_status_during,shortcut_racing)));
            return 0;
        }
        for (ServerPlayerEntity player : targets) {
            String name = player.getGameProfile().getName();
            if (MightyPlayer.list.containsKey(name)) {
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
            String text = String.format(info_status_switch,shortcut_driver);
            switch (racingstatus){
                case OFFLINE -> {
                    text += "\n" + String.format(info_racestatus_now,shortcut_offline);
                }
                case PRACTICE -> {
                    trackBestLoad(track, name);
                    raceboardPutSort(scoreboard, name, CWHITE);
                    text += "\n" + String.format(info_racestatus_now,shortcut_practice) + "\n" + String.format(info_racestatus_track,track);
                }
                case QUALI -> {
                    raceboardPutSort(scoreboard, name, CWHITE);
                    text += "\n" + String.format(info_racestatus_now,shortcut_quali) + "\n" + String.format(info_racestatus_duration,qualitime);
                }
                case RACING -> {
                    raceboardPutSort(scoreboard, name, CWHITE);
                    if (maxdurability != 0){
                        mightyplayer.durability = maxdurability;
                    }
                    text += "\n" + String.format(info_racestatus_now,shortcut_racing) +
                            "\n" + String.format(info_racestatus_laps,racelaps) +
                            (racestops == 0 ? "" : "\n" + String.format(info_racestatus_pitstops,racestops)) +
                            (maxdurability == 0 ? "" : "\n" + String.format(info_racestatus_durability,maxdurability));
                }
            }
            if (source.getEntity() != player) {
                source.sendFeedback(() -> Text.literal(String.format(feedback_status_switch, name, shortcut_driver)), false);
            }
            player.sendMessageToClient(Text.literal(text),false);
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
            if (source.getEntity() != player) {
                source.sendFeedback(() -> Text.literal(String.format(feedback_status_switch, name, shortcut_normal)), false);
            }
            player.sendMessageToClient(Text.literal(String.format(info_status_switch,shortcut_normal)),false);
            checkQualiEnd(source.getServer());
            checkRaceEnd(source.getServer());
            calls+=1;
        }
        return calls;
    }
    private static int racestatus(ServerCommandSource source, int status, CommandContext<ServerCommandSource> context){
        String trackname = "";
        try {
            trackname = StringArgumentType.getString(context, "track");
        }catch (Exception ignored){
        }
        MightyPlayer.allToZero();
        racingstatus = status;
        Scoreboard scoreboard = source.getServer().getScoreboard();
        clearRaceboard(scoreboard);
        switch (status) {
            case OFFLINE -> {
                raceboardNotDisplay(scoreboard);
                String text = String.format(info_racestatus_switch, shortcut_offline);
                source.sendFeedback(() -> Text.literal(text), false);
                broadcastToDrivers(source.getServer(),Text.literal(text),source.getPlayer());
            }
            case PRACTICE -> {
                if (trackname_blacklist.contains(trackname)){
                    source.sendError(Text.literal(error_illegal_trackname));
                    return 0;
                }
                track = trackname;
                trackBestLoadAll(trackname);
                for (Map.Entry<String, MightyPlayer> listentry : MightyPlayer.list.entrySet()){
                    String name = listentry.getKey();
                    raceboardPutSort(scoreboard, name, CWHITE);
                }
                raceboardDisplay(scoreboard,shortcut_practice);
                String text = String.format(info_racestatus_switch, shortcut_practice) + "\n" + String.format(info_racestatus_track, trackname);
                source.sendFeedback(() -> Text.literal(text), false);
                broadcastToDrivers(source.getServer(),Text.literal(text),source.getPlayer());
            }
            case QUALI -> {
                qualistage = QSTARTING;
                try {
                    qualitime = IntegerArgumentType.getInteger(context, "minutes");
                }catch (Exception ignored){
                }
                MightyQualiTime mightydelta = new MightyQualiTime(qualitime);
                bestReset();
                for (Map.Entry<String, MightyPlayer> listentry : MightyPlayer.list.entrySet()) {
                    String name = listentry.getKey();
                    raceboardPutSort(scoreboard, name, CWHITE);
                }
                raceboardDisplay(scoreboard, shortcut_quali + " " + mightydelta.getString());
                String text = String.format(info_racestatus_switch, shortcut_quali) + "\n" + String.format(info_racestatus_duration, qualitime);
                source.sendFeedback(() -> Text.literal(text), false);
                broadcastToDrivers(source.getServer(),Text.literal(text),source.getPlayer());
            }
            case RACING -> {
                racestage = RSTARTING;
                fastest = null;
                racecurlap = 0;
                try {
                    racelaps = IntegerArgumentType.getInteger(context, "laps");
                }catch (Exception ignored){
                }
                try {
                    maxdurability = IntegerArgumentType.getInteger(context, "maxdurability");
                }catch (Exception e){
                    maxdurability = 0;
                }
                try {
                    racestops = IntegerArgumentType.getInteger(context, "pitstops");
                }catch (Exception e){
                    racestops = 0;
                }
                bestReset();
                for (Map.Entry<String, MightyPlayer> listentry : MightyPlayer.list.entrySet()) {
                    String name = listentry.getKey();
                    raceboardPutSort(scoreboard, name, CWHITE);
                    if (maxdurability != 0){
                        MightyPlayer mightyplayer = listentry.getValue();
                        mightyplayer.durability = maxdurability;
                    }
                }
                raceboardDisplay(scoreboard,shortcut_racing + CWHITE + "  1/" + racelaps);
                String text = String.format(info_racestatus_switch, shortcut_racing) +
                        "\n" + String.format(info_racestatus_laps,racelaps) +
                        (racestops == 0 ? "" : "\n" + String.format(info_racestatus_pitstops,racestops)) +
                        (maxdurability == 0 ? "" : "\n" + String.format(info_racestatus_durability,maxdurability));
                source.sendFeedback(() -> Text.literal(text), false);
                broadcastToDrivers(source.getServer(),Text.literal(text),source.getPlayer());
            }
        }
        return 1;
    }
    private static int qualistart(ServerCommandSource source) {
        if (racingstatus != QUALI || qualistage != QSTARTING){
            source.sendError(Text.literal(String.format(error_start,shortcut_quali)));
            return 0;
        }
        qualistage = QDURING;
        LocalDateTime now = LocalDateTime.now();
        qualiend = now.plusMinutes(qualitime).plusSeconds(1);
        source.sendFeedback(() -> Text.literal(String.format(feedback_start, shortcut_quali)), false);
        return 1;
    }
    private static int racestart(ServerCommandSource source) {
        if (racingstatus != RACING || racestage != RSTARTING){
            source.sendError(Text.literal(String.format(error_start,shortcut_racing)));
            return 0;
        }
        racestage = RDURING;
        source.sendFeedback(() -> Text.literal(String.format(feedback_start, shortcut_racing)), false);
        return 1;
    }
    private static int timereset(ServerCommandSource source, Collection<ServerPlayerEntity> targets ,String trackname) {
        int calls = 0;
        if (trackname_blacklist.contains(trackname)){
            source.sendError(Text.literal(error_illegal_trackname));
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
            if (source.getEntity() != player) {
                source.sendFeedback(() -> Text.literal(String.format(feedback_time_reset, name, trackname)), false);
            }
            player.sendMessageToClient(Text.literal(String.format(info_time_reset,trackname)),false);
            calls+=1;
        }
        return calls;
    }
    private static int statsreset(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        int calls = 0;
        for (ServerPlayerEntity player : targets) {
            MightyData.removeStats((IEntityDataSaver) player);
            if (source.getEntity() != player) {
                source.sendFeedback(() -> Text.literal(String.format(feedback_stats_reset, player.getGameProfile().getName())), false);
            }
            player.sendMessageToClient(Text.literal(info_stats_reset),false);
            calls+=1;
        }
        return calls;
    }
    private static int setName(ServerCommandSource source, String cuttedname) {
        if (!source.isExecutedByPlayer()){
            source.sendError(Text.literal(error_notplayer));
            return 0;
        }
        if (cuttedname == null){
            cuttedname = MightyData.getName((IEntityDataSaver) source.getPlayer());
            if (cuttedname.isEmpty()){
                source.getPlayer().sendMessageToClient(Text.literal(String.format(info_name_no, cuttedname)),false);
            }else{
                source.getPlayer().sendMessageToClient(Text.literal(String.format(info_name, cuttedname)),false);
            }
            return 1;
        }
        String name = Objects.requireNonNull(source.getPlayer()).getGameProfile().getName();
        if (MightyPlayer.list.containsKey(name) && racingstatus != OFFLINE){
            source.sendError(Text.literal(String.format(error_name_condition,shortcut_driver,shortcut_offline)));
            return 0;
        }
        if (cuttedname.length() < 3){
            source.sendError(Text.literal(error_name_short));
            return 0;
        }
        cuttedname = cutName(cuttedname);
        if (MightyPlayer.list.containsKey(name)){
            MightyPlayer mightyplayer = MightyPlayer.list.get(name);
            mightyplayer.cuttedname = cuttedname;
        }
        MightyData.putName(((IEntityDataSaver)source.getPlayer()), cuttedname);
        source.getPlayer().sendMessageToClient(Text.literal(String.format(info_name_change, cuttedname)),false);
        return 1;
    }
    private static int showStats(ServerCommandSource source, ServerPlayerEntity statsPlayer) {
        if (source.getPlayer() == null){
            source.sendError(Text.literal(error_notplayer));
            return 0;
        }
        String name = statsPlayer.getGameProfile().getName();
        Map<String, Integer> stats = MightyData.getStats((IEntityDataSaver) statsPlayer);
        String racename = MightyData.getName((IEntityDataSaver) statsPlayer);
        source.getPlayer().sendMessageToClient(Text.literal(statsFormatter(name, racename, stats)),false);
        return 1;
    }
    private static int select(ServerCommandSource source, String str) {
        if (source.getPlayer() == null){
            source.sendError(Text.literal(error_notplayer));
            return 0;
        }
        ServerPlayerEntity player = source.getPlayer();
        switch (str){
            case "position1" -> {
                MightySelection.setPos(source.getPlayer(), source.getPosition(), 1, source.getWorld());
                return 1;
            }
            case "position2" -> {
                MightySelection.setPos(source.getPlayer(), source.getPosition(), 2, source.getWorld());
                return 1;
            }
            case "generate" -> {
                String res = MightySelection.getSelector(player);
                if (!res.isEmpty()){
                    Text message = Text.literal(res).setStyle(Style.EMPTY
                            .withColor(Formatting.GREEN)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, res))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(tooltip_copy)))
                    );
                    player.sendMessageToClient(message, false);
                }else{
                    source.sendError(Text.literal(error_noposition));
                }
                return 1;
            }
            case "clear" -> {
                MightySelection.removeSelector(player);
                return 1;
            }
        }
        Text message = Text.literal("").setStyle(Style.EMPTY).append(Text.literal(button_pos1).setStyle(Style.EMPTY
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mightyracing select position1"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(tooltip_pos1)))
        )).append(Text.literal("  ")).setStyle(Style.EMPTY).append(Text.literal(button_pos2).setStyle(Style.EMPTY
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mightyracing select position2"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(tooltip_pos2)))
        )).append(Text.literal("  ")).setStyle(Style.EMPTY).append(Text.literal(button_generate).setStyle(Style.EMPTY
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mightyracing select generate"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(tooltip_generate)))
        )).append(Text.literal("  ")).setStyle(Style.EMPTY).append(Text.literal(button_clear).setStyle(Style.EMPTY
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mightyracing select clear"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(tooltip_clear)))
        ));
        player.sendMessageToClient(message, false);
        return 1;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private static void clearRaceboard(Scoreboard scoreboard){
        for (Map.Entry<String, MightyPlayer> listentry : MightyPlayer.list.entrySet()){
            MightyPlayer mightyplayer = listentry.getValue();
            if (mightyplayer.raceboardname == null){
                continue;
            }
            MightyDifferences.raceboardResetPlayer(mightyplayer.raceboardname,scoreboard);
            mightyplayer.raceboardname = null;
        }
    }
    public static void raceboardPutOnlyNamecolor(Scoreboard scoreboard, String name, String namecolor) {
        MightyPlayer mightyplayer = MightyPlayer.list.get(name);
        if (mightyplayer.raceboardname == null){
            return;
        }
        int scr = MightyDifferences.raceboardGetPlayer(mightyplayer.raceboardname,scoreboard);
        MightyDifferences.raceboardResetPlayer(mightyplayer.raceboardname,scoreboard);
        mightyplayer.namecolor = namecolor;
        int len = MightyPlayer.list.size();
        int number = (scr - len) * -1;
        mightyplayer.raceboardname = raceboardFormatter(number, mightyplayer);
        MightyDifferences.raceboardSetPlayer(mightyplayer.raceboardname,scoreboard,scr);
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
                int score1 = MightyDifferences.raceboardGetPlayer(mightyplayer1.raceboardname,scoreboard);
                int score2 = MightyDifferences.raceboardGetPlayer(mightyplayer2.raceboardname,scoreboard);
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
                int score = MightyDifferences.raceboardGetPlayer(mightyplayer2.raceboardname,scoreboard);
                if (mightyplayer1.raceboardname != null) {
                    score -= 1;
                }
                MightyDifferences.raceboardResetPlayer(mightyplayer2.raceboardname,scoreboard);
                int number = (score - len) * -1;
                mightyplayer2.raceboardname = raceboardFormatter(number, mightyplayer2);
                MightyDifferences.raceboardSetPlayer(mightyplayer2.raceboardname,scoreboard,score);
            }else{
                if (mightyplayer1.raceboardname == null) {
                    int score = MightyDifferences.raceboardGetPlayer(mightyplayer2.raceboardname,scoreboard);
                    MightyDifferences.raceboardSetPlayer(mightyplayer2.raceboardname,scoreboard,score+1);
                }
            }
        }
        if (mightyplayer1.raceboardname != null) {
            MightyDifferences.raceboardResetPlayer(mightyplayer1.raceboardname,scoreboard);
        }
        int number = (scr - len) * -1;
        mightyplayer1.raceboardname = raceboardFormatter(number, mightyplayer1);
        MightyDifferences.raceboardSetPlayer(mightyplayer1.raceboardname,scoreboard,scr);
    }
    private static void raceboardPutToEnd(Scoreboard scoreboard, String name, String namecolor){
        MightyPlayer mightyplayer1 = MightyPlayer.list.get(name);
        mightyplayer1.namecolor = namecolor;
        int len = MightyPlayer.list.size();
        for (MightyPlayer mightyplayer2 : MightyPlayer.list.values()) {
            if (mightyplayer1 == mightyplayer2) {
                continue;
            }
            int score1 = MightyDifferences.raceboardGetPlayer(mightyplayer1.raceboardname,scoreboard);
            int score2 = MightyDifferences.raceboardGetPlayer(mightyplayer2.raceboardname,scoreboard);
            if (score1 > score2){
                score2+=1;
                MightyDifferences.raceboardResetPlayer(mightyplayer2.raceboardname,scoreboard);
                int number = (score2 - len) * -1;
                mightyplayer2.raceboardname = raceboardFormatter(number,mightyplayer2);
                MightyDifferences.raceboardSetPlayer(mightyplayer2.raceboardname,scoreboard,score2);
            }
        }
        MightyDifferences.raceboardResetPlayer(mightyplayer1.raceboardname,scoreboard);
        mightyplayer1.raceboardname = raceboardFormatter(len,mightyplayer1);
        MightyDifferences.raceboardSetPlayer(mightyplayer1.raceboardname,scoreboard,0);
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
                int score1 = MightyDifferences.raceboardGetPlayer(mightyplayer1.raceboardname,scoreboard);
                int score2 = MightyDifferences.raceboardGetPlayer(mightyplayer2.raceboardname,scoreboard);
                if (score1 > score2){
                    MightyDifferences.raceboardResetPlayer(mightyplayer2.raceboardname,scoreboard);
                    int number = (score2 - len + 1) * -1;
                    mightyplayer2.raceboardname = raceboardFormatter(number, mightyplayer2);
                    MightyDifferences.raceboardSetPlayer(mightyplayer2.raceboardname,scoreboard,score2);
                }else{
                    MightyDifferences.raceboardSetPlayer(mightyplayer2.raceboardname,scoreboard,score2-1);
                }

            }
        }
        MightyDifferences.raceboardResetPlayer(mightyplayer1.raceboardname,scoreboard);
        mightyplayer1.raceboardname = null;
    }
    public static void raceboardDisplay(Scoreboard scoreboard, String name){
        ScoreboardObjective raceboard = scoreboard.getNullableObjective("MRM_raceboard");
        if (raceboard != null) {
            raceboard.setDisplayName(Text.literal(name));
            MightyDifferences.setRaceboardSidebar(scoreboard);
            raceboarddisplayname = name;
        }
    }
    private static void raceboardNotDisplay(Scoreboard scoreboard){
        MightyDifferences.resetSlotSidebar(scoreboard);
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
    public static void broadcastToDrivers(MinecraftServer server, Text message, ServerPlayerEntity except) {
        if (Config.BROADCAST_ONLY_TO_DRIVERS.get()){
            for (MightyPlayer mightyplayer : MightyPlayer.list.values()){
                if (mightyplayer.player != except) {
                    mightyplayer.player.sendMessageToClient(message, false);
                }
            }
        }else{
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()){
                if (player != except) {
                    player.sendMessageToClient(message, false);
                }
            }
        }
    }
    public static void checkQualiEnd(MinecraftServer server){
        if (racingstatus != QUALI || (qualistage != QDURING && qualistage != QENDING)){
            return;
        }
        for (Map.Entry<String, MightyPlayer> listentry : MightyPlayer.list.entrySet()){
            MightyPlayer mightyplayer = listentry.getValue();
            if (mightyplayer.starttime != null){
                return;
            }
        }
        qualistage = QENDED;
        broadcastToDrivers(server,Text.literal(String.format(info_ended,shortcut_quali)),null);
        if (Config.STATS_ENABLE.get()){
            int len = MightyPlayer.list.size();
            for (Map.Entry<String, MightyPlayer> listentry : MightyPlayer.list.entrySet()) {
                MightyPlayer mightyplayer = listentry.getValue();
                Scoreboard scoreboard = server.getScoreboard();
                int scr = MightyDifferences.raceboardGetPlayer(mightyplayer.raceboardname, scoreboard);
                int number = (scr - len) * -1;
                Map<String, Integer> stats = MightyData.getStats((IEntityDataSaver) mightyplayer.player);
                if (number == 1) {
                    stats.put("poles", stats.getOrDefault("poles", 0) + 1);
                }
                int hgp = stats.getOrDefault("highestgridposition", -1);
                if (number < hgp || hgp == -1) {
                    stats.put("highestgridposition", number);
                }
                MightyData.putStats((IEntityDataSaver) mightyplayer.player, stats);
            }
        }
    }
    public static void checkRaceEnd(MinecraftServer server){
        if (racingstatus != RACING || (racestage != RDURING && racestage != RENDING)){
            return;
        }
        for (Map.Entry<String, MightyPlayer> listentry : MightyPlayer.list.entrySet()){
            MightyPlayer mightyplayer = listentry.getValue();
            if (!mightyplayer.finished){
                return;
            }
        }
        racestage = RENDED;
        broadcastToDrivers(server,Text.literal(String.format(info_ended,shortcut_racing)),null);
        int len = MightyPlayer.list.size();
        for (Map.Entry<String, MightyPlayer> listentry : MightyPlayer.list.entrySet()) {
            MightyPlayer mightyplayer = listentry.getValue();
            if (Config.STATS_ENABLE.get()){
                Scoreboard scoreboard = server.getScoreboard();
                int scr = MightyDifferences.raceboardGetPlayer(mightyplayer.raceboardname, scoreboard);
                int number = (scr - len) * -1;
                Map<String, Integer> stats = MightyData.getStats((IEntityDataSaver) mightyplayer.player);
                if (number >= 1 && number <= 3) {
                    stats.put("podiums", stats.getOrDefault("podiums", 0) + 1);
                    if (number == 1) {
                        stats.put("wins", stats.getOrDefault("wins", 0) + 1);
                    } else if (number == 2) {
                        stats.put("secondplaces", stats.getOrDefault("secondplaces", 0) + 1);
                    } else {
                        stats.put("thirdplaces", stats.getOrDefault("thirdplaces", 0) + 1);
                    }
                }
                if (fastest == mightyplayer) {
                    stats.put("fastestlaps", stats.getOrDefault("fastestlaps", 0) + 1);
                }
                int hrf = stats.getOrDefault("highestracefinish", -1);
                if (number < hrf || hrf == -1) {
                    stats.put("highestracefinish", number);
                }
                stats.put("races", stats.getOrDefault("races", 0) + 1);
                MightyData.putStats((IEntityDataSaver) mightyplayer.player, stats);
            }
        }
    }
    private static String cutName(String cuttedname){
        cuttedname = cuttedname.toUpperCase();
        if (cuttedname.length()>3){
            cuttedname = cuttedname.substring(0,3);
        }
        return cuttedname;
    }
    private static String raceboardFormatter(int number, MightyPlayer mightyplayer){
        String output = (number < 10 ? " " : "") + number + CGRAY + " | " + mightyplayer.namecolor + mightyplayer.cuttedname + "      " + CWHITE + CBOLD;
        if (racingstatus == RACING) {
            return output + (mightyplayer.lap < 10 ? "0" : "") + (mightyplayer.lap == -1 ? 0 : mightyplayer.lap) + "l " + (mightyplayer.sector < 10 ? "0" : "") + mightyplayer.sector + "s";
        }else{
            return output + (mightyplayer.besttimes.isEmpty() ? shortcut_notime : mightyplayer.besttimes.get(0).getString());
        }
    }
    private static String statsFormatter(String name, String racename, Map<String, Integer> stats){
        StringBuilder result = new StringBuilder();
        Set<String> valueSet = new LinkedHashSet<>(List.of(Config.STATS_FORMAT.get()));
        result.append(shortcut_field).append("§r\n").append(String.format(info_stats_stats,name)).append("§r\n\n");
        for (String entry : valueSet){
            String text = info_stats.get(entry);
            if (text == null){
                continue;
            }
            if (Objects.equals(entry, "highestracefinish") || Objects.equals(entry, "highestgridposition")){
                int stat = stats.getOrDefault(entry,-1);
                result.append(String.format(text,stat == -1 ? "N/A" : stat));
            }else if (Objects.equals(entry, "racename")) {
                result.append(String.format(text,racename));
            }else{
                result.append(String.format(text,stats.getOrDefault(entry,0)));
            }
            result.append("§r\n");
        }
        result.append(shortcut_field);
        return result.toString();
    }
}
