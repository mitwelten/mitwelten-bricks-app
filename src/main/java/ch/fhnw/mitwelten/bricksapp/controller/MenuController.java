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
import ch.fhnw.mitwelten.bricksapp.model.brick.impl.ActuatorBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.impl.SensorBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.sensors.DistanceBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.actuators.MotorBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.sensors.PaxBrickData;
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

  private final Set<String> ids;

  public MenuController(Garden model) {
    super(model);
    ids = new HashSet<>();
  }

  private void addSensor(SensorBrickData brick) {
    var list = new ArrayList<>(model.sensors.getValue());
    Location spawnLocation = brick.location.getValue();
    if(spawnLocation.lat() == 0 && spawnLocation.lon() == 0) spawnLocation = calcSpawnPosition();
    list.add(brick);
    updateModel(
        set(model.sensors, list),
        set(brick.location, spawnLocation)
    );
    this.awaitCompletion();
  }

  private void addActuator(ActuatorBrickData brick) {
    var list = new ArrayList<>(model.actuators.getValue());
    Location spawnLocation = brick.location.getValue();
    if(spawnLocation.lat() == 0 && spawnLocation.lon() == 0) spawnLocation = calcSpawnPosition();
    list.add(brick);
    updateModel(
        set(model.actuators, list),
        set(brick.location, spawnLocation)
    );
    this.awaitCompletion();
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
      createNotification(NotificationType.ERROR, "Export config", "Failed to export config!");
    }
    updateModel(set(model.isLoading, false));
  }

  public void printAllBrickData() {
    String sb = "Data Snapshot from:" + Util.getTimeStamp() + "\n" +
        "\nSensors:\n" +
        toStringOfBrickList(model.sensors.getValue()) +
        "\nActuators:\n" +
        toStringOfBrickList(model.actuators.getValue());
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

  public void removeBrick(BrickData data) {
    String id = data.getID();
    if (!ids.remove(id)){
      createNotification(
          NotificationType.ERROR,
          "Delete Brick",
          "Remove Brick: Id " + id + " not assigned!"
      );
    }
    if(data instanceof DistanceBrickData) removeBrick((DistanceBrickData) data);
    if(data instanceof MotorBrickData)    removeBrick((MotorBrickData)    data);
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

  public boolean isIdAssigned(String id){
    if(id == null || id.equals("")){
      createNotification(
      NotificationType.ERROR,
          "Error: Brick ID",
          "Id must not be empty!"
      );
      return true;
    }
    if(!ids.add(id)) {
      createNotification(
          NotificationType.ERROR,
          "Error: Brick ID",
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
      case STEPPER   -> addActuator (new MotorBrickData((StepperBrick)     brick, new Location(lat, lon), faceAngle));
      case PAX       -> addSensor   (new PaxBrickData((PaxBrick)           brick, new Location(lat, lon), faceAngle));
      case DISTANCE  -> addSensor   (new DistanceBrickData((DistanceBrick) brick, new Location(lat, lon), faceAngle));
    }
  }
}