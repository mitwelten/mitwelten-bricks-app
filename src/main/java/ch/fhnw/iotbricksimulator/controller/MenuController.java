package ch.fhnw.iotbricksimulator.controller;

import ch.fhnw.imvs.bricks.actuators.ServoBrick;
import ch.fhnw.imvs.bricks.sensors.DistanceBrick;
import ch.fhnw.iotbricksimulator.model.Garden;
import ch.fhnw.iotbricksimulator.model.Notification.Notification;
import ch.fhnw.iotbricksimulator.model.Notification.NotificationType;
import ch.fhnw.iotbricksimulator.model.brick.BrickData;
import ch.fhnw.iotbricksimulator.model.brick.DistanceBrickData;
import ch.fhnw.iotbricksimulator.model.brick.ServoBrickData;
import ch.fhnw.iotbricksimulator.util.Constants;
import ch.fhnw.iotbricksimulator.util.Location;
import ch.fhnw.iotbricksimulator.util.Util;
import ch.fhnw.iotbricksimulator.util.mvcbase.ControllerBase;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

import static ch.fhnw.iotbricksimulator.util.ConfigIOHandler.readFromFile;
import static ch.fhnw.iotbricksimulator.util.ConfigIOHandler.writeToFile;

public class MenuController extends ControllerBase<Garden> {

  private int mockIdCounter  = 0;
  private double spiralValue = 5d;

  private final Set<String> mqttIds;

  public MenuController(Garden model) {
    super(model);
    mqttIds = new HashSet<>();
  }

  public ServoBrickData createMockActuator(){
    String id = createMockId();
    ServoBrickData newBrick = new ServoBrickData(ServoBrick.connect(model.mockProxy, id));
    addActuator(newBrick);
    return newBrick;
  }

  public DistanceBrickData createMockSensor(){
    String id = createMockId();
    DistanceBrickData newBrick = new DistanceBrickData(DistanceBrick.connect(model.mockProxy, id));
    addSensor(newBrick);
    return newBrick;
  }

  public Optional<DistanceBrickData> createMqttSensor(String id){
    if(isMqttIdAssigned(id)) return Optional.empty();
    DistanceBrickData newBrick = new DistanceBrickData(DistanceBrick.connect(model.mqttProxy, id));
    addSensor(newBrick);
    return Optional.of(newBrick);
  }

  public Optional<ServoBrickData> createMqttActuator(String id){
    if(isMqttIdAssigned(id)) return Optional.empty();
    ServoBrickData newBrick = new ServoBrickData(ServoBrick.connect(model.mqttProxy, id));
    addActuator(newBrick);
    return Optional.of(newBrick);
  }

  private void addSensor(DistanceBrickData brick) {
    var list = new ArrayList<>(model.sensors.getValue());
    list.add(brick);
    updateModel(
        set(model.sensors, list),
        set(brick.location, calcSpawnPosition())
    );
    this.awaitCompletion();
  }

  private void addActuator(ServoBrickData brick) {
    var list = new ArrayList<>(model.actuators.getValue());
    list.add(brick);
    updateModel(
        set(model.actuators, list),
        set(brick.location, calcSpawnPosition())
    );
    this.awaitCompletion();
  }

  public void exportToFile(File file) {
    updateModel(set(model.isLoading, true));
    boolean success = writeToFile(file,
        Stream.concat(
            model.actuators.getValue().stream(),
            model.sensors  .getValue().stream()
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
        .forEach(this::createBrickFromStringLine);
  }

  private void createBrickFromStringLine(String[] line) {
    Optional<? extends BrickData> brick = createBrick(line);
    this.awaitCompletion();
    brick.ifPresentOrElse(
        newBrick ->
            updateModel(
                set(newBrick.location,  new Location(Double.parseDouble(line[3]), Double.parseDouble(line[4]))),
                set(newBrick.faceAngle, Double.parseDouble(line[5]))
            ),
        () -> createNotification(
            NotificationType.ERROR,
            "Create Brick from Config",
            "Failed to create Brick from CSV Data!"
        )
    );
  }

  private Optional<? extends BrickData> createBrick(String[] line) {
    // line content:  1: mock, 2: brick, 3: id, 4: lat, 5: long, 6: faceAngle
    Optional<? extends BrickData> brick = Optional.empty();
    boolean isMock = Boolean.parseBoolean(line[0]);
    boolean isSensor   = line[1].contains(DistanceBrick.class.getSimpleName());
    boolean isActuator = line[1].contains(ServoBrick   .class.getSimpleName());

    if(isMock){
      if(isSensor)   brick = Optional.of(createMockSensor());
      if(isActuator) brick = Optional.of(createMockActuator());
    } else {
      String id = line[2];
      if(isSensor)   brick = createMqttSensor(id);
      if(isActuator) brick = createMqttActuator(id);
    }
    return brick;
  }

  private String createMockId() {
    return Constants.MOCK_ID_PREFIX + mockIdCounter++;
  }

  private boolean isMqttIdAssigned(String id){
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
}