package ch.fhnw.iotbricksimulator.view.menu;

import ch.fhnw.iotbricksimulator.controller.ApplicationController;
import ch.fhnw.iotbricksimulator.util.Util;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AppMenuBar extends MenuBar {

  private Menu     menu;
  private MenuItem addBrick;
  private MenuItem printBrickData;
  private MenuItem exportConfig;
  private MenuItem importConfig;
  private MenuItem shutdown;

  private final ApplicationController controller;

  public AppMenuBar(ApplicationController controller, Stage stage, Runnable shutdownCallback) {
    this.controller = controller;
    initializeControls();
    layoutControls();
    initializeListeners(stage, shutdownCallback);
  }

  private void initializeListeners(Stage stage, Runnable shutdownCallback) {
    addBrick.setOnAction(_e -> {
      Stage dialog     = new Stage();
      Scene popUpScene = new Scene(new Controls(controller, dialog::close), 350, 450);
      dialog.setScene(popUpScene);
      dialog.initOwner(stage);
      dialog.showAndWait();
    });

    printBrickData.setOnAction(_e -> controller.printAllBrickData());

    exportConfig.setOnAction(_e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Load Brick Config");
      fileChooser.setInitialFileName(Util.getTimeStamp() + ".csv");
      fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Brick Config", "csv"));
      File file = fileChooser.showSaveDialog(stage);
      if(file != null) {
        controller.exportToFile(file);
      }
    });

    shutdown.setOnAction(_e -> {
      shutdownCallback.run();
      stage.close();
      Platform.exit();
      System.exit(0);
    });

    importConfig.setOnAction(event -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Load Brick Config");
      fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Brick CSV","*.csv"));
      File f = fileChooser.showOpenDialog(stage);
      controller.importFromFile(f);
    });
  }

  private void initializeControls() {
    menu           = new Menu    ("Menu");
    addBrick       = new MenuItem("Add Brick");
    printBrickData = new MenuItem("Print Brick Data");
    exportConfig   = new MenuItem("Export");
    importConfig   = new MenuItem("Import");
    shutdown       = new MenuItem("Close");
  }

  private void layoutControls() {
    SeparatorMenuItem separator = new SeparatorMenuItem();
    menu.getItems().addAll(
        addBrick,
        printBrickData,
        exportConfig,
        importConfig,
        separator,
        shutdown
    );
    this.getMenus().add(menu);
  }
}
