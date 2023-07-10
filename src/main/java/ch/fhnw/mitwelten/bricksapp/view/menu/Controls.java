/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.view.menu;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ch.fhnw.mitwelten.bricksapp.controller.ApplicationController;
import ch.fhnw.mitwelten.bricksapp.util.Constants;

public class Controls extends GridPane {

  private static final int DEFAULT_NODE_WIDTH = 125;

  private Separator separator1;
  private Separator separator2;
  private Separator separator3;
  private Separator separator4;

  private Button addActuatorButton;
  private Button addSensorButton;
  private Button addMqttSensor;
  private Button addMqttActuator;
  private Button addPaxSensor;
  private Button closeDialog;

  private TextField sensorId;
  private TextField actuatorId;
  private TextField mqttUrl;
  private TextField paxId;

  private Text title;

  private Label simulatedTitle;
  private Label mqttTitle;
  private Label mqttUrlLabel;
  private Label paxTitle;

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
    add(simulatedTitle,    0,  2, 1, 1);
    add(separator1,        0,  3, 2, 1);
    add(addActuatorButton, 0,  4, 1, 1);
    add(addSensorButton,   1,  4, 1, 1);
    add(mqttTitle,         0,  7, 1, 1);
    add(separator2,        0,  8, 2, 1);
    add(mqttUrlLabel,      0,  9, 1, 1);
    add(mqttUrl,           1,  9, 1, 1);
    add(addMqttSensor,     0, 10, 1, 1);
    add(sensorId,          1, 10, 1, 1);
    add(addMqttActuator,   0, 11, 1, 1);
    add(actuatorId,        1, 11, 1, 1);
    add(paxTitle,          0, 13, 1, 1);
    add(separator3,        0, 14, 2, 1);
    add(addPaxSensor,      0, 15, 1, 1);
    add(paxId,             1, 15, 1, 1);
    add(separator4,        0, 17, 2, 1);
    add(closeDialog,       0, 20, 1, 1);
  }

  private void initializeControls(Runnable closeCallback) {

    title             = new Text("Add Bricks");
    mqttUrlLabel      = new Label("URL:");
    simulatedTitle    = new Label("Simulated");
    mqttTitle         = new Label("MQTT");
    paxTitle          = new Label("PAX");

    separator1        = new Separator();
    separator2        = new Separator();
    separator3        = new Separator();
    separator4        = new Separator();

    addActuatorButton = new Button("+ Actuator");
    addSensorButton   = new Button("+ Sensor");
    addMqttSensor     = new Button("+ Distance Sensor");
    addMqttActuator   = new Button("+ Stepper Actuator");
    addPaxSensor      = new Button("+ PAX Sensor");
    closeDialog       = new Button("Close");

    sensorId          = new TextField("sensor ID");
    actuatorId        = new TextField("actor ID");
    mqttUrl           = new TextField(Constants.BASE_URL);
    paxId             = new TextField("PAX ID");

    title            .setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
    mqttTitle        .setFont(Font.font("Tahoma", FontWeight.LIGHT, 10));
    simulatedTitle   .setFont(Font.font("Tahoma", FontWeight.LIGHT, 10));
    paxTitle         .setFont(Font.font("Tahoma", FontWeight.LIGHT, 10));

    addActuatorButton.setPrefWidth(DEFAULT_NODE_WIDTH);
    addSensorButton  .setPrefWidth(DEFAULT_NODE_WIDTH);
    addMqttActuator  .setPrefWidth(DEFAULT_NODE_WIDTH);
    addMqttSensor    .setPrefWidth(DEFAULT_NODE_WIDTH);
    closeDialog      .setPrefWidth(DEFAULT_NODE_WIDTH);
    mqttUrl          .setPrefWidth(DEFAULT_NODE_WIDTH);
    actuatorId       .setPrefWidth(DEFAULT_NODE_WIDTH);
    sensorId         .setPrefWidth(DEFAULT_NODE_WIDTH);
    paxId            .setPrefWidth(DEFAULT_NODE_WIDTH);

    addActuatorButton.setOnAction(e -> controller.createMockActuator());
    addSensorButton  .setOnAction(e -> controller.createMockSensor());
    addMqttActuator  .setOnAction(e -> controller.createMqttActuator(actuatorId.getText()));
    addMqttSensor    .setOnAction(e -> controller.createMqttSensor  (sensorId  .getText()));
    addPaxSensor     .setOnAction(e -> controller.createPaxSensor   (paxId.getText()));
    closeDialog      .setOnAction(e -> closeCallback.run());
  }
}