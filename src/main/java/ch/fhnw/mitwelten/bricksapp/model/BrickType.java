/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.model;

import ch.fhnw.imvs.bricks.actuators.StepperBrick;
import ch.fhnw.imvs.bricks.core.Brick;
import ch.fhnw.imvs.bricks.core.Proxy;
import ch.fhnw.imvs.bricks.sensors.DistanceBrick;
import ch.fhnw.imvs.bricks.sensors.PaxBrick;

import java.util.function.BiFunction;

public enum BrickType {

  DISTANCE("Distance", DistanceBrick::connect),
  STEPPER("Stepper", StepperBrick::connect),
  PAX("Pax", PaxBrick::connect);

  private final String value;
  private final BiFunction<Proxy, String, Brick> create;

  BrickType(String value, BiFunction<Proxy, String, Brick> create){
    this.value = value;
    this.create = create;
  }

  public Brick connect (Proxy proxy, String id){
    return create.apply(proxy, id);
  }

  @Override
  public String toString() {
    return value;
  }
}