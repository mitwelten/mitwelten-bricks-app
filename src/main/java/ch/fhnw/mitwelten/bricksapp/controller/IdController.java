/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.controller;

import ch.fhnw.mitwelten.bricksapp.model.Garden;
import ch.fhnw.mitwelten.bricksapp.model.Notification.NotificationType;
import ch.fhnw.mitwelten.bricksapp.model.brick.BrickData;
import ch.fhnw.mitwelten.bricksapp.util.Constants;
import ch.fhnw.mitwelten.bricksapp.util.mvcbase.ControllerBase;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class IdController extends ControllerBase<Garden> {

  private final BiConsumer<NotificationType, String> createNotification;
  private int simulatedIdCounter = 0;
  private final Set<String> ids;

  protected IdController(Garden model, BiConsumer<NotificationType, String> createNotification) {
    super(model);
    this.createNotification = createNotification;
    this.ids = new HashSet<>();
  }

  public boolean isValidId(String id) {
    if(id == null || id.equals("")){
      createNotification.accept(
          NotificationType.ERROR,
          "Error: Brick ID: ID must not be empty!"
      );
      return true;
    }
    if(!ids.add(id)) {
      createNotification.accept(
          NotificationType.ERROR,
          "Error: Brick ID: ID is already assigned"
      );
      return true;
    }
    return false;
  }

  public String getSimulatedId() {
    return Constants.SIM_ID_PREFIX + simulatedIdCounter++;
  }

  public boolean removeId(BrickData data) {
    String id = data.getID();
    if (!ids.remove(id)){
      createNotification.accept(
          NotificationType.ERROR,
          "Remove Brick: Id " + id + " not assigned!"
      );
      return false;
    }
    return true;
  }
}
