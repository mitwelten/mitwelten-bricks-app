package ch.fhnw.iotbricksimulator.view.menu;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ch.fhnw.iotbricksimulator.controller.ApplicationController;
import ch.fhnw.iotbricksimulator.util.Constants;

public class Controls extends GridPane {

  private static final int DEFAULT_NODE_WIDTH = 125;

  private Separator separator1;
  private Separator separator2;
  private Separator separator3;

  private Button addActuatorButton;
  private Button addSensorButton;
  private Button addMqttSensor;
  private Button addMqttActuator;
  private Button closeDialog;

  private TextField sensorId;
  private TextField actuatorId;
  private TextField mqttUrl;

  private Text title;

  private Label simBricksTitle;
  private Label mqttBricksTitle;
  private Label mqttUrlLabel;

  private final ApplicationController controller;

  public Controls(ApplicationController controller, Runnable closeCallback) {
    this.controller = controller;
    setAlignment(Pos.CENTER);
    setHgap(12);
    setVgap(8);
    setPadding(new Insets(25, 25, 25, 25));

    initializeControls(closeCallback);
    layoutControls();
  }

  private void layoutControls() {
    // col, row, col-spawn, row-spawn
    add(title,             0,  0, 2, 1);
    add(simBricksTitle,    0,  2, 1, 1);
    add(separator1,        0,  3, 2, 1);
    add(addActuatorButton, 0,  4, 1, 1);
    add(addSensorButton,   1,  4, 1, 1);
    add(mqttBricksTitle,   0,  7, 1, 1);
    add(separator2,        0,  8, 2, 1);
    add(mqttUrlLabel,      0,  9, 1, 1);
    add(mqttUrl,           1,  9, 1, 1);
    add(addMqttSensor,     0, 10, 1, 1);
    add(sensorId,          1, 10, 1, 1);
    add(addMqttActuator,   0, 11, 1, 1);
    add(actuatorId,        1, 11, 1, 1);
    add(separator3,        0, 13, 2, 1);
    add(closeDialog,       0, 17, 1, 1);
  }

  private void initializeControls(Runnable closeCallback) {

    title             = new Text("Add Bricks");
    mqttUrlLabel      = new Label("URL:");
    simBricksTitle    = new Label("Simulated");
    mqttBricksTitle   = new Label("MQTT");

    separator1        = new Separator();
    separator2        = new Separator();
    separator3        = new Separator();

    addActuatorButton = new Button("+ Actuator");
    addSensorButton   = new Button("+ Sensor");
    addMqttSensor     = new Button("+ Mqtt Sensor");
    addMqttActuator   = new Button("+ Mqtt Actuator");
    closeDialog       = new Button("Close");

    sensorId          = new TextField("sensor ID");
    actuatorId        = new TextField("actor ID");
    mqttUrl           = new TextField(Constants.BASE_URL);

    title            .setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
    mqttBricksTitle  .setFont(Font.font("Tahoma", FontWeight.LIGHT, 10));
    simBricksTitle   .setFont(Font.font("Tahoma", FontWeight.LIGHT, 10));

    addMqttActuator  .setDisable(true);
    actuatorId       .setDisable(true);
    mqttUrl          .setDisable(true);

    addActuatorButton.setPrefWidth(DEFAULT_NODE_WIDTH);
    addSensorButton  .setPrefWidth(DEFAULT_NODE_WIDTH);
    addMqttActuator  .setPrefWidth(DEFAULT_NODE_WIDTH);
    addMqttSensor    .setPrefWidth(DEFAULT_NODE_WIDTH);
    closeDialog      .setPrefWidth(DEFAULT_NODE_WIDTH);
    mqttUrl          .setPrefWidth(DEFAULT_NODE_WIDTH);
    actuatorId       .setPrefWidth(DEFAULT_NODE_WIDTH);
    sensorId         .setPrefWidth(DEFAULT_NODE_WIDTH);

    addActuatorButton.setOnAction(e -> controller.createMockActuator());
    addSensorButton  .setOnAction(e -> controller.createMockSensor());
    addMqttActuator  .setOnAction(e -> controller.createMqttActuator(actuatorId.getText()));
    addMqttSensor    .setOnAction(e -> controller.createMqttSensor  (sensorId  .getText()));
    closeDialog      .setOnAction(e -> closeCallback.run());
  }
}