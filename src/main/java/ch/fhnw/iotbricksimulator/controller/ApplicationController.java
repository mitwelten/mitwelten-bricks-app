package ch.fhnw.iotbricksimulator.controller;

import ch.fhnw.iotbricksimulator.model.Garden;
import ch.fhnw.iotbricksimulator.model.brick.BrickData;
import ch.fhnw.iotbricksimulator.model.brick.DistanceBrickData;
import ch.fhnw.iotbricksimulator.model.brick.ServoBrickData;
import ch.fhnw.iotbricksimulator.util.Location;
import ch.fhnw.iotbricksimulator.util.mvcbase.ControllerBase;

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
  public ServoBrickData createMockActuator() {
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
