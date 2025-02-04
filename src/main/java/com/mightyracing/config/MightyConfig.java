package com.mightyracing.config;

import com.mightyracing.MightyRacingMod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

public class MightyConfig {
    private static final String CONFIG_PATH = "config/mightyracing.properties";
    private static final Properties properties = new Properties();

    public static String AUTO_FINISH = "auto_finish";
    public static String CHECKPOINT_PRECISION = "checkpoint_precision";
    public static String BROADCAST_ONLY_TO_DRIVERS = "broadcast_only_to_drivers";
    public static String DESTROY_VEHICLE = "destroy_vehicle";
    public static String DEFAULT_MODIF = "default_modif";
    public static String DEFAULT_POWER = "default_power";
    public static String SPECIAL_MODIF = "special_modif";
    public static String SPECIAL_POWER = "special_power";
    public static String SPECIAL_BLOCKS = "special_blocks";

    public static void register() {
        File file = new File(CONFIG_PATH);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                properties.load(reader);
            } catch (Exception e) {
                MightyRacingMod.LOGGER.error("Error with reading config file!");
            }
        }

        String provider = getProvider();

        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)){
            writer.write(provider);
        }catch (Exception e){
            MightyRacingMod.LOGGER.error("Error with writing to config file!");
        }
    }

    private static String getProvider(){
        String provider = "";
        provider += "# Setting this to false allows lapped drivers to complete all their laps, even after another driver finishes.\n";
        provider += propertyReg(AUTO_FINISH,true);
        provider += "# This setting determines how many checkpoints can be skipped for the next one to count. A value of 0 means all previous checkpoints must be passed.\n";
        provider += propertyReg(CHECKPOINT_PRECISION,0,0,10);
        provider += "# This setting controls whether broadcast messages (e.g. new fastest lap or racestatus changes) are sent to all players or only drivers.\n";
        provider += propertyReg(BROADCAST_ONLY_TO_DRIVERS,true);
        provider += "# ================DURABILITY SYSTEM================\n\n";
        provider += "# This setting determines whether the driver's vehicle (if they have one) will be destroyed when durability reaches zero.\n";
        provider += propertyReg(DESTROY_VEHICLE,true);
        provider += "# This setting controls how much speed influences overall durability reduction.\n";
        provider += propertyReg(DEFAULT_MODIF,100f,0f,1000f);
        provider += "# This setting controls how linearly durability decreases relative to speed. A value of 1 means a linear relationship.\n";
        provider += propertyReg(DEFAULT_POWER,0.8f,0f,10f);
        provider += "# This setting controls how much speed influences overall durability reduction when the driver is on the blocks listed below.\n";
        provider += propertyReg(SPECIAL_MODIF,2f,0f,1000f);
        provider += "# This setting controls how linearly durability decreases relative to speed when the driver is on the blocks listed below. A value of 1 means a linear relationship.\n";
        provider += propertyReg(SPECIAL_POWER,3f,0f,10f);
        provider += "# This setting defines the blocks for the above settings.\n";
        provider += propertyReg(SPECIAL_BLOCKS,"minecraft:ice, minecraft:packed_ice, minecraft:blue_ice, minecraft:air",new String[]{});


        while (provider.endsWith("\n")) {
            provider = provider.substring(0, provider.length() - 1);
        }
        return provider;
    }

    private static String propertyReg(String key, String defaultValue, String[] accepted){
        String acceptString = String.join(", ", accepted);
        String defComment = (acceptString.isEmpty() ? "" : "# Accepted values: " + acceptString + "\n") + "# Default: " + defaultValue + "\n";
        if (!properties.containsKey(key) || (accepted.length != 0 && !Arrays.asList(accepted).contains(properties.getProperty(key)))){
            properties.setProperty(key,defaultValue);
            return defComment + key + "=" + defaultValue + "\n\n";
        }
        return defComment + key + "=" + properties.getProperty(key) + "\n\n";
    }

    private static String propertyReg(String key,Boolean defaultValue){
        String defComment = "# Default: " + defaultValue + "\n";
        if (!properties.containsKey(key) || (!Objects.equals(properties.getProperty(key), "true") && !Objects.equals(properties.getProperty(key), "false"))){
            properties.setProperty(key, String.valueOf(defaultValue));
            return defComment + key + "=" + defaultValue + "\n\n";
        }
        return defComment + key + "=" + properties.getProperty(key) + "\n\n";
    }

    private static String propertyReg(String key,Integer defaultValue, Integer min, Integer max){
        String defComment = "# Min: " + min + "\n# Max: " + max + "\n# Default: " + defaultValue + "\n";
        if (!properties.containsKey(key)){
            properties.setProperty(key, String.valueOf(defaultValue));
            return defComment + key + "=" + defaultValue + "\n\n";
        }
        Integer intValue = defaultValue;
        try {
            intValue = Integer.parseInt(properties.getProperty(key));
            if (intValue < min || intValue > max){
                intValue = defaultValue;
                properties.setProperty(key, String.valueOf(defaultValue));
            }
        } catch (Exception ignored) {
            properties.setProperty(key, String.valueOf(defaultValue));
        }
        return defComment + key + "=" + intValue + "\n\n";
    }

    private static String propertyReg(String key,Float defaultValue, Float min, Float max){
        String defComment = "# Min: " + min + "\n# Max: " + max + "\n# Default: " + defaultValue + "\n";
        if (!properties.containsKey(key)){
            properties.setProperty(key, String.valueOf(defaultValue));
            return defComment + key + "=" + defaultValue + "\n\n";
        }
        Float floatValue = defaultValue;
        try {
            floatValue = Float.parseFloat(properties.getProperty(key));
            if (floatValue < min || floatValue > max){
                floatValue = defaultValue;
                properties.setProperty(key, String.valueOf(defaultValue));
            }
        } catch (Exception ignored) {
            properties.setProperty(key, String.valueOf(defaultValue));
        }
        return defComment + key + "=" + floatValue + "\n\n";
    }

    public static String getString(String key){
        return properties.getProperty(key);
    }

    public static Boolean getBoolean(String key){
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    public static Integer getInteger(String key){
        return Integer.parseInt(properties.getProperty(key));
    }

    public static Float getFloat(String key){
        return Float.parseFloat(properties.getProperty(key));
    }
}