package ch.fhnw.iotbricksimulator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ch.fhnw.iotbricksimulator.controller.ApplicationController;
import ch.fhnw.iotbricksimulator.model.Garden;
import ch.fhnw.iotbricksimulator.util.Constants;
import ch.fhnw.iotbricksimulator.view.GardenGUI;
import ch.fhnw.iotbricksimulator.view.background.Grid;
import ch.fhnw.iotbricksimulator.view.menu.AppMenuBar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class AppStarter extends Application {

  private ApplicationController controller;

  @Override
  public void start(Stage primaryStage) throws Exception {
    Garden gardenModel = new Garden();
    controller         = new ApplicationController(gardenModel);
    BorderPane gui     = new BorderPane();
    primaryStage.setTitle("IoT Brick Simulator");
    setupStage(primaryStage, gui);
    primaryStage.show();
  }

  @Override
  public void stop() {
    controller.shutdown();
  }

  private void setupStage(Stage primaryStage, BorderPane gui) throws IOException {
    Pane copyright = new Pane();
    drawCopyright(copyright);

    Pane background = new StackPane(new Grid(), copyright, new GardenGUI(controller));
    drawBackground(background);

    AppMenuBar menu = new AppMenuBar(controller, primaryStage, this::stop);
    gui.setTop(menu);
    gui.setCenter(background);

    Scene scene = new Scene(gui);
    addKeyListener(scene);

    primaryStage.setScene(scene);
  }

  private void addKeyListener(Scene scene) {
    scene.setOnKeyPressed(e -> {
      if(e.getCode().toString().equals(Constants.REMOVE_KEY)){
        controller.setRemoveButtonVisible(true);
      }
    });
    scene.setOnKeyReleased(e -> {
      if(e.getCode().toString().equals(Constants.REMOVE_KEY)){
        controller.setRemoveButtonVisible(false);
      }
    });
  }

  private void drawCopyright(Pane copyright) {
    int width = 70, height = 18;
    String text = "© swisstopo";
    Region background = new Region();
    background.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.6),null, null)));
    background.setMinWidth(width);
    background.setMinHeight(height);
    background.relocate(Constants.WINDOW_WIDTH - width, Constants.WINDOW_HEIGHT - height);

    Text textField = new Text(text);
    textField.setFont(Font.font("Tahoma", FontWeight.LIGHT, 10));
    textField.relocate(Constants.WINDOW_WIDTH - width + 5, Constants.WINDOW_HEIGHT - height + 1);
    copyright.getChildren().addAll(background, textField);
  }

  private void drawBackground(Pane background) throws IOException {
    // https://map.geo.admin.ch/?lang=de&topic=swisstopo&bgLayer=ch.swisstopo.pixelkarte-farbe&catalogNodes=1392,1430&layers=ch.swisstopo.images-swissimage-dop10.metadata,ch.swisstopo.swissimage-product,KML%7C%7Chttps:%2F%2Fpublic.geo.admin.ch%2Fapi%2Fkml%2Ffiles%2FLAyAU_uYTCGngB4wfIo6tQ&E=2612657.47&N=1261467.03&zoom=11&layers_timestamp=,current,
    // images around 	2'612'660.5, 1'261'504.3
    //                                                                                                                             (lat),  (long)
    // left bottom coordinate:  bbox=846616.5252866121, 6024537.25835274,  846692.9623148973, 6024613.695381022 => (left bottom) 47.502823,7.605286 - (right top) 47.503287,7.605972
    // left top coordinate:     bbox=846616.5252866121, 6024690.132409308, 846692.9623148973, 6024766.569437591 =>               47.503751,7.605286 ,             47.504214,7.605972
    // right top coordinate:    bbox=846769.3993431824, 6024690.132409308, 846845.8363714677, 6024766.569437591 =>               47.503751,7.606659,              47.504214,7.607346
    // right bottom coordinate: bbox=846769.3993431824, 6024537.25835274,  846845.8363714677, 6024613.695381022 =>               47.502823,7.606659,              47.503287,7.607346
    //
    //
    // LT: 47.504214,7.605286
    // LB: 47.502823,7.605286
    // RT: 47.504214,7.607346
    // RB: 47.502823,7.607346
    //
    // deltaX = 0.001765
    // deltaY = 0.001765
    // Image Positions:
    // zoom level: 27
    // [ 0 1 2 ]
    // [ 3 4 5 ]
    // [ 6 7 8 ]

    // https://wms.geo.admin.ch/?&service=WMS&request=GetMap&layers=ch.swisstopo.swissimage&styles=&format=image%2Fjpeg&transparent=false&version=1.1.1&height=256&width=256&srs=EPSG%3A3857&bbox=846692.9623148973,6024690.132409308,846769.3993431824,6024766.569437591
    // http://gps-cache.de/geocaching/koordinaten-umrechnung.htm
    // LAT_WGS84 = 2*(ARCTAN(EXP(LAT_MG/6371000))-PI()/4)/PI()*180
    // LON_WGS84 = LON_MG*180/(6371000*PI())

    // LAT_MG=6371000*LN(TAN(LAT_WGS84+90)/360*PI())
    // LON_MG=LON_WGS84*6371000*PI()/180

    List<Image> filesInFolder = Files.walk(Paths.get(Constants.CSV_PATH + "/img/v2/"))
        .filter(Files::isRegularFile)
        .map(Path::toFile)
        .sorted()
        .map(img -> new Image(Constants.CSV_PATH + "/img/v2/" + img.getName()))
        .toList();

    List<BackgroundPosition> positions = new ArrayList<>();

    for (int left = 0; left < 3; left++) {
      for (int right = 0; right < 3; right++) {
        final int PIXEL_PER_PIECE = 256;
        positions.add(new BackgroundPosition(
            null,
            PIXEL_PER_PIECE * right,
            false,
            null,
            PIXEL_PER_PIECE * left,
            false
        ));
      }
    }

    List<BackgroundImage> bgImages = IntStream
        .range(0, filesInFolder.size())
        .mapToObj(
            idx -> new BackgroundImage(
                filesInFolder.get(idx),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                positions.get(idx),
                BackgroundSize.DEFAULT
            )).toList();

    background.setBackground(new Background(bgImages.toArray(new BackgroundImage[0])));
  }

  public static void main(String[] args) {
    launch(args);
  }
}
