/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.controller;

import ch.fhnw.mitwelten.bricksapp.model.Garden;
import ch.fhnw.mitwelten.bricksapp.model.brick.BrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.actuators.StepperBrickData;
import ch.fhnw.mitwelten.bricksapp.model.BrickType;
import ch.fhnw.mitwelten.bricksapp.util.Location;
import ch.fhnw.mitwelten.bricksapp.util.mvcbase.ControllerBase;

import java.io.File;

public class ApplicationController extends ControllerBase<Garden> {
  private final ProcessController brickController;
  private final MenuController  menuController;

  public ApplicationController(Garden model) {
    super(model);
    brickController = new ProcessController(model);
    menuController  = new MenuController (model);
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

  // ProcessController delegation
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
    menuController.removeBrick(data);
  }

  // MenuController delegation
  public void printAllBrickData() {
    menuController.printAllBrickData();
  }

  public void importFromFile(File file){
    menuController.importFromFile(file);
  }

  public void exportToFile(File file) {
    menuController.exportToFile(file);
  }

  public void functionTest(StepperBrickData brick, int[] positions) {
    brickController.functionTest(brick, positions);
  }

  public void toggleUpdateLoop() {
    brickController.toggleUpdateLoop();
  }

  public BrickData addBrick(boolean isSimulated, BrickType userData, String id) {
    return menuController.addBrick(isSimulated, userData, id, new Location(0.0, 0.0), 0);
  }

  public boolean isIdAssigned(String id) {
    return menuController.isValidId(id);
  }

  public String getMockId() {
    return menuController.getMockId();
  }
}
