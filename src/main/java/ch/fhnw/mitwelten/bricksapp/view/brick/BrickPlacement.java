/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.view.brick;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ch.fhnw.mitwelten.bricksapp.controller.ApplicationController;
import ch.fhnw.mitwelten.bricksapp.model.brick.BrickData;
import ch.fhnw.mitwelten.bricksapp.util.Constants;
import ch.fhnw.mitwelten.bricksapp.util.Location;

import java.util.concurrent.atomic.AtomicReference;

public abstract class BrickPlacement extends Group {

  protected double faceAngle;

  protected Group cross;

  private Text label;
  private Pane labelGroup;

  private final ApplicationController controller;
  private final BrickData             brickData;

  public BrickPlacement(ApplicationController controller, BrickData brick, Runnable removeMe) {
    super();
    this.controller = controller;
    this.brickData  = brick;
    initializeControls(removeMe);
    layoutControls();
    initializeMouseListeners();
  }

  private void layoutControls() {
    super.getChildren().add(cross);
  }

  protected Pane labelHook(Pane group){
    return group;
  }

  private void initializeControls(Runnable removeMe) {
    this.faceAngle = 0;
    this.setCursor(Cursor.HAND);

    Region labelBackground = new Region();
    labelBackground.setMinHeight(90);
    labelBackground.setMinWidth(105);
    labelBackground.relocate(-5, -15);
    labelBackground.setBackground(
        new Background(
            new BackgroundFill(Color.rgb(255,255,255, 0.5), new CornerRadii(5), null)
        )
    );
    label = new Text();
    label.setFont(Font.font("SourceCodePro", FontWeight.NORMAL, 12));

    labelGroup = labelHook(new Pane(labelBackground, label));
    labelGroup.relocate(BrickNode.WIDTH_BRICK + 15, -BrickNode.HEIGHT_BRICK + 30);

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

  public void setLabel(String label) {
    this.label.setText(label);
  }

  public void setRemoveBtnVisible(boolean isVisible){
    if(isVisible) cross.toFront();
    cross.setVisible(isVisible);
  }

  public abstract BrickData getBrick();

  public abstract void setRotateBrickSymbol(double angel);

  private void initializeMouseListeners() {
    addDragNDropSupport();

    this.setOnMouseEntered(event -> {
      this.toFront();
      // super.getChildren().addAll(labelBackground, label);
      super.getChildren().addAll(labelGroup);
    });
    this.setOnMouseExited (event -> super.getChildren().removeAll(labelGroup));
//    this.setOnMouseExited (event -> super.getChildren().removeAll(labelBackground, label));

    this.setOnScroll( e -> {
      int dAngle = 0;
      // need to be in separate if statements to work properly
      if(e.getDeltaY() < 0) dAngle = -2;
      if(e.getDeltaY() > 0) dAngle =  2;

      double targetAngle = (brickData.faceAngle.getValue() + dAngle) % 360;
      if (targetAngle < 0) targetAngle += 360;
      controller.rotate(targetAngle, brickData);
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

  private Line createCrossLine(boolean isMirrored) {
    Line line = new Line(0, 0, 8, 8);
    // style properties
    line.setStroke(Color.rgb(255, 255, 255));
    line.setStrokeType(StrokeType.CENTERED);
    line.setStrokeLineCap(StrokeLineCap.ROUND);
    line.setStrokeWidth(2.0);

    if (isMirrored) line.setScaleX(-1.0); // mirroring
    return line;
  }
}