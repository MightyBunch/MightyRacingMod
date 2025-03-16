package com.mightyracing.config;

public class Config {
    public static ConfigBase<Boolean> AUTO_FINISH = new ConfigBase<>(
            "auto_finish",
            "Setting this to false allows lapped drivers to complete all their laps, even after another driver finishes.",
            true
    );
    public static ConfigBase<Integer> CHECKPOINT_PRECISION = new ConfigBase<>(
            "checkpoint_precision",
            "This setting determines how many checkpoints can be skipped for the next one to count. A value of 0 means all previous checkpoints must be passed.",
            0,
            0,
            10
    );
    public static ConfigBase<Boolean> BROADCAST_ONLY_TO_DRIVERS = new ConfigBase<>(
            "broadcast_only_to_drivers",
            "This setting controls whether broadcast messages (e.g. new fastest lap or racestatus changes) are sent to all players or only drivers.",
            true
    );
    public static ConfigBase<Boolean> SEND_FEEDBACK = new ConfigBase<>(
            "send_feedback",
            "This setting controls whether feedback messages are sent when using commands.",
            true
    );
    public static ConfigBase<Boolean> STATS_ENABLE = new ConfigBase<>(
            "stats_enable",
            "This setting allows you to disable driver stats.",
            true
    );
    public static ConfigBase<String[]> STATS_FORMAT = new ConfigBase<>(
            "stats_format",
            "This setting defines what will be displayed when using the mightyracing stats command.",
            new String[]{"racename", "wins", "podiums", "fastestlaps", "races", "poles"},
            new String[]{"racename", "wins", "secondplaces", "thirdplaces", "podiums", "fastestlaps", "races", "poles", "highestracefinish", "highestgridposition"}
    );
    public static ConfigBase<Boolean> DESTROY_VEHICLE = new ConfigBase<>(
            "destroy_vehicle",
            "================DURABILITY SYSTEM================\n\n# This setting determines whether the driver's vehicle (if they have one) will be destroyed when durability reaches zero.",
            true
    );
    public static ConfigBase<Float> DEFAULT_MODIF = new ConfigBase<>(
            "default_modif",
            "This setting controls how much speed influences overall durability reduction.",
            100f,
            0f,
            1000f
    );
    public static ConfigBase<Float> DEFAULT_POWER = new ConfigBase<>(
            "default_power",
            "This setting controls how linearly durability decreases relative to speed. A value of 1 means a linear relationship.",
            0.8f,
            0f,
            10f
    );
    public static ConfigBase<Float> SPECIAL_MODIF = new ConfigBase<>(
            "special_modif",
            "This setting controls how much speed influences overall durability reduction when the driver is on the blocks listed below.",
            2f,
            0f,
            1000f
    );
    public static ConfigBase<Float> SPECIAL_POWER = new ConfigBase<>(
            "special_power",
            "This setting controls how linearly durability decreases relative to speed when the driver is on the blocks listed below. A value of 1 means a linear relationship.",
            3f,
            0f,
            10f
    );
    public static ConfigBase<String[]> SPECIAL_BLOCKS = new ConfigBase<>(
            "special_blocks",
            "This setting defines the blocks for the above settings.",
            new String[]{"minecraft:ice", "minecraft:packed_ice", "minecraft:blue_ice", "minecraft:air"},
            new String[]{}
    );

    public static void reg(){
        ConfigBase.register("config/mightyracing.properties");
    }
}
