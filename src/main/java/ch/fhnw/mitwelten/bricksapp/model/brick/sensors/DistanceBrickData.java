/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.model.brick.sensors;

import ch.fhnw.imvs.bricks.sensors.DistanceBrick;
import ch.fhnw.mitwelten.bricksapp.model.brick.impl.SensorBrickData;
import ch.fhnw.mitwelten.bricksapp.util.Location;
import ch.fhnw.mitwelten.bricksapp.util.mvcbase.ObservableValue;

public class DistanceBrickData extends SensorBrickData {

  private final DistanceBrick inner;

  public DistanceBrickData(DistanceBrick inner, Location location, double faceAngle) {
    super(inner, location, faceAngle);
    this.inner   = inner;
  }

  public int getValue() {
    return inner.getDistance();
  }

  @Override
  public String toStringFormatted() {
    return super.toStringFormatted()
        + "\nval:\t" + value.getValue();
  }

  @Override
  public String toString() {
    return "DistanceBrick" + "," + super.toString();
  }
}