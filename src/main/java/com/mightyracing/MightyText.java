package com.mightyracing;

import java.util.HashMap;
import java.util.Map;

public class MightyText {
    public static String shortcut_offline = "§c§lOFFLINE";
    public static String shortcut_practice = "§e§lFREE PRACTICE";
    public static String shortcut_quali = "§2§lQUALIFICATION";
    public static String shortcut_racing = "§4§lGRAND PRIX";
    public static String shortcut_driver = "§lDRIVER";
    public static String shortcut_normal = "§lNORMAL";
    public static String shortcut_notime = "No time";
    public static String shortcut_field = "§8============================";
    public static String info_racestatus_switch = "Race status switched to %s§r.";
    public static String info_racestatus_now = "Current race status is %s§r.";
    public static String info_racestatus_track = "Track: %s§r.";
    public static String info_racestatus_duration = "Duration: %s§r minutes.";
    public static String info_racestatus_laps = "Laps: %s§r.";
    public static String info_racestatus_pitstops = "Required pit stops: %s§r.";
    public static String info_racestatus_durability = "Max durability: %s§r.";
    public static String info_status_switch = "Your status switched to %s§r.";
    public static String info_time_personal = "Your new best time is: %s§r.";
    public static String info_time_bad = "Your time is: %s§r.";
    public static String info_time_fastest = "New fastest lap: %s§r.";
    public static String info_time_reset = "Your time on track %s§r has been reset.";
    public static String info_stats_reset = "Your stats has been reset.";
    public static String info_race_finish = "You finished the race!";
    public static String info_name = "Your racing name: %s§r.";
    public static String info_name_no = "You don't have a racing name yet.";
    public static String info_name_change = "Your racing name has changed to %s§r.";
    public static String info_ended = "%s§r is over!";
    public static String info_pitstops_completed = "%s§r failed to make required number of pit stops.";
    public static String info_durability_zero = "%s§r's vehicle durability has reached zero.";
    public static Map<String, String> info_stats = new HashMap<>();
    public static String info_stats_stats = "%s's statistics:";
    public static String error_name_short = "Your racename has to contain at least 3 symbols.";
    public static String error_name_condition = "You can't change your racename when you are a %s§r and racestatus is not %s§r.";
    public static String error_status_during = "You can't change player's status during %s§r.";
    public static String error_illegal_trackname = "Illegal trackname.";
    public static String error_start = "%s§r cannot be started because it is either already started or not ready to begin.";
    public static String error_notplayer = "This command can only be executed by a player.";
    public static String error_noposition = "You need to set at least one position before generating a selector.";
    public static String feedback_time_reset = "%s's time on track %s§r has been reset.";
    public static String feedback_stats_reset = "%s's stats has been reset.";
    public static String feedback_status_switch = "%s's status switched to %s§r.";
    public static String feedback_start = "%s§r started.";
    public static String tooltip_generate = "Click to generate the selector.";
    public static String tooltip_pos1 = "Click to set the first position.";
    public static String tooltip_pos2 = "Click to set the second position.";
    public static String tooltip_copy = "§2Click to copy the selector.";
    public static String tooltip_clear = "Click to clear the selection.";
    public static String button_pos1 = "§6[POS1]§r";
    public static String button_pos2 = "§e[POS2]§r";
    public static String button_generate = "§d[GENERATE]§r";
    public static String button_clear = "§c[CLEAR]§r";

    static {
        info_stats.put("racename", "Racename: %s");
        info_stats.put("wins", "§eWins: %s");
        info_stats.put("secondplaces", "§7Second Places: %s");
        info_stats.put("thirdplaces", "§6Third Places: %s");
        info_stats.put("poles", "Poles: %s");
        info_stats.put("podiums", "Podiums: %s");
        info_stats.put("races", "Races: %s");
        info_stats.put("fastestlaps", "§dFastest Laps: %s");
        info_stats.put("highestracefinish", "Highest race finish: %s");
        info_stats.put("highestgridposition", "Highest grid position: %s");
    }
}
