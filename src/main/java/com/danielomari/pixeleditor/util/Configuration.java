package com.danielomari.pixeleditor.util;

import java.io.*;
import java.util.Properties;

import static java.lang.System.getProperty;


public final class Configuration {
    private static Configuration instance;
    public Properties properties = new Properties();


    private Configuration() {
        // try & catch is required by FileInputStream.
        // TODO - Add logging
        try {
            File filePath = new File(getConfiguration(), "editor.properties"); // If the file is missing its created.
            if(filePath.exists() && filePath.isFile()) {
                InputStream stream = new FileInputStream(filePath);
                properties.load(stream);
                stream.close();}
        } catch (Exception e) {
            System.out.println("An error occurred during loading configuration.");
            // Log the error
        }
    }

    private File getConfiguration() {
        // Get the configuration folder path
        String configDir = getProperty("user.dir") + "/src/main/java/com/danielomari/pixeleditor/config";
//        System.out.println(configDir);
        return new File(configDir);

    }

    private void updateConfiguration() {
        // Using getConfiguration() to get the configuration folder path and then adding the properties file name.
        File filePath = new File(getConfiguration(), "editor.properties");
        try {
            properties.store(new FileOutputStream(filePath), "Updated successfully!");
        } catch (IOException e) {
            throw new RuntimeException("Error occurred when updating the configuration:", e);
        }
    }


    public Boolean is(String key, Boolean defaultValue) {
        return getValue(key, defaultValue);
    } // Getter of GetValue - there may be a better way to handle this..

    private Boolean getValue(String key, Boolean defaultValue) {
        // Get the value of the boolean, it may be beneficial to change this to handle more than just booleans...
        return Boolean.parseBoolean(properties.getProperty(key, defaultValue.toString()));
    }

    public void getUpdatedConfiguration() { // Getter
        updateConfiguration();
    }


    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    public void updateUI() {
        // Update the UI
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Changes each window to the updated UI
            for (java.awt.Window window : java.awt.Window.getWindows()) {
                javax.swing.SwingUtilities.updateComponentTreeUI(window);
                window.revalidate();
                window.repaint();
            }
        });
    }
}
