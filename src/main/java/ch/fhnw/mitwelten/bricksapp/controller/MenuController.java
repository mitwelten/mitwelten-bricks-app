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

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

import static ch.fhnw.mitwelten.bricksapp.util.ConfigIOHandler.readFromFile;
import static ch.fhnw.mitwelten.bricksapp.util.ConfigIOHandler.writeToFile;

public class MenuController extends ControllerBase<Garden> {

  private int mockIdCounter  = 0;
  private double spiralValue = 5d;

  private final Set<String> mqttIds;

  public MenuController(Garden model) {
    super(model);
    mqttIds = new HashSet<>();
  }

  private void addPaxSensor(PaxBrickData brick) {
    var list = new ArrayList<>(model.paxSensors.getValue());
    Location spawnLocation = brick.location.getValue();
    if(spawnLocation.lat() == 0 && spawnLocation.lon() == 0) spawnLocation = calcSpawnPosition();
    list.add(brick);
    updateModel(
        set(model.paxSensors, list),
        set(brick.location, spawnLocation)
    );
    this.awaitCompletion();
  }

  private void addDistSensor(DistanceBrickData brick) {
    var list = new ArrayList<>(model.distSensors.getValue());
    Location spawnLocation = brick.location.getValue();
    if(spawnLocation.lat() == 0 && spawnLocation.lon() == 0) spawnLocation = calcSpawnPosition();
    list.add(brick);
    updateModel(
        set(model.distSensors, list),
        set(brick.location, spawnLocation)
    );
    this.awaitCompletion();
  }

  private void addActuator(MotorBrickData brick) {
    var list = new ArrayList<>(model.stepperActuators.getValue());
    Location spawnLocation = brick.location.getValue();
    if(spawnLocation.lat() == 0 && spawnLocation.lon() == 0) spawnLocation = calcSpawnPosition();
    list.add(brick);
    updateModel(
        set(model.stepperActuators, list),
        set(brick.location, spawnLocation)
    );
    this.awaitCompletion();
  }

  public void exportToFile(File file) {
    updateModel(set(model.isLoading, true));
    boolean success = writeToFile(file,
        Stream.concat(
            model.stepperActuators.getValue().stream(),
            model.distSensors.getValue().stream()
        ).toList()
    );
    if(!success){
      createNotification(NotificationType.ERROR, "Export config", "Failed to export config!");
    }
    updateModel(set(model.isLoading, false));
  }

  public void printAllBrickData() {
    String sb = "Data Snapshot from:" + Util.getTimeStamp() + "\n" +
        "\nSensors:\n" +
        toStringOfBrickList(model.distSensors.getValue()) +
        "\nActuators:\n" +
        toStringOfBrickList(model.stepperActuators.getValue());
    System.out.println(sb);
  }

  private String toStringOfBrickList(List<? extends BrickData> bricks) {
    return String.join("\n", bricks.stream().map(BrickData::toString).toList());
  }

  public void importFromFile(File file){
    updateModel(set(model.isLoading, true));
    readFromFile(file).ifPresentOrElse(
        this::importConfigFromList,
        () -> createNotification(NotificationType.ERROR, "Load Config", "Failed to read config!")
    );
    updateModel(set(model.isLoading, false));
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

  private void importConfigFromList(List<String> lines) {
    lines.stream()
        .skip(1) // header
        .map(line -> line.split(","))
        .forEach(this::createBrick);
  }


  private void createBrick(String[] line) {
    // line content:  0: mock, 1: brick, 2: id, 3: lat, 4: long, 5: faceAngle
    boolean isMock   = Boolean.parseBoolean(line[0]);
    double lat       = Double.parseDouble  (line[3]);
    double lon       = Double.parseDouble  (line[4]);
    double faceAngle = Double.parseDouble  (line[5]);
    System.out.println("lat: " + lat);
    System.out.println("lon: " + lon);

    Optional<BrickType> brickType = Arrays.stream(BrickType.values())
        .filter(bt -> line[1].contains(bt.toString()))
        .findAny();

    brickType.ifPresentOrElse(
        bt -> addBrick(isMock, bt, line[2], lat, lon, faceAngle),
        () -> createNotification(
            NotificationType.ERROR,
            "Create Brick from Config",
            "Failed to create Brick from CSV Data!"
        )
    );
  }

  private String createMockId() {
    return Constants.MOCK_ID_PREFIX + mockIdCounter++;
  }

  public boolean isIdAssigned(String id){
    if(!mqttIds.add(id)) {
      createNotification(
          NotificationType.ERROR,
          "Create Brick from Config",
          "Id is already assigned"
      );
      return true;
    }
    return false;
  }

  private Location calcSpawnPosition() {
    // archimedic spiral formula: x(t) = at cos(t), y(t) = at sin(t)
    double a = 10;
    double offset = (double) Constants.WINDOW_HEIGHT / 2;
    double t = spiralValue;
    double x = a * t * Math.cos(t);
    double y = a * t * Math.sin(t);
    spiralValue += 0.5;
    return new Location(x + offset, y + offset);
  }

  public void addBrick(boolean isSimulated, BrickType userData, String id, double lat, double lon, double faceAngle) {
    if (isSimulated)  id = createMockId();
    Brick brick = userData.connect(isSimulated ? model.mockProxy : model.mqttProxy, id);
    switch (userData) {
      case PAX       -> addPaxSensor (new PaxBrickData((PaxBrick)           brick, lat, lon, faceAngle));
      case STEPPER   -> addActuator  (new MotorBrickData((StepperBrick)     brick, lat, lon, faceAngle));
      case DISTANCE  -> addDistSensor(new DistanceBrickData((DistanceBrick) brick, lat, lon, faceAngle));
    }
  }
}