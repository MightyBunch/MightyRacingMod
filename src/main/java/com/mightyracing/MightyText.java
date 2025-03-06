package com.mightyracing;

import java.util.HashMap;
import java.util.Map;

public class MightyText {
    public static String shortcut_offline = "§c§lOFFLINE";
    public static String shortcut_practice = "§e§lFREE PRACTICE";
    public static String shortcut_quali = "§2§lQUALIFICATIONГОВНО";
    public static String shortcut_racing = "§4§lGRAND PRIX";
    public static String shortcut_driver = "§lDRIVER";
    public static String shortcut_normal = "§lNORMAL";
    public static String shortcut_notime = "No time";
    public static String shortcut_field = "§8============================";
    public static String info_racestatus_switch = "Race status switched to %s§r.";
    public static String info_status_switch = "Your status switched to %s§r.";
    public static String info_time_personal = "Your new best time is: %s§r.";
    public static String info_time_bad = "Your time is: %s§r.";
    public static String info_time_fastest = "New fastest lap: %s§r.";
    public static String info_time_reset = "Your time on track %s§r has been reset.";
    public static String info_race_finish = "You finished the race!";
    public static String info_name_change = "Your racename has changed to %s§r.";
    public static String info_ended = "%s§r is over!";
    public static String info_pitstops_completed = "Drivers who failed to make required pit stops: %s§r.";
    public static String info_durability_zero = "%s§r's vehicle durability has reached zero.";
    public static Map<String, String> info_stats = new HashMap<>();
    public static String info_stats_stats = "%s's statistics:";
    public static String error_name_short = "Your racename has to contain at least 3 symbols.";
    public static String error_name_condition = "You can't change your racename when you are a %s§r and racestatus is not %s§r.";
    public static String error_status_during = "You can't change status during %s§r.";

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
