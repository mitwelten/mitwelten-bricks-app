/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.model.brick.actuators;

import ch.fhnw.imvs.bricks.actuators.StepperBrick;
import ch.fhnw.mitwelten.bricksapp.model.brick.impl.ActuatorBrickData;
import ch.fhnw.mitwelten.bricksapp.util.Location;

public class MotorBrickData extends ActuatorBrickData {

  private final StepperBrick inner;

  public MotorBrickData(StepperBrick inner, Location location,  double faceAngle) {
    super(inner, location, faceAngle);
    this.inner = inner;
  }

  @Override
  public int getPosition() {
    return inner.getPosition();
  }

  @Override
  public void setPosition(int i) {
    try {
      inner.setPosition(i);
      target = i;
    } catch (IllegalArgumentException e){
      System.err.println("Could not set target position!");
      System.err.println(e.getMessage());
    }
  }

  @Override
  public String toStringFormatted() {
    return super.toStringFormatted()
        + "\nval:\t" + Math.round(value.getValue());
  }

  @Override
  public String toString() {
    return "Stepper" + "," + super.toString();
  }
}