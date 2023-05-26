package ch.fhnw.iotbricksimulator.util;

import org.junit.jupiter.api.Test;

import static ch.fhnw.iotbricksimulator.util.Constants.*;
import static ch.fhnw.iotbricksimulator.util.Util.toCoordinates;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoordPixelMapTest {

  private static final double PRECISION = 0.0001;

  @Test
  public void testOrigin() {
    Location result = toCoordinates(0, 0);
    Location expected = new Location(BOTTOM_LAT, LEFT_LONG);
    assertEquals(expected.lat(), result.lat(), PRECISION, "origin lat");
    assertEquals(expected.lon(), result.lon(), PRECISION, "origin lon");
  }

  @Test
  public void testTopRightCorner() {
    Location result = toCoordinates(WINDOW_WIDTH, WINDOW_HEIGHT);
    Location expected = new Location(TOP_LAT, RIGHT_LONG);
    assertEquals(expected.lat(), result.lat(), PRECISION, "lat");
    assertEquals(expected.lon(), result.lon(), PRECISION, "lon");
  }

  @Test
  public void testTopLeftCorner() {
    Location result = toCoordinates(0, WINDOW_HEIGHT);
    Location expected = new Location(TOP_LAT, LEFT_LONG);
    assertEquals(expected.lat(), result.lat(), PRECISION, "lat");
    assertEquals(expected.lon(), result.lon(), PRECISION, "lon");
  }

  @Test
  public void testBottomRightCorner() {
    Location result = toCoordinates(WINDOW_WIDTH, 0);
    Location expected = new Location(BOTTOM_LAT, RIGHT_LONG);
    assertEquals(expected.lat(), result.lat(), PRECISION, "lat");
    assertEquals(expected.lon(), result.lon(), PRECISION, "lon");
  }

  @Test
  public void testCenter() {
    Location result = toCoordinates((double) WINDOW_WIDTH / 2, (double) WINDOW_HEIGHT / 2);
    Location expected = new Location(
            BOTTOM_LAT + ((TOP_LAT - BOTTOM_LAT) / 2),
            LEFT_LONG + ((RIGHT_LONG - LEFT_LONG) / 2)
    );
    assertEquals(expected.lat(), result.lat(), PRECISION, "lat");
    assertEquals(expected.lon(), result.lon(), PRECISION, "lon");
  }
}