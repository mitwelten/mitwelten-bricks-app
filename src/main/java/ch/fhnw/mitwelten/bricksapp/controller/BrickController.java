/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.controller;

import ch.fhnw.imvs.bricks.core.ProxyGroup;
import ch.fhnw.mitwelten.bricksapp.model.Garden;
import ch.fhnw.mitwelten.bricksapp.model.Notification.Notification;
import ch.fhnw.mitwelten.bricksapp.model.Notification.NotificationType;
import ch.fhnw.mitwelten.bricksapp.model.brick.BrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.DistanceBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.MotorBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.PaxBrickData;
import ch.fhnw.mitwelten.bricksapp.util.Constants;
import ch.fhnw.mitwelten.bricksapp.util.Location;
import ch.fhnw.mitwelten.bricksapp.util.Util;
import ch.fhnw.mitwelten.bricksapp.util.mvcbase.ControllerBase;

import java.util.*;

public class BrickController extends ControllerBase<Garden> {

  private final ProxyGroup proxyGroup;
  private DistanceBrickData mostActive;
  private final Runnable updateLoopThread;

  public BrickController(Garden model) {
    super(model);

    proxyGroup = new ProxyGroup();
    proxyGroup.addProxy(model.mockProxy);
    proxyGroup.addProxy(model.mqttProxy);

    updateLoopThread = initializeUpdateLoop(model);
  }

  private Runnable initializeUpdateLoop(Garden model) {
    final Runnable updateLoopThread;

    updateLoopThread = (() -> {
      while(model.runningUpdateLoop.getValue()) {

        // update all sensor values
        model.distSensors.getValue().forEach(brick ->
            updateModel(set(brick.value, brick.getDistance())));

        model.paxSensors.getValue().forEach(brick ->
            updateModel(set(brick.value, brick.getValue())));

        // update most active sensor (acts as target position)
        DistanceBrickData mostActiveSensor = updateMostActiveSensor(model.distSensors.getValue());
        mostActive = mostActiveSensor;

        // update actuator target position
        model.stepperActuators.getValue().forEach(act -> {

          // update only the actuators that have reached the last target value
          if(act.getPosition() == act.getTargetPosition()){
            setTargetPosition(act, mostActiveSensor == null ? Constants.MAP_MIDDLE : mostActiveSensor.location.getValue());
          }
        });
        updateActuatorVisualization();

        proxyGroup.waitForUpdate();
      }
    });
    return updateLoopThread;
  }

  private void updateActuatorVisualization() {
    model.stepperActuators.getValue().forEach(motor ->
        updateModel(set(motor.mostActiveAngle, (double) motor.getPosition()),
                    set(motor.viewPortAngle,   180 + motor.getPosition() - motor.faceAngle.getValue())
        )
    );
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

  private void setTargetPosition(MotorBrickData motor, Location mostActiveLocation) {
    Location motorLocation = motor.location.getValue();

    double dLat   = mostActiveLocation.lat() - motorLocation.lat();
    double dLong  = mostActiveLocation.lon() - motorLocation.lon();
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


  public void toggleUpdateLoop(){
    if (!model.runningUpdateLoop.getValue()){
      updateModel(set(model.runningUpdateLoop, true));
      new Thread(updateLoopThread).start();
    } else {
      updateModel(set(model.runningUpdateLoop, false));
    }
  }

  private void createNotification(NotificationType type, String title, String message) {
    Notification newNotification = new Notification(type, title, message);
    Deque<Notification> queue    = new ArrayDeque<>(model.notifications.getValue());
    queue.push(newNotification);
    updateModel(set(
        model.notifications,
        queue
    ));
  }

  public void functionTest(MotorBrickData brick, int[] positions) {
    new Thread( () -> {

      boolean prevUpdateLoopState = model.runningUpdateLoop.getValue();
      if(prevUpdateLoopState) {
        toggleUpdateLoop();
      }

      long startTime = System.currentTimeMillis();
      boolean testFailed = true;

      for (int pos : positions) {
        brick.setPosition(pos);

        while(brick.getPosition() != pos) {
          proxyGroup.waitForUpdate();
          updateActuatorVisualization();
          try {
            Thread.sleep(500);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }

        long duration = System.currentTimeMillis() - startTime ;
        if(duration > 10_000){
          break;
        }
        testFailed = false;
      }

      if(!testFailed){
        long duration = System.currentTimeMillis() - startTime ;
        createNotification(NotificationType.CONFIRMATION, "Function Test", "Test successful - took " + duration + "ms");
      }

      if(prevUpdateLoopState){
        toggleUpdateLoop();
      }
    }).start();
  }
}