package ch.fhnw.mitwelten.bricksapp.util;

import org.junit.jupiter.api.Test;

import static ch.fhnw.mitwelten.bricksapp.util.Util.calcAngle;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PositionTest {

  private static final double PRECISION = 0.005;

  @Test
  public void testTypicalPointsInEachQuadrant() {
    assertEquals( 90 - 45, calcAngle( 5.0,  5.0), PRECISION, "first");
    assertEquals(360 - 45, calcAngle(-5.0,  5.0), PRECISION, "second");
    assertEquals(180 + 45, calcAngle(-5.0, -5.0), PRECISION, "third");
    assertEquals( 90 + 45, calcAngle( 5.0, -5.0), PRECISION, "fourth");
  }

  @Test
  public void testShiftedPointsInEachQuadrant() {
    assertEquals(90 - 75.963, calcAngle( 1.0,  4.0), PRECISION, "first");
    assertEquals(90 - 51.340, calcAngle( 4.0,  5.0), PRECISION, "first");
    assertEquals(90 - 38.659, calcAngle( 5.0,  4.0), PRECISION, "first");

    assertEquals(270 + 75.963, calcAngle(-1.0,  4.0), PRECISION, "second");
    assertEquals(270 + 51.340, calcAngle(-4.0,  5.0), PRECISION, "second");
    assertEquals(270 + 38.659, calcAngle(-5.0,  4.0), PRECISION, "second");

    assertEquals(270 - 75.963, calcAngle(-1.0, -4.0), PRECISION, "third");
    assertEquals(270 - 51.340, calcAngle(-4.0, -5.0), PRECISION, "third");
    assertEquals(270 - 38.659, calcAngle(-5.0, -4.0), PRECISION, "third");

    assertEquals(90 + 75.963, calcAngle( 1.0, -4.0), PRECISION, "fourth");
    assertEquals(90 + 51.340, calcAngle( 4.0, -5.0), PRECISION, "fourth");
    assertEquals(90 + 38.659, calcAngle( 5.0, -4.0), PRECISION, "fourth");
  }

  @Test
  public void testPointsOnAxis() {
    assertEquals(  0, calcAngle( 0.0,  5.0), PRECISION, "north"); // 90
    assertEquals(180, calcAngle( 0.0, -5.0), PRECISION, "south"); // -90
    assertEquals( 90, calcAngle( 5.0,  0.0), PRECISION, "east"); // 0
    assertEquals(270, calcAngle(-5.0,  0.0), PRECISION, "west"); // 0
  }




  @Test
  public void testFromToCoordinates() {
    double x = 12.0;
    double y = 7.0;
    Location testLocation1 = Util.toCoordinates(x, y);
    Location testLocation2 = Util.fromCoordinates(testLocation1);

    assertEquals(testLocation2.lat(), x, PRECISION, "from to lat"); // 90
    assertEquals(testLocation2.lon(), y, PRECISION, "from to lon"); // 90
  }
}