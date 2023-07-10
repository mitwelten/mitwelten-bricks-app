/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.controller;

import ch.fhnw.mitwelten.bricksapp.model.Garden;
import ch.fhnw.mitwelten.bricksapp.model.brick.BrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.DistanceBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.MotorBrickData;
import ch.fhnw.mitwelten.bricksapp.util.Location;
import ch.fhnw.mitwelten.bricksapp.util.mvcbase.ControllerBase;

import java.io.File;

public class ApplicationController extends ControllerBase<Garden> {
  private final BrickController brickController;
  private final MenuController menuController;

  public ApplicationController(Garden model) {
    super(model);
    brickController = new BrickController(model);
    menuController  = new MenuController(model);
  }

  @Override
  public void awaitCompletion() {
    super.awaitCompletion();
    brickController.awaitCompletion();
    menuController .awaitCompletion();
  }

  @Override
  public void shutdown() {
    super.shutdown();
    brickController.shutdown();
    menuController .shutdown();
  }

  // BrickController delegation
  public void move(Location target, BrickData brickData){
    brickController.move(target, brickData);
  }

  public void rotate(double angle, BrickData brickData) {
    brickController.rotate(angle, brickData);
  }

  public void setRemoveButtonVisible(boolean state) {
    brickController.setRemoveButtonVisible(state);
  }

  public void removeBrick(BrickData data) {
    brickController.removeBrick(data);
  }
  // Menu Controller delegation
  public MotorBrickData createMockActuator() {
    return menuController.createMockActuator();
  }
  public DistanceBrickData createMockSensor() {
    return menuController.createMockSensor();
  }
  public void createMqttSensor(String id) {
    menuController.createMqttSensor(id);
  }
  public void createMqttActuator(String id) {
    menuController.createMqttActuator(id);
  }

  public void createPaxSensor(String id) {
    menuController.createPaxSensor(id);
  }

  public void printAllBrickData() {
    menuController.printAllBrickData();
  }

  public void importFromFile(File file){
    menuController.importFromFile(file);
  }

  public void exportToFile(File file) {
    menuController.exportToFile(file);
  }
}
