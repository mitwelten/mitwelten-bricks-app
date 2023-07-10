/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.view.brick;

import ch.fhnw.mitwelten.bricksapp.controller.ApplicationController;
import ch.fhnw.mitwelten.bricksapp.model.brick.BrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.impl.SensorBrickData;

public abstract class SensorPlacement extends BrickPlacement {

  private SensorBrickData brickData;

  public SensorPlacement(ApplicationController controller, SensorBrickData brick, Runnable removeMe) {
    super(controller, brick, removeMe);
    this.brickData = brick;
  }

  public abstract void setHighlighted(boolean isHighlighted);

  public abstract void setActivityValue(int value);

  @Override
  public SensorBrickData getBrick() {
    return brickData;
  }

  @Override
  public void setRotateBrickSymbol(double angel) {

  }
}
