package com.mightyracing.config;

import com.mightyracing.MightyRacingMod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class ConfigBase<V> {
    private V value;
    private final String key;
    private final String comment;
    private int min;
    private int max;
    private String[] accepted;
    private float fmin;
    private float fmax;
    private final static List<ConfigBase<?>> configList = new ArrayList<>();
    //boolean
    public ConfigBase(String key, String comment, V value){
        this.key = key;
        this.comment = comment;
        this.value = value;
        configList.add(this);
    }
    //int
    public ConfigBase(String key, String comment, V value, int min, int max){
        this.key = key;
        this.comment = comment;
        this.value = value;
        this.min = min;
        this.max = max;
        configList.add(this);
    }
    //string
    public ConfigBase(String key, String comment, V value, String[] accepted){
        this.key = key;
        this.comment = comment;
        this.value = value;
        this.accepted = accepted;
        configList.add(this);
    }
    //float
    public ConfigBase(String key, String comment, V value, float min, float max){
        this.key = key;
        this.comment = comment;
        this.value = value;
        this.fmin = min;
        this.fmax = max;
        configList.add(this);
    }
    public V get(){
        return this.value;
    }
    public void set(V value){
        this.value = value;
    }
    public static void register(String path){
        Properties properties = new Properties();
        File file = new File(path);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                properties.load(reader);
            } catch (Exception e) {
                MightyRacingMod.LOGGER.error("Error with reading config file!");
            }
        }

        String provider = getProvider(properties);

        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)){
            writer.write(provider);
        }catch (Exception e){
            MightyRacingMod.LOGGER.error("Error with writing to config file!");
        }
    }
    private static String getProvider(Properties properties){
        StringBuilder provider = new StringBuilder();
        for (ConfigBase<?> entry : configList){
            String key = entry.key;
            String comment = entry.comment;
            if (entry.get() instanceof Boolean val){
                ConfigBase<Boolean> config = (ConfigBase<Boolean>) entry;
                String text = "# " + comment + "\n" + "# Default: " + val + "\n" + key + "=";
                if (properties.containsKey(key) && (Objects.equals(properties.getProperty(key), "true") || Objects.equals(properties.getProperty(key), "false"))){
                    val = Boolean.parseBoolean(properties.getProperty(key));
                    config.set(val);
                }
                provider.append(text).append(val);
            }else if (entry.get() instanceof Integer val){
                int min = entry.min;
                int max = entry.max;
                ConfigBase<Integer> config = (ConfigBase<Integer>) entry;
                String text = "# " + comment + "\n" + "# Min: " + min + "\n# Max: " + max + "\n# Default: " + val + "\n" + key + "=";
                int def = val;
                if (properties.containsKey(key)){
                    try {
                        val = Integer.parseInt(properties.getProperty(key));
                        if (val < min || val > max){
                            val = def;
                        }else{
                            config.set(val);
                        }
                    } catch (Exception ignored) {
                    }
                }
                provider.append(text).append(val);
            }else if (entry.get() instanceof String val){
                String[] accepted = entry.accepted;
                ConfigBase<String> config = (ConfigBase<String>) entry;
                String acceptString = String.join(", ", accepted);
                String text = "# " + comment + "\n" + (acceptString.isEmpty() ? "" : "# Accepted values: " + acceptString + "\n") + "# Default: " + val + "\n" + key + "=";
                if (properties.containsKey(key) && (accepted.length == 0 || Set.of(accepted).contains(properties.getProperty(key)))){
                    val = properties.getProperty(key);
                    config.set(val);
                }
                provider.append(text).append(val);
            }else if (entry.get() instanceof String[] val){
                String[] accepted = entry.accepted;
                ConfigBase<String[]> config = (ConfigBase<String[]>) entry;
                String acceptString = String.join(", ", accepted);
                String valString = String.join(", ", val);
                String text = "# " + comment + "\n" + (acceptString.isEmpty() ? "" : "# Accepted values: " + acceptString + "\n") + "# Default: " + valString + "\n" + key + "=";
                if (properties.containsKey(key) && (accepted.length == 0 || Set.of(accepted).containsAll(Set.of(properties.getProperty(key).replace(" ","").split(","))))){
                    valString = properties.getProperty(key);
                    val = valString.replace(" ","").split(",");
                    config.set(val);
                }
                provider.append(text).append(valString);
            }else if (entry.get() instanceof Float val){
                float min = entry.fmin;
                float max = entry.fmax;
                ConfigBase<Float> config = (ConfigBase<Float>) entry;
                String text = "# " + comment + "\n" + "# Min: " + min + "\n# Max: " + max + "\n# Default: " + val + "\n" + key + "=";
                float def = val;
                if (properties.containsKey(key)){
                    try {
                        val = Float.parseFloat(properties.getProperty(key));
                        if (val < min || val > max){
                            val = def;
                        }else{
                            config.set(val);
                        }
                    } catch (Exception ignored) {
                    }
                }
                provider.append(text).append(val);
            }
            provider.append("\n\n");
        }
        while (!provider.isEmpty() && provider.charAt(provider.length() - 1) == '\n') {
            provider.deleteCharAt(provider.length() - 1);
        }
        return provider.toString();
    }
}
