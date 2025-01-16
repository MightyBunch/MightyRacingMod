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
            return defComment + key + "=" + defaultValue + "\n";
        }
        return defComment + key + "=" + properties.getProperty(key) + "\n";
    }

    private static String propertyReg(String key,Boolean defaultValue){
        String defComment = "# Default: " + defaultValue + "\n";
        if (!properties.containsKey(key) || (!Objects.equals(properties.getProperty(key), "true") && !Objects.equals(properties.getProperty(key), "false"))){
            properties.setProperty(key, String.valueOf(defaultValue));
            return defComment + key + "=" + defaultValue + "\n";
        }
        return defComment + key + "=" + properties.getProperty(key) + "\n";
    }

    private static String propertyReg(String key,Integer defaultValue, Integer min, Integer max){
        String defComment = "# Min: " + min + "\n# Max: " + max + "\n# Default: " + defaultValue + "\n";
        if (!properties.containsKey(key)){
            properties.setProperty(key, String.valueOf(defaultValue));
            return defComment + key + "=" + defaultValue + "\n";
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
        return defComment + key + "=" + intValue + "\n";
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
}