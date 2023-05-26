package ch.fhnw.iotbricksimulator.view.brick;

import ch.fhnw.iotbricksimulator.controller.ApplicationController;
import ch.fhnw.iotbricksimulator.model.brick.BrickData;
import ch.fhnw.iotbricksimulator.model.brick.DistanceBrickData;
import ch.fhnw.iotbricksimulator.util.Constants;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeType;

import java.util.Objects;

public class DistancePlacement extends BrickPlacement {

  private final double VIEW_PORT_RADIUS = 25.0;
  private final DistanceBrickData brick;

  private Group     distanceShape;
  private BrickNode brickIcon;
  private Arc       sensorActivity;

  public DistancePlacement(ApplicationController controller, BrickData brick) {
    super(controller, brick, () -> controller.removeBrick(brick));
    this.brick = (DistanceBrickData) brick;
    initializeControls();
    layoutControls();
  }

  public void setHighlighted(boolean isHighlighted) {
    if (isHighlighted) {
      brickIcon.getBody().setStrokeType(StrokeType.INSIDE);
      brickIcon.getBody().setStroke(Color.YELLOW);
    } else {
      brickIcon.getBody().setStrokeType(StrokeType.INSIDE);
      brickIcon.getBody().setStroke(null);
    }
  }

  private void initializeControls() {
    brickIcon = new BrickNode(Color.RED);

    Arc viewPort   = createViewPortArc(VIEW_PORT_RADIUS, Color.grayRgb(100, 0.7));
    sensorActivity = createViewPortArc(0.0,              Color.rgb(205, 205, 0, 0.4));

    distanceShape = new Group(viewPort, sensorActivity, brickIcon);
    distanceShape.setRotate(faceAngle);
  }

  private Arc createViewPortArc(double radius, Color color) {
    Arc arc = new Arc(
        BrickNode.CENTER_X,
        15,
        radius,
        radius,
        45.0,
        90
    );
    arc.setType(ArcType.ROUND);
    arc.setFill(color);
    return arc;
  }

  public void setRotateBrickSymbol(double angel){
    distanceShape.setRotate(angel);
  }

  private void layoutControls() {
    super.getChildren().addAll(distanceShape);
  }

  @Override
  public DistanceBrickData getBrick() {
    return brick;
  }

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