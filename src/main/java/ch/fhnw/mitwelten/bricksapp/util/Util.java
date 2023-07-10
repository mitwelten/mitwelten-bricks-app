/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.util;

import ch.fhnw.mitwelten.bricksapp.model.brick.BrickData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ch.fhnw.mitwelten.bricksapp.util.Constants.*;

public class Util {


  /**
   * 0   45
   * | /
   * 270 * 90
   * |
   * 180
   *
   * @param dLat  latitude (Breitengrad)
   * @param dLong longitude (Längengrad)
   * @return degree
   */
  public static double calcAngle(double dLong, double dLat) {
    double degrees = Math.toDegrees(Math.atan(dLat / dLong));

    // on x-axis
    if (dLat == 0) {
      // west
      if (dLong >= 0) {
        return 90;
      }
      // east
      return 270;
    }

    // on y-axis
    if (dLong == 0) {
      // north
      if (dLat >= 0) {
        return 0;
      }
      return 180;
    }

    // first quadrant
    if (dLat >= 0 && dLong >= 0) {
      return 90 - degrees;
    }

    // second quadrant
    if (dLat > 0 && dLong < 0) {
      return 270 - degrees;
    }

    // third quadrant
    if (dLat < 0 && dLong < 0) {
      return 180 + (90 - degrees);
    }

    // fourth quadrant
    if (dLat < 0 && dLong >= 0) {
      return 90 - degrees;
    }

    throw new IllegalArgumentException("Angle could not be calculated");
  }

  public static int calculateServoPositionFromAngle(BrickData brick, double angle) {
    double result = angle - brick.faceAngle.getValue() + 90;
    if (result < 0) {
      result += 360.0;
    }
    return (int) result;
  }

  public static double absolutToRelativ(BrickData brick, double angle){
    return (360 - (brick.faceAngle.getValue() - angle)) % 360;
  }

  public static Location toCoordinates(double x, double y) {
    double lon = (LEFT_LONG + ((RIGHT_LONG - LEFT_LONG) / WINDOW_WIDTH) * x);
    double lat = (BOTTOM_LAT + ((TOP_LAT - BOTTOM_LAT) / WINDOW_HEIGHT) * y);

    double decimalFactor = 10e4;
    double roundedLat = Math.round(lat * decimalFactor) / decimalFactor;
    double roundedLon = Math.round(lon * decimalFactor) / decimalFactor;

    return new Location(roundedLat, roundedLon);
  }

//  public static Location fromLocation(Location location) {
//    double lon = (LEFT_LONG + ((RIGHT_LONG - LEFT_LONG) / WINDOW_WIDTH) * x);
//    double lat = (BOTTOM_LAT + ((TOP_LAT - BOTTOM_LAT) / WINDOW_HEIGHT) * y);
//
//    double decimalFactor = 10e4;
//    double roundedLat = Math.round(lat * decimalFactor) / decimalFactor;
//    double roundedLon = Math.round(lon * decimalFactor) / decimalFactor;
//
//    return new Location(roundedLat, roundedLon);
//  }

  public static String getTimeStamp() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    return dtf.format(now);
  }


  public static Location calcSpawnPosition(double spiralValue) {
    // archimedic spiral formula: x(t) = at cos(t), y(t) = at sin(t)
    double a = 10;
    double offset = (double) Constants.WINDOW_HEIGHT / 2;
    double t = spiralValue;
    double x = a * t * Math.cos(t);
    double y = a * t * Math.sin(t);
    return new Location(x + offset, y + offset);
  }
}