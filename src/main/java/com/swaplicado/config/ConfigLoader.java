/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.swaplicado.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Edwin Carmona
 */
public class ConfigLoader {

    private Properties properties = new Properties();

    public ConfigLoader() {
        String configFilePath = "cloudstorage.properties";
        try (InputStream input = new FileInputStream(configFilePath)) {
            // Carga el archivo de propiedades
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getProjectId() {
        return properties.getProperty("project.id");
    }

    public String getBucketName() {
        return properties.getProperty("bucket.name");
    }
    
    public long getUrlExpirationTimeMinutes() {
        return Long.parseLong(properties.getProperty("url.expirationtime.minutes", "30"));
    }

    public static void main(String[] args) {
        ConfigLoader config = new ConfigLoader();
        System.out.println("Project ID: " + config.getProjectId());
        System.out.println("Bucket Name: " + config.getBucketName());
    }
}