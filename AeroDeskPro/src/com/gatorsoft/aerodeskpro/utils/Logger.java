package com.gatorsoft.aerodeskpro.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class Logger {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Logger.class.getName());

    static {
        try {
            // Ensure that the 'logs' directory exists
            File logsDir = new File("src/com/gatorsoft/aerodeskpro/logs");
            if (!logsDir.exists()) {
                boolean created = logsDir.mkdirs();  // Create 'logs' directory if it doesn't exist
                if (created) {
                    System.out.println("Logs directory created.");
                } else {
                    System.out.println("Failed to create logs directory.");
                }
            }

            // Create a FileHandler to write logs to the 'app.log' file in the 'logs' folder
            FileHandler fileHandler = new FileHandler("src/com/gatorsoft/aerodeskpro/logs/app.log", true); // true = append mode
            fileHandler.setFormatter(new SimpleFormatter()); // Makes logs readable
            logger.addHandler(fileHandler);

            // Disable default console logging
            logger.setUseParentHandlers(false);  // Only log to the file, no console output

        } catch (IOException e) {
            // If FileHandler fails, log to console
            System.err.println("Failed to set up file logging: " + e.getMessage());
            logger.addHandler(new ConsoleHandler()); // Add console handler as fallback
            logger.log(Level.SEVERE, "Failed to set up file logging", e);
        }
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void error(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }
}
