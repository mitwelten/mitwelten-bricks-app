/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License

 Based on: https://github.com/Pi4J/pi4j-template-javafx
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under Apache License 2.0
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.util.mvcbase;

import java.util.logging.Logger;

public final class MvcLogger {
    private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public void logInfo(String msg) {
        logger.info(() -> msg);
    }

    public void logError(String msg) {
        logger.severe(() -> msg);
    }

    public void logConfig(String msg) {
        logger.config(() -> msg);
    }

    public void logDebug(String msg) {
        logger.fine(() -> msg);
    }
}
