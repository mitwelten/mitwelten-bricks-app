/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.controller;

import ch.fhnw.imvs.bricks.core.ProxyGroup;
import ch.fhnw.mitwelten.bricksapp.model.Garden;
import ch.fhnw.mitwelten.bricksapp.model.brick.BrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.DistanceBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.ServoBrickData;
import ch.fhnw.mitwelten.bricksapp.util.Location;
import ch.fhnw.mitwelten.bricksapp.util.Util;
import ch.fhnw.mitwelten.bricksapp.util.mvcbase.ControllerBase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class BrickController extends ControllerBase<Garden> {

  private final ProxyGroup proxyGroup;


  public BrickController(Garden model) {
    super(model);

    proxyGroup = new ProxyGroup();
    proxyGroup.addProxy(model.mockProxy);
    proxyGroup.addProxy(model.mqttProxy);

    updateLoop();
  }

  private void updateLoop() {
    new Thread(() -> {
      while(true) {
        model.sensors.getValue().forEach(brick ->
            updateModel(set(brick.value, brick.getDistance())));

        Optional<DistanceBrickData> mostActiveSensor = updateMostActiveSensor(model.sensors.getValue());

        proxyGroup.waitForUpdate();

        if(mostActiveSensor.isPresent()){
          model.actuators.getValue().forEach(act -> {
            if(act.getPosition() == act.getTargetPosition()){
              setTargetPosition(act, mostActiveSensor.get());
            }
          });

          model.actuators.getValue().forEach(act ->
              updateModel(set(act.mostActiveAngle, (double) act.getPosition()),
                          set(act.viewPortAngle,   180 + act.getPosition() - act.faceAngle.getValue())
              )
          );
        }
      }
    }).start();
  }

  private boolean allActuatorsReachedPosition(){
    return model.actuators.getValue()
        .stream()
        .map(act -> act.getPosition() == act.getTargetPosition())
        .reduce(true, (acc, cur) -> cur && acc);
  }

  private Optional<DistanceBrickData> updateMostActiveSensor(List<DistanceBrickData> bricks){
    if(bricks.isEmpty()) return Optional.empty();

    Optional<DistanceBrickData> maybeSensor = bricks
        .stream()
        .peek(brick -> updateModel(set(brick.isMostActive, false)))
        .min(Comparator.comparing(DistanceBrickData::getDistance));

    maybeSensor.ifPresent(brick ->
        updateModel(set(maybeSensor.get().isMostActive, true)));

    return maybeSensor;
  }

  private void setTargetPosition(ServoBrickData servo, DistanceBrickData mostActivePlacement) {
    Location mostActive    = mostActivePlacement.location.getValue();
    Location servoLocation = servo.location.getValue();

    double dLat  = mostActive.lat() - servoLocation.lat();
    double dLong = mostActive.lon() - servoLocation.lon();
    double angle = Util.calcAngle(dLong, dLat);
//    int pos      = Util.calculateServoPositionFromAngle(servo, angle);
    servo.setPosition((int) (angle - servo.faceAngle.getValue()));
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
    if(data instanceof ServoBrickData)    removeBrick((ServoBrickData) data);
  }

  private void removeBrick(DistanceBrickData data) {
    List<DistanceBrickData> modified = new ArrayList<>(model.sensors.getValue())
        .stream()
        .filter(b -> !b.getID().equals(data.getID()))
        .toList();
    updateModel(set(model.sensors, modified));
  }

  private void removeBrick(ServoBrickData data) {
    List<ServoBrickData> modified = new ArrayList<>(model.actuators.getValue())
        .stream()
        .filter(b -> !b.getID().equals(data.getID()))
        .toList();
    updateModel(set(model.actuators, modified));
  }
}