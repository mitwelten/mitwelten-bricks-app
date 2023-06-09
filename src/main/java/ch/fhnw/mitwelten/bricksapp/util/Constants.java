/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.util;

public class Constants {
  public static final String REMOVE_KEY = "SHIFT";

  public static final int WINDOW_HEIGHT = 3 * 256;
  public static final int WINDOW_WIDTH  = 3 * 256;

  public static final Location MAP_MIDDLE = new Location(WINDOW_HEIGHT / 2.0, WINDOW_WIDTH / 2.0);

  public static final double BOTTOM_LAT = 47.502823;
  public static final double TOP_LAT    = 47.504214;
  public static final double LEFT_LONG  =  7.605286;
  public static final double RIGHT_LONG =  7.607346;

  public static final String BASE_URL = "brick.li/";

  public static final String CSV_PATH           = "src/main/resources/";
  public static final String FILE_ID_PATH       = "src/main/resources/bricks/id/";
  public static final String FILE_LOCATION_PATH = "src/main/resources/bricks/location/";

  public static final String SIM_ID_PREFIX  = "(sim): ";
  public static final String MQTT_ID_PREFIX = "(mqtt): ";

  public static final int SPAWN_POSITION_X = 400;
  public static final int SPAWN_POSITION_Y = 400;

  public static final int MAX_SENSOR_VALUE = 350;
  public static final String TTN_BASE_URL = "";
}