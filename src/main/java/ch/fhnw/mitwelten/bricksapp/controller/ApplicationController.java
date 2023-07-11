/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.controller;

import ch.fhnw.mitwelten.bricksapp.model.Garden;
import ch.fhnw.mitwelten.bricksapp.model.Notification.Notification;
import ch.fhnw.mitwelten.bricksapp.model.Notification.NotificationType;
import ch.fhnw.mitwelten.bricksapp.model.brick.BrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.actuators.StepperBrickData;
import ch.fhnw.mitwelten.bricksapp.model.BrickType;
import ch.fhnw.mitwelten.bricksapp.util.Location;
import ch.fhnw.mitwelten.bricksapp.util.mvcbase.ControllerBase;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.BiConsumer;

public class ApplicationController extends ControllerBase<Garden> {

  private final ProcessController brickController;
  private final MenuController    menuController;
  private final IdController      idController;

  public ApplicationController(Garden model) {
    super(model);

    BiConsumer<NotificationType, String> createNotification = (NotificationType type, String message) -> {
      Notification newNotification = new Notification(type, message);
      Deque<Notification> queue    = new ArrayDeque<>(model.notifications.getValue());
      queue.push(newNotification);
      updateModel(set(model.notifications, queue));
    };

    brickController = new ProcessController(model, createNotification);
    menuController  = new MenuController   (model, createNotification);
    idController    = new IdController     (model, createNotification);
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
    idController   .shutdown();
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

  public void toggleUpdateLoop() {
    brickController.toggleUpdateLoop();
  }

  public void functionTest(StepperBrickData brick, int[] positions) {
    brickController.functionTest(brick, positions);
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

  public BrickData addBrick(boolean isSimulated, BrickType userData, String id) {
    return menuController.addBrick(isSimulated, userData, id, new Location(0.0, 0.0), 0);
  }

  public void removeBrick(BrickData data) {
    if(idController.removeId(data)) menuController.removeBrick(data);
  }

  // IdController delegation
  public boolean isIdAssigned(String id) {
    return idController.isValidId(id);
  }

  public String getSimulatedId() {
    return idController.getSimulatedId();
  }
}
