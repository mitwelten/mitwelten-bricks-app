/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.controller;

import ch.fhnw.imvs.bricks.core.ProxyGroup;
import ch.fhnw.mitwelten.bricksapp.model.Garden;
import ch.fhnw.mitwelten.bricksapp.model.brick.BrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.DistanceBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.MotorBrickData;
import ch.fhnw.mitwelten.bricksapp.util.Location;
import ch.fhnw.mitwelten.bricksapp.util.Util;
import ch.fhnw.mitwelten.bricksapp.util.mvcbase.ControllerBase;

import java.util.*;

public class BrickController extends ControllerBase<Garden> {

  private final ProxyGroup proxyGroup;
  private DistanceBrickData mostActive;
  private final Runnable updateLoopThread;
  private boolean stopUpdateLoop = false;

  public BrickController(Garden model) {
    super(model);

    proxyGroup = new ProxyGroup();
    proxyGroup.addProxy(model.mockProxy);
    proxyGroup.addProxy(model.mqttProxy);

    updateLoopThread = initializeUpdateLoop(model);
    addBrickListsListener();
  }

  private Runnable initializeUpdateLoop(Garden model) {
    final Runnable updateLoopThread;

    updateLoopThread = (() -> {
      while(!stopUpdateLoop) {

        // update all sensor values
        model.sensors.getValue().forEach(brick ->
            updateModel(set(brick.value, brick.getDistance())));

        // update most active sensor (acts as target position)
        DistanceBrickData mostActiveSensor = updateMostActiveSensor(model.sensors.getValue());
        mostActive = mostActiveSensor;

        // update actuator target position
        model.actuators.getValue().forEach(act -> {

          // update only the actuators that have reached the last target value
          if(act.getPosition() == act.getTargetPosition()){
            setTargetPosition(act, mostActiveSensor);
          }
        });
        updateActuatorVisualization();

        proxyGroup.waitForUpdate();
      }
    });
    return updateLoopThread;
  }

  private void addBrickListsListener() {
    model.sensors.onChange((old, newList)-> {
      if(old.isEmpty() && !newList.isEmpty()){
        startUpdateLoop();
      } else if(!old.isEmpty() && newList.isEmpty()){
        stopUpdateLoop();
      }
    });
  }

  private void updateActuatorVisualization() {
    model.actuators.getValue().forEach(motor ->
        updateModel(set(motor.mostActiveAngle, (double) motor.getPosition()),
                    set(motor.viewPortAngle,   180 + motor.getPosition() - motor.faceAngle.getValue())
        )
    );
  }

  private void startUpdateLoop() {
    stopUpdateLoop = false;
    new Thread(updateLoopThread).start();
  }

  private void stopUpdateLoop() {
    stopUpdateLoop = true;
  }

  private DistanceBrickData updateMostActiveSensor(List<DistanceBrickData> bricks){
    Optional<DistanceBrickData> maybeSensor = bricks
        .stream()
        .peek(brick -> updateModel(set(brick.isMostActive, false)))
        .min(Comparator.comparing(DistanceBrickData::getDistance));

    if (maybeSensor.isPresent()){
      updateModel(set(maybeSensor.get().isMostActive, true));
      return maybeSensor.get();
    } else {
      return mostActive;
    }
  }

  private void setTargetPosition(MotorBrickData motor, DistanceBrickData mostActivePlacement) {
    Location mostActive    = mostActivePlacement.location.getValue();
    Location motorLocation = motor.location.getValue();

    double dLat   = mostActive.lat() - motorLocation.lat();
    double dLong  = mostActive.lon() - motorLocation.lon();
    double angle  = Util.calcAngle(dLong, dLat);
    double target = Util.absolutToRelativ(motor, angle);

    motor.setPosition((int) target);
  }

  public void move(Location target, BrickData brick){
    updateModel(set(brick.location, target));
  }

  public void rotate(double angle, BrickData brick) {
    updateModel(set(brick.faceAngle, angle));
  }

  public void setRemoveButtonVisible(boolean state){
    updateModel(set(model.removeButtonVisible, state));
  }

  public void removeBrick(BrickData data) {
    if(data instanceof DistanceBrickData) removeBrick((DistanceBrickData) data);
    if(data instanceof MotorBrickData)    removeBrick((MotorBrickData) data);
  }

  private void removeBrick(DistanceBrickData data) {
    List<DistanceBrickData> modified = new ArrayList<>(model.sensors.getValue())
        .stream()
        .filter(b -> !b.getID().equals(data.getID()))
        .toList();
    updateModel(set(model.sensors, modified));
  }

  private void removeBrick(MotorBrickData data) {
    List<MotorBrickData> modified = new ArrayList<>(model.actuators.getValue())
        .stream()
        .filter(b -> !b.getID().equals(data.getID()))
        .toList();
    updateModel(set(model.actuators, modified));
  }

  public void test() {
    if(stopUpdateLoop){
      startUpdateLoop();
    }else {
      stopUpdateLoop();
    }
  }
}