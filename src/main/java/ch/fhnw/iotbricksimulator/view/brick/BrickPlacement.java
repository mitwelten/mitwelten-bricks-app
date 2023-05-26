package ch.fhnw.iotbricksimulator.view.brick;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ch.fhnw.iotbricksimulator.controller.ApplicationController;
import ch.fhnw.iotbricksimulator.model.brick.BrickData;
import ch.fhnw.iotbricksimulator.util.Constants;
import ch.fhnw.iotbricksimulator.util.Location;

import java.util.concurrent.atomic.AtomicReference;

public abstract class BrickPlacement extends Group {

  protected double faceAngle;

  protected Group  cross;
  private Text     label;
  private Region   labelBackground;

  private final ApplicationController controller;
  private final BrickData        brickData;

  public BrickPlacement(ApplicationController controller, BrickData brick, Runnable removeMe) {
    super();
    this.controller = controller;
    this.brickData  = brick;
    initializeControls(removeMe);
    layoutControls();
    initializeMouseListeners();
  }

  private void initializeMouseListeners() {
    addDragNDropSupport();

    this.setOnMouseEntered(event -> {
      this.toFront();
      super.getChildren().addAll(labelBackground, label);
    });
    this.setOnMouseExited (event -> super.getChildren().removeAll(labelBackground, label));

    this.setOnScroll( e -> {
      int dAngle = 0;
      // need to be in separate if statements to work properly
      if(e.getDeltaY() < 0) dAngle = -2;
      if(e.getDeltaY() > 0) dAngle =  2;
      controller.rotate(brickData.faceAngle.getValue() + dAngle, brickData);
    });
  }

  private void addDragNDropSupport(){
    AtomicReference<Double> orgSceneX = new AtomicReference<>(0d);
    AtomicReference<Double> orgSceneY = new AtomicReference<>(0d);

    this.setOnMousePressed(event -> {

      orgSceneX.set(event.getSceneX());
      orgSceneY.set(event.getSceneY());

      BrickPlacement bp = (BrickPlacement) (event.getSource());
      bp.toFront();
    });

    this.setOnMouseDragged(event -> {
      double offsetX = event.getSceneX() - orgSceneX.get();
      double offsetY = event.getSceneY() - orgSceneY.get();
      BrickPlacement bp = (BrickPlacement) (event.getSource());
      orgSceneX.set(event.getSceneX());
      orgSceneY.set(event.getSceneY());
      Location brickLocation = new Location(
          Constants.WINDOW_WIDTH - (bp.getLayoutY() + offsetY), // mirroring the y-axis
          bp.getLayoutX() + offsetX
      );
      controller.move(brickLocation,brickData);
    });
  }

  private void layoutControls() {
    int padding = 5;
    int margin    = 15;
    int marginTop = 15;
    label.relocate          (BrickNode.WIDTH_BRICK + margin,          - BrickNode.HEIGHT_BRICK + margin + marginTop);
    labelBackground.relocate(BrickNode.WIDTH_BRICK + margin - padding, - BrickNode.HEIGHT_BRICK + margin+ marginTop - padding);

    super.getChildren().add(cross);
  }

  private void initializeControls(Runnable removeMe) {
    this.faceAngle = 0;
    this.setCursor(Cursor.HAND);

    labelBackground = new Region();
    labelBackground.setMinHeight(90);
    labelBackground.setMinWidth(105);
    BackgroundFill bgFill = new BackgroundFill(Color.rgb(255,255,255, 0.5), new CornerRadii(5), null);
    labelBackground.setBackground(new Background(bgFill));

    label = new Text();
    label.setFont(Font.font("SourceCodePro", FontWeight.NORMAL, 12));

    cross = new Group();
    Line line1 = createCrossLine(false);
    Line line2 = createCrossLine(true);
    Circle crossCircle = new Circle(4, 4, 8);
    crossCircle.setStrokeWidth(1);
    crossCircle.setFill(Color.rgb(255,0,0, 1));
    cross.getChildren().addAll(crossCircle, line1, line2);
    cross.relocate(40,-15);

    cross.setOnMouseClicked(e -> {
      if(e.isShiftDown()){
        removeMe.run();
      }
    });
  }

  private Line createCrossLine(boolean isMirrored) {
    Line line = new Line(0, 0, 8, 8);
    // style properties
    line.setStroke       (Color.rgb(255,255,255));
    line.setStrokeType   (StrokeType.CENTERED);
    line.setStrokeLineCap(StrokeLineCap.ROUND);
    line.setStrokeWidth  (2.0);

    if(isMirrored) line.setScaleX(-1.0); // mirroring
    return line;
  }

  public void setLabel(String label) {
    this.label.setText(label);
  }

  public void setRemoveBtnVisible(boolean isVisible){
    if(isVisible) cross.toFront();
    cross.setVisible(isVisible);
  }

  public abstract BrickData getBrick();

  public abstract void setRotateBrickSymbol(double angel);

  public double getFaceAngle() {
    return faceAngle;
  }
}