/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.controller;

import ch.fhnw.imvs.bricks.actuators.StepperBrick;
import ch.fhnw.imvs.bricks.core.Brick;
import ch.fhnw.imvs.bricks.sensors.DistanceBrick;
import ch.fhnw.imvs.bricks.sensors.PaxBrick;
import ch.fhnw.mitwelten.bricksapp.model.BrickType;
import ch.fhnw.mitwelten.bricksapp.model.Garden;
import ch.fhnw.mitwelten.bricksapp.model.Notification.NotificationType;
import ch.fhnw.mitwelten.bricksapp.model.brick.BrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.actuators.StepperBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.impl.ActuatorBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.impl.SensorBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.sensors.DistanceBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.sensors.PaxBrickData;
import ch.fhnw.mitwelten.bricksapp.util.Location;
import ch.fhnw.mitwelten.bricksapp.util.Util;
import ch.fhnw.mitwelten.bricksapp.util.mvcbase.ControllerBase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static ch.fhnw.mitwelten.bricksapp.util.ConfigIOHandler.readFromFile;
import static ch.fhnw.mitwelten.bricksapp.util.ConfigIOHandler.writeToFile;
import static ch.fhnw.mitwelten.bricksapp.util.Util.calcSpawnPosition;

public class MenuController extends ControllerBase<Garden> {

  private double spiralValue     = 5d;
  private final BiConsumer<NotificationType, String> createNotification;

  public MenuController(Garden model, BiConsumer<NotificationType, String> createNotification) {
    super(model);
    this.createNotification = createNotification;
  }

  private SensorBrickData addSensor(SensorBrickData brick) {
    var list = new ArrayList<>(model.sensors.getValue());
    Location spawnLocation = brick.location.getValue();
    if(spawnLocation.lat() == 0 && spawnLocation.lon() == 0) spawnLocation = calcSpawnPosition(spiralValue++);
    list.add(brick);
    updateModel(
        set(model.sensors, list),
        set(brick.location, spawnLocation)
    );
    this.awaitCompletion();
    return brick;
  }

  private ActuatorBrickData addActuator(ActuatorBrickData brick) {
    var list = new ArrayList<>(model.actuators.getValue());
    Location spawnLocation = brick.location.getValue();
    if(spawnLocation.lat() == 0 && spawnLocation.lon() == 0) spawnLocation = calcSpawnPosition(spiralValue++);
    list.add(brick);
    updateModel(
        set(model.actuators, list),
        set(brick.location, spawnLocation)
    );
    this.awaitCompletion();
    return brick;
  }

  public void exportToFile(File file) {
    updateModel(set(model.isLoading, true));
    boolean success = writeToFile(file,
        Stream.concat(
            model.actuators.getValue().stream(),
            model.sensors.getValue().stream()
        ).toList()
    );
    if(!success){
      createNotification.accept(NotificationType.ERROR, "Export config: Failed to export config!");
    }
    updateModel(set(model.isLoading, false));
  }

  public void printAllBrickData() {
    String sb = "Data Snapshot from:" + Util.getTimeStamp() + "\n" +
        "\nSensors:\n" +
        toStringOfBrickList(model.sensors.getValue()) +
        "\n\nActuators:\n" +
        toStringOfBrickList(model.actuators.getValue());
    System.out.println(sb);
  }

  private String toStringOfBrickList(List<? extends BrickData> bricks) {
    return String.join("\n", bricks.stream().map(BrickData::toStringFormatted).toList());
  }

  public void importFromFile(File file){
    updateModel(set(model.isLoading, true));
    readFromFile(file).ifPresentOrElse(
        this::importConfigFromList,
        () -> createNotification.accept(NotificationType.ERROR, "Load Config: Failed to read config!")
    );
    updateModel(set(model.isLoading, false));
  }



  private void importConfigFromList(List<String> lines) {
    lines.stream()
        .skip(1) // header
        .map(line -> line.split(","))
        .forEach(this::createBrick);
  }

  private void createBrick(String[] line) {
    // line content:  0: sim, 1: brick, 2: id, 3: lat, 4: long, 5: faceAngle
    boolean isSimulated = Boolean.parseBoolean(line[0]);
    double lat          = Double.parseDouble  (line[3]);
    double lon          = Double.parseDouble  (line[4]);
    double faceAngle    = Double.parseDouble  (line[5]);

    Optional<BrickType> brickType = Arrays.stream(BrickType.values())
        .filter(bt -> line[1].contains(bt.toString()))
        .findAny();

    brickType.ifPresentOrElse(
        bt -> addBrick(isSimulated, bt, line[2], new Location(lat, lon), faceAngle),
        () -> createNotification.accept(
            NotificationType.ERROR,
            "Create Brick from Config: Failed to create Brick from CSV Data!"
        )
    );
  }

  public void removeBrick(BrickData data) {
    if(data instanceof DistanceBrickData) removeBrick((DistanceBrickData) data);
    if(data instanceof StepperBrickData)  removeBrick((StepperBrickData)  data);
    if(data instanceof PaxBrickData)      removeBrick((PaxBrickData)      data);
  }

  private void removeBrick(SensorBrickData brickData) {
    List<SensorBrickData> modified = new ArrayList<>(model.sensors.getValue())
        .stream()
        .filter(b -> !b.getID().equals(brickData.getID()))
        .toList();
    updateModel(set(model.sensors, modified));
  }

  private void removeBrick(ActuatorBrickData brickData) {
    List<ActuatorBrickData> modified = new ArrayList<>(model.actuators.getValue())
        .stream()
        .filter(b -> !b.getID().equals(brickData.getID()))
        .toList();
    updateModel(set(model.actuators, modified));
  }

  public BrickData addBrick(boolean isSimulated, BrickType userData, String id, Location location, double faceAngle) {
    Brick brick = userData.connect(isSimulated ? model.mockProxy : model.mqttProxy, id);
    BrickData brickData = null;
    switch (userData) {
      case STEPPER   -> brickData = addActuator(new StepperBrickData((StepperBrick)   brick, location, faceAngle));
      case PAX       -> brickData = addSensor  (new PaxBrickData((PaxBrick)           brick, location, faceAngle));
      case DISTANCE  -> brickData = addSensor  (new DistanceBrickData((DistanceBrick) brick, location, faceAngle));
    }
    return brickData;
  }
}