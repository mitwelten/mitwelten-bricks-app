/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.view.menu;

import ch.fhnw.mitwelten.bricksapp.controller.ApplicationController;
import ch.fhnw.mitwelten.bricksapp.model.BrickType;
import ch.fhnw.mitwelten.bricksapp.util.ConfigIOHandler;
import ch.fhnw.mitwelten.bricksapp.util.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.File;
import java.util.*;

public class MenuControls extends BorderPane {

  private static final int DEFAULT_NODE_WIDTH = 125;

  private Text             title;
  private Label            lblId;
  private Label            lblSimulated;
  private CheckBox         isSimulated;
  private Button           addBrickBtn;
  private Button           closeDialogBtn;
  private ToggleGroup      brickTypeGroup;
  private ComboBox<String> comboBox;

  private final ApplicationController controller;

  private Map<BrickType, Set<String>> ids = new HashMap<>();

  public MenuControls(ApplicationController controller, Runnable closeCallback) {
    this.controller = controller;
    initIds();
    initializeControls(closeCallback);
    layoutControls();
  }

  private void initIds() {
    if(ids == null || ids.isEmpty()){
      ids = Map.of(
          BrickType.PAX,      loadIds("paxId"),
          BrickType.STEPPER,  loadIds("stepperId"),
          BrickType.DISTANCE, loadIds("distanceId")
      );
    }
  }

  private Set<String> loadIds(String fileName) {
    File file = new File(Constants.PAX_ID_PATH + fileName);
    Optional<List<String>> result = ConfigIOHandler.readFromFile(file);
    if(result.isPresent()) {
      return new TreeSet<>(result.get());
    }
    return Collections.emptySet();
  }

  private void layoutControls() {
    List<RadioButton> rbs = Arrays.stream(BrickType.values()).map(el -> {
      RadioButton rb = new RadioButton(el.toString());
      rb.setToggleGroup(brickTypeGroup);
      rb.setUserData(el);
      return rb;
    }).toList();

    rbs.get(0).setSelected(true);

    VBox controls = new VBox(10.0);
    controls.getChildren().addAll(
        new HBox(20.0, lblSimulated, isSimulated),
        new Separator()
    );
    controls.getChildren().addAll(rbs);
    controls.getChildren().addAll(
        new Separator(),
        new HBox(20.0, lblId, comboBox)
    );

    setPadding(new Insets(25, 25, 25, 25));
    setTop(title);
    setBottom(new HBox(50.0, addBrickBtn, closeDialogBtn));
    setCenter(controls);
    BorderPane.setMargin(controls, new Insets(12,12,12,12));
    BorderPane.setAlignment(closeDialogBtn, Pos.BASELINE_RIGHT);
  }

  private void initializeControls(Runnable closeCallback) {

    title = new Text("Add Bricks");
    title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

    closeDialogBtn = new Button("Close");
    addBrickBtn    = new Button("Add");
    lblId          = new Label("ID");
    lblSimulated   = new Label("Simulated");
    isSimulated    = new CheckBox();
    brickTypeGroup = new ToggleGroup();
    comboBox       = new ComboBox<>();

    comboBox.getItems().addAll(ids.get(BrickType.DISTANCE));
    comboBox.setValue(comboBox.getItems().get(0));
    comboBox.setEditable(true);
    comboBox.getValue();

    closeDialogBtn.setPrefWidth(DEFAULT_NODE_WIDTH);
    addBrickBtn.setPrefWidth(DEFAULT_NODE_WIDTH);

    brickTypeGroup.selectedToggleProperty().addListener((_1, _2, newValue) -> {
      comboBox.getItems().clear();
      comboBox.getItems().addAll(ids.get((BrickType) newValue.getUserData()));
      comboBox.setValue(comboBox.getItems().get(0));
    });

    isSimulated.selectedProperty().addListener((_1, _2, newValue) -> {
      comboBox.setDisable(newValue);
    });

    closeDialogBtn.setOnAction(e -> closeCallback.run());
    addBrickBtn.setOnAction(e -> {
      String brickId = isSimulated.isSelected() ? controller.getSimulatedId() : comboBox.getValue();
          if (!controller.isIdAssigned(brickId)) {
            controller.addBrick(
                isSimulated.isSelected(),
                (BrickType) brickTypeGroup.getSelectedToggle().getUserData(),
                brickId
            );
          }
        }
    );

  }
}