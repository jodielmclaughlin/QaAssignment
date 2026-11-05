package com.example.qaassignment.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    public static final String configFile = "config.properties";
    public static final Properties properties = new Properties();
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    static {
        try(InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(configFile)){
            if(input ==null){
                throw new RuntimeException(configFile + " not found");
            }

            properties.load(input);
        } catch(IOException e){
            throw new RuntimeException( "Failed to load " + configFile);
        }
    }

    public static String get(String key) {

        String systemProperty = System.getProperty(key);
        if(systemProperty != null && !systemProperty.isBlank()){
            return systemProperty;
        }
        String envKey = key.replace(".", "_").toUpperCase();
        String envValue = dotenv.get(envKey);

        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return properties.getProperty(key);
    }

    public static String baseUrl() {
        return get("base.url");
    }

    public static String processPath() {
        return get("api.process-path");
    }

    public static String inputFolder() {
        return get("folders.input");
    }

    public static String processingFolder() {
        return get("folders.process");
    }

    public static String encodedFolder() {
        return get("folders.encoded");
    }

    public static String errorFolder() {
        return get("folders.error");
    }



}