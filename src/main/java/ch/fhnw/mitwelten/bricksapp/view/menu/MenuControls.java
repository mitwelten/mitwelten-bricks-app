/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.view.menu;

import ch.fhnw.mitwelten.bricksapp.controller.ApplicationController;
import ch.fhnw.mitwelten.bricksapp.model.BrickType;
import ch.fhnw.mitwelten.bricksapp.util.Location;
import ch.fhnw.mitwelten.bricksapp.util.Util;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class MenuControls extends BorderPane {

  private static final int DEFAULT_NODE_WIDTH = 125;

  private Text title;
  private CheckBox isSimulated;
  private CheckBox isOnMap;
  private Button addBrickBtn;
  private Button closeDialogBtn;
  private TextField lat;
  private TextField lon;
  private ToggleGroup brickTypeGroup;
  private ComboBox<String> comboBox;

  private final ApplicationController controller;

  private Map<BrickType, Set<String>> ids = new HashMap<>();
  private Map<String, Location> paxLocations = new HashMap<>();

  public MenuControls(ApplicationController controller, Runnable closeCallback) {
    this.controller = controller;
    if (ids.isEmpty()) ids = controller.initIds();
    if (paxLocations.isEmpty()) paxLocations = controller.initPaxLocations();
    initializeControls(closeCallback);
    layoutControls();
  }

  private void layoutControls() {
    List<RadioButton> rbs = Arrays.stream(BrickType.values()).map(brickType -> {
      RadioButton radioBtn = new RadioButton(brickType.toString());
      radioBtn.setToggleGroup(brickTypeGroup);
      radioBtn.setUserData(brickType);
      return radioBtn;
    }).toList();

    rbs.get(0).setSelected(true);

    VBox controls = new VBox(10.0);
    controls.getChildren().addAll(
        new HBox(
            20.0,
            new Label("Simulated"),
            isSimulated,
            new Label("on Map"),
            isOnMap
        ),
        new Separator()
    );
    controls.getChildren().addAll(rbs);
    controls.getChildren().addAll(
        new Separator(),
        new HBox(20.0, new Label("ID"), comboBox)
    );
    controls.getChildren().addAll(
        new Separator(),
        new HBox(20.0, new Label("lat\t"), lat),
        new HBox(20.0, new Label("lon\t"), lon)
    );

    setPadding(new Insets(25, 25, 25, 25));
    setTop(title);
    setBottom(new HBox(50.0, addBrickBtn, closeDialogBtn));
    setCenter(controls);
    BorderPane.setMargin(controls, new Insets(12, 12, 12, 12));
    BorderPane.setAlignment(closeDialogBtn, Pos.BASELINE_RIGHT);
  }

  private void initializeControls(Runnable closeCallback) {

    title = new Text("Add Bricks");
    title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

    closeDialogBtn = new Button("Close");
    addBrickBtn = new Button("Add");
    isSimulated = new CheckBox();
    isOnMap = new CheckBox();
    brickTypeGroup = new ToggleGroup();
    comboBox = new ComboBox<>();
    lat = new TextField("0.0");
    lon = new TextField("0.0");

    isOnMap.setDisable(true);

    comboBox.setEditable(true);
    comboBox.getValue();
    addValuesToComboBox(ids.get(BrickType.DISTANCE));

    closeDialogBtn.setPrefWidth(DEFAULT_NODE_WIDTH);
    addBrickBtn   .setPrefWidth(DEFAULT_NODE_WIDTH);

    brickTypeGroup.selectedToggleProperty().addListener((_1, _2, newActiveBrickType) -> {
      comboBox.getItems().clear();
      addValuesToComboBox(ids.get((BrickType) newActiveBrickType.getUserData()));

      boolean isPaxActive = newActiveBrickType.getUserData().equals(BrickType.PAX);
      Location newTextFieldLocation = new Location(0.0, 0.0);
      if (isPaxActive) {
        newTextFieldLocation = paxLocations.get(comboBox.getValue());
      }
      setLocationTextFieldValues(newTextFieldLocation);
      isOnMap.setDisable(!isPaxActive);
    });

    isSimulated.selectedProperty().addListener((_1, _2, newValue) ->
        comboBox.setDisable(newValue));

    isOnMap.selectedProperty().addListener((_1, _2, isOnMapActive) -> {
      Set<String> paxValues = ids.get(BrickType.PAX);
      if (isOnMapActive) {
         paxValues = paxLocations.keySet()
            .stream()
            .filter(key -> Util.locationOnMap(paxLocations.get(key)))
            .collect(Collectors.toSet());
      }
      addValuesToComboBox(paxValues);
    });

    comboBox.getSelectionModel().selectedItemProperty().addListener((_1, _2, newValue) -> {
     if (brickTypeGroup.getSelectedToggle().getUserData().equals(BrickType.PAX)){
       if(newValue == null) return;

       Location location = paxLocations.getOrDefault(newValue, new Location(0.0, 0.0));
       setLocationTextFieldValues(location);
      }
    });

    closeDialogBtn.setOnAction(e -> closeCallback.run());
    addBrickBtn.setOnAction(e -> {
      Location spawnLocation = paxLocations.getOrDefault(comboBox.getValue(), new Location(0.0, 0.0));
      if (Util.locationOnMap(spawnLocation)){
        spawnLocation = Util.fromCoordinates(spawnLocation);
      } else {
        spawnLocation = new Location(0.0, 0.0);
      }

      String brickId = isSimulated.isSelected() ? controller.getSimulatedId() : comboBox.getValue();
          if (!controller.isIdAssigned(brickId)) {
            controller.addBrick(
                isSimulated.isSelected(),
                (BrickType) brickTypeGroup.getSelectedToggle().getUserData(),
                spawnLocation,
                brickId
            );
          }
        }
    );
  }

  private void setLocationTextFieldValues(Location location){
    lat.setText(String.valueOf(location.lat()));
    lon.setText(String.valueOf(location.lon()));
  }

  private void addValuesToComboBox(Set<String> values) {
    comboBox.getItems().clear();
    comboBox.getItems().addAll(values);
    if (!comboBox.getItems().isEmpty()) {
      comboBox.setValue(comboBox.getItems().get(0));
    }
  }
}