package ch.fhnw.iotbricksimulator.util;

public class Constants {
  public static final String REMOVE_KEY = "SHIFT";

  public static final int WINDOW_HEIGHT = 3 * 256;
  public static final int WINDOW_WIDTH  = 3 * 256;

  public static final double BOTTOM_LAT = 47.502823;
  public static final double TOP_LAT    = 47.504214;
  public static final double LEFT_LONG  =  7.605286;
  public static final double RIGHT_LONG =  7.607346;

  public static final String BASE_URL = "brick.li/";

  public static final String CSV_PATH = "src/resources";

  public static final String MOCK_ID_PREFIX   = "(mock): ";
  public static final String MQTT_ID_PREFIX   = "(mqtt): ";

  public static final int SPAWN_POSITION_X = 400;
  public static final int SPAWN_POSITION_Y = 400;

  public static final int MAX_SENSOR_VALUE = 350;
}