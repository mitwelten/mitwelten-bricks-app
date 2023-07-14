/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.view.brick;

import ch.fhnw.mitwelten.bricksapp.controller.ApplicationController;
import ch.fhnw.mitwelten.bricksapp.model.brick.impl.ActuatorBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.impl.SensorBrickData;
import javafx.scene.paint.Color;

public abstract class ActuatorPlacement extends BrickPlacement {

  public ActuatorPlacement(ApplicationController controller, ActuatorBrickData brick, Runnable removeMe, Color color) {
    super(controller, brick, removeMe, color);
  }

  public abstract ActuatorBrickData getBrick();

  public abstract void setTargetValue (double value);
}
