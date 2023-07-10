/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.model.brick.impl;

import ch.fhnw.imvs.bricks.core.Brick;
import ch.fhnw.mitwelten.bricksapp.model.brick.BrickData;
import ch.fhnw.mitwelten.bricksapp.util.Location;
import ch.fhnw.mitwelten.bricksapp.util.mvcbase.ObservableValue;

public abstract class SensorBrickData extends BrickData {

  public final ObservableValue<Integer> value;
  public final ObservableValue<Boolean> isMostActive;

  public SensorBrickData(Brick inner, Location location, double faceAngle) {
    super(inner, location, faceAngle);
    value        = new ObservableValue<>(0);
    isMostActive = new ObservableValue<>(false);
  }

  public abstract int getValue();
}