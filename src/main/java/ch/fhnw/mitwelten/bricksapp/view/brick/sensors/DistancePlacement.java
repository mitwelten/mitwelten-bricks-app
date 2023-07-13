/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.view.brick.sensors;

import ch.fhnw.mitwelten.bricksapp.controller.ApplicationController;
import ch.fhnw.mitwelten.bricksapp.model.brick.impl.SensorBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.sensors.DistanceBrickData;
import ch.fhnw.mitwelten.bricksapp.util.Constants;
import ch.fhnw.mitwelten.bricksapp.view.brick.BrickNode;
import ch.fhnw.mitwelten.bricksapp.view.brick.SensorPlacement;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Rotate;

import java.util.Objects;

public class DistancePlacement extends SensorPlacement {

  private final double VIEW_PORT_RADIUS = 25.0;
  private final DistanceBrickData brick;

  private Group     distanceShape;
  private Arc       sensorActivity;
  private Rectangle highlightBorder;
  private Rotate    brickRotation;

  public DistancePlacement(ApplicationController controller, SensorBrickData brick) {
    super(controller, brick, () -> controller.removeBrick(brick), Color.RED);
    this.brick = (DistanceBrickData) brick;
    initializeControls();
    layoutControls();
  }

  public void setHighlighted(boolean isHighlighted) {
    if (isHighlighted) {
      highlightBorder.setStrokeType(StrokeType.INSIDE);
      highlightBorder.setStroke(Color.YELLOW);
    } else {
      highlightBorder.setStrokeType(StrokeType.INSIDE);
      highlightBorder.setStroke(null);
    }
  }

  private void initializeControls() {
    Arc viewPort   = createViewPortArc(VIEW_PORT_RADIUS, Color.grayRgb(100, 0.7));
    sensorActivity = createViewPortArc(0.0,              Color.rgb(205, 205, 0, 0.4));

    highlightBorder= new Rectangle(
        CENTER_Y - BRICK_WIDTH / 2,
        CENTER_X - BRICK_HEIGHT / 2,
        BRICK_WIDTH,
        BRICK_HEIGHT
    );
    highlightBorder.setArcHeight(BRICK_RADIUS);
    highlightBorder.setArcWidth (BRICK_RADIUS);
    highlightBorder.setStrokeWidth(1.5);
    highlightBorder.setFill(Color.TRANSPARENT);
    highlightBorder.setStroke(null);

    distanceShape = new Group(viewPort, sensorActivity);
    distanceShape.setRotate(faceAngle);

    brickRotation = new Rotate(0.0, BrickNode.CENTER_X, BrickNode.CENTER_Y);
    distanceShape  .getTransforms().add(brickRotation);
    highlightBorder.getTransforms().add(brickRotation);
  }

  private Arc createViewPortArc(double radius, Color color) {
    Arc arc = new Arc(
        CENTER_X,
        -5,
        radius,
        radius,
        45.0,
        90
    );
    arc.setType(ArcType.ROUND);
    arc.setFill(color);
    return arc;
  }

  private void layoutControls() {
//    distanceShape.relocate(-BrickNode.SYMBOL_WIDTH / 2, -BrickNode.SYMBOL_HEIGHT / 2);
    super.getChildren().addAll(distanceShape, highlightBorder);
    distanceShape.toBack();
  }

  public void setRotateBrickSymbol(double angle){
    brickRotation.setAngle(angle);
    super.setRotateBrickSymbol(angle);
  }

  @Override
  public DistanceBrickData getBrick() {
    return brick;
  }

  @Override
  public void setActivityValue(int value) {
    // Example: 25 - (25 / (max_sensor_value)) * current_value
    // sensor value is inverse for distance measurement
    double radius = VIEW_PORT_RADIUS - (VIEW_PORT_RADIUS / Constants.MAX_SENSOR_VALUE) * value;
    sensorActivity.setRadiusX(radius);
    sensorActivity.setRadiusY(radius);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DistancePlacement that = (DistancePlacement) o;
    return Objects.equals(brick, that.brick);
  }

  @Override
  public int hashCode() {
    return Objects.hash(brick);
  }
}