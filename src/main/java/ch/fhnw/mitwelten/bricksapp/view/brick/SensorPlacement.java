/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.view.brick;

import ch.fhnw.mitwelten.bricksapp.controller.ApplicationController;
import ch.fhnw.mitwelten.bricksapp.model.brick.impl.SensorBrickData;

public abstract class SensorPlacement extends BrickPlacement {

  public SensorPlacement(ApplicationController controller, SensorBrickData brick, Runnable removeMe) {
    super(controller, brick, removeMe);
  }

  public abstract SensorBrickData getBrick();

  public abstract void setHighlighted(boolean isHighlighted);

  public abstract void setActivityValue(int value);
}