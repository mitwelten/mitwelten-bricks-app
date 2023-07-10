/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.model.brick;

import ch.fhnw.imvs.bricks.core.Brick;
import ch.fhnw.mitwelten.bricksapp.util.Constants;
import ch.fhnw.mitwelten.bricksapp.util.Util;
import ch.fhnw.mitwelten.bricksapp.util.mvcbase.ObservableValue;
import ch.fhnw.mitwelten.bricksapp.util.Location;

import java.util.Date;

public abstract class BrickData {

  public final ObservableValue<Location> location;
  public final ObservableValue<Double>   faceAngle;

  private final Brick inner;

  public BrickData(Brick inner){
    this.location   = new ObservableValue<>(new Location(Constants.SPAWN_POSITION_X, Constants.SPAWN_POSITION_Y));
    this.faceAngle  = new ObservableValue<>(0.0);
    this.inner = inner;
  }

  public BrickData(Brick inner, Location location, double faceAngle){
    this.location   = new ObservableValue<>(location);
    this.faceAngle  = new ObservableValue<>(faceAngle);
    this.inner = inner;
  }

  public String getID() {
    return inner.getID();
  }

  public double getBatteryVoltage() {
    return inner.getBatteryVoltage();
  }

  public Date getTimestamp() {
    return inner.getTimestamp();
  }

  public String getTimestampIsoUtc() {
    return inner.getTimestampIsoUtc();
  }

  public String toStringFormatted() {
    String id = inner.getID();
    if (id.length() > 12) id = "..." + id.substring(id.length() - 12);
    Location coordinates = Util.toCoordinates(location.getValue().lon(), location.getValue().lat());
    return id +
//        "\nx: "   + brick.location.getValue().lon() +
//        "\ny: "   + brick.location.getValue().lat() +
        "\nx:\t"   + coordinates.lat() +
        "\ny:\t"   + coordinates.lon() +
        "\nfa:\t"  + faceAngle.getValue();
  }

  @Override
  public String toString() {
    return inner.getID()
        + "," + location.getValue().lat()
        + "," + location.getValue().lon()
        + "," + faceAngle.getValue();
  }
}