/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.view;

import ch.fhnw.mitwelten.bricksapp.controller.ApplicationController;
import ch.fhnw.mitwelten.bricksapp.model.Garden;
import ch.fhnw.mitwelten.bricksapp.model.Notification.Notification;
import ch.fhnw.mitwelten.bricksapp.model.brick.BrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.impl.ActuatorBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.impl.SensorBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.sensors.DistanceBrickData;
import ch.fhnw.mitwelten.bricksapp.util.Constants;
import ch.fhnw.mitwelten.bricksapp.util.mvcbase.ViewMixin;
import ch.fhnw.mitwelten.bricksapp.view.brick.*;
import ch.fhnw.mitwelten.bricksapp.view.brick.actuators.StepperPlacement;
import ch.fhnw.mitwelten.bricksapp.view.brick.sensors.DistancePlacement;
import ch.fhnw.mitwelten.bricksapp.view.brick.sensors.PaxPlacement;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Function;

import static javafx.scene.paint.Color.rgb;

public class GardenGUI extends Pane implements ViewMixin<Garden, ApplicationController> {

  private final ApplicationController controller;
  private ProgressIndicator spinner;
  private Group  runButton;
  private Button updateLoopButton;
  private Region playIcon;
  private Region pauseIcon;


  public GardenGUI(ApplicationController controller) {
    init(controller);
    this.controller = controller;
  }

  @Override
  public void initializeSelf() {
    ViewMixin.super.initializeSelf();
  }

  @Override
  public void initializeParts() {
    spinner = new ProgressIndicator();

    playIcon  = new Region();
    pauseIcon = new Region();
    playIcon .getStyleClass().add("play");
    pauseIcon.getStyleClass().add("pause");

    Region background = new Region();
    background.setMinHeight(25);
    background.setMinWidth(25);
    background.relocate(10,10);
    background.setBackground(
        new Background(
            new BackgroundFill(rgb(255,255,255), new CornerRadii(Integer.MAX_VALUE), null)
        )
    );
    updateLoopButton = new Button();
    updateLoopButton.getStyleClass().add("play-button");
    updateLoopButton.setGraphic(playIcon);
    updateLoopButton.setOnAction(e -> controller.toggleUpdateLoop());
    runButton = new Group(background, updateLoopButton);
  }

  @Override
  public void layoutParts() {
    spinner  .relocate(350, 350);
    runButton.relocate(650, 20);
    this.getChildren().addAll(spinner, runButton);
  }

  @Override
  public void setupModelToUiBindings(Garden model) {
    onChangeOf(model.isLoading).execute((oldValue, newValue) -> {
      if (newValue && !this.getChildren().contains(spinner)) {
        this.getChildren().add(spinner);
        return;
      }
      this.getChildren().remove(spinner);
    });

    onChangeOf(model.runningUpdateLoop).execute(((oldValue, newValue) -> {
      if (newValue){
        updateLoopButton.setGraphic(pauseIcon);
      } else {
        updateLoopButton.setGraphic(playIcon);
      }
    }));

    onChangeOf(model.notifications).execute((oldValue, newValue) -> {
      if(newValue.isEmpty()) return;
      Notification notification = newValue.peek();
      Alert.AlertType alertType;
      switch(notification.type()){
        case CONFIRMATION -> alertType = Alert.AlertType.CONFIRMATION;
        case INFO         -> alertType = Alert.AlertType.INFORMATION;
        case WARNING      -> alertType = Alert.AlertType.WARNING;
        default           -> alertType = Alert.AlertType.ERROR;
      }
      Alert alert = new Alert(alertType, notification.msg());
      alert.showAndWait();
    });

    onChangeOf(model.actuators).execute((oldValue, newValue) -> {
          if(oldValue.size() > newValue.size()) {
            removePlacement(oldValue, newValue);
          } else {
            if (newValue.isEmpty()) return;
            ActuatorPlacement mp = addPlacement(
                model,
                oldValue,
                newValue,
                (brick) -> new StepperPlacement(controller, (ActuatorBrickData) brick)
            );
            addActuatorListeners(mp);
          }
        }
    );

    onChangeOf(model.sensors).execute((oldValue, newValue) -> {
          if(oldValue.size() > newValue.size()) {
            removePlacement(oldValue, newValue);
          } else {
            if (newValue.isEmpty()) return;
            SensorPlacement dp = addPlacement(
                model,
                oldValue,
                newValue,
                (brick) -> {
                  if(brick instanceof DistanceBrickData) {
                    return new DistancePlacement(controller, (SensorBrickData) brick);
                  } else {
                    return new PaxPlacement(controller, (SensorBrickData) brick);
                  }
                }
            );
            addDistSensorListeners(dp);
          }
        }
    );
  }

  private <T extends BrickPlacement> T addPlacement(
      Garden model,
      List<? extends BrickData> oldValue,
      List<? extends BrickData> newValue,
      Function<BrickData, T> ctor)
  {
    T newBrick = newValue
        .stream()
        .filter(brick -> !oldValue.contains(brick))
        .map(ctor)
        .toList()
        .get(0);

    addPlacementToGUI(model, newBrick);
    return newBrick;
  }

  private void addPlacementToGUI(Garden model, BrickPlacement placement) {
    addPlacementListener(model, placement);
    this.getChildren().add(placement);
  }

  private <T extends  BrickData> void removePlacement(List<T> oldValue, List<T> newValue) {
   BrickData removed = oldValue
        .stream()
        .filter(brick -> !newValue.contains(brick))
        .toList()
        .get(0);

    this.getChildren().forEach(brick -> {
      if(brick instanceof BrickPlacement sp){
        if(sp.getBrick().getID().equals(removed.getID())) {
          Platform.runLater(() -> this.getChildren().removeAll(sp));
        }
      }
    });
  }

  private void addPlacementListener(Garden model, BrickPlacement placement) {
    onChangeOf(model.removeButtonVisible).execute((oldVal, newVal) -> placement.setRemoveBtnVisible(newVal));

    onChangeOf(placement.getBrick().faceAngle).execute((oldValue, newValue) -> {
      placement.setRotateBrickSymbol(newValue);
      refreshLabel(placement);
    });

    onChangeOf(placement.getBrick().location).execute((oldValue, newValue) -> {
      placement.setLayoutY(Constants.WINDOW_WIDTH - newValue.lat());
      placement.setLayoutX(newValue.lon());
      refreshLabel(placement);
    });
  }

  private void addDistSensorListeners(SensorPlacement placement) {
    onChangeOf(placement.getBrick().value).execute((oldVal, currentVal) -> {
      placement.setActivityValue(currentVal);
      refreshLabel(placement);
    });
    onChangeOf(placement.getBrick().isMostActive).execute((oldVal, newVal)
        -> placement.setHighlighted(newVal));
  }

  private void addActuatorListeners(ActuatorPlacement placement) {
    onChangeOf(placement.getBrick().value).execute((oldVal, newVal) -> {
      placement.setTargetValue(newVal);
      refreshLabel(placement);
    });
  }

  private void refreshLabel(BrickPlacement placement){
    placement.setLabel(placement.getBrick().toStringFormatted());
  }
}