package ch.fhnw.iotbricksimulator.view.brick;

import ch.fhnw.iotbricksimulator.controller.ApplicationController;
import ch.fhnw.iotbricksimulator.model.brick.BrickData;
import ch.fhnw.iotbricksimulator.model.brick.ServoBrickData;
import javafx.scene.Group;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;

public class ServoPlacement extends BrickPlacement {

  private final ServoBrickData brick;
  private Group  servoShape;
  private Rotate mostActiveSensorAngle;
  private Rotate frontViewAngle;

  public ServoPlacement(ApplicationController controller, BrickData brick) {
    super(controller, brick, () -> controller.removeBrick(brick));
    this.brick = (ServoBrickData) brick;

    initializeControls();
    layoutControls();
  }

  private void initializeControls() {
    final double outerCircleRadius = ((BrickNode.SYMBOL_HEIGHT - BrickNode.HEIGHT_BRICK) / 2) + 3;
    final double innerCircleRadius = outerCircleRadius - 6;

    mostActiveSensorAngle = new Rotate();
    frontViewAngle        = new Rotate();
    Line mostActiveSensorIndicator = createLine(outerCircleRadius, mostActiveSensorAngle);
    Line frontViewIndicator        = createLine(innerCircleRadius, frontViewAngle);

    Circle outerCircle = new Circle(BrickNode.CENTER_X, BrickNode.CENTER_Y, outerCircleRadius);
    Circle innerCircle = new Circle(BrickNode.CENTER_X, BrickNode.CENTER_Y, innerCircleRadius);
    innerCircle.setFill(Color.LIGHTGRAY);
    outerCircle.setFill(Color.GREY);
    innerCircle.setStroke(Color.BLACK);
    outerCircle.setStroke(Color.BLACK);

    BrickNode brickIcon = new BrickNode(Color.BLUE);
    Region brickArea    = new Region();
    brickArea.setMinWidth (BrickNode.SYMBOL_WIDTH);
    brickArea.setMinHeight(BrickNode.SYMBOL_HEIGHT);
    BackgroundFill bgFill = new BackgroundFill(Color.TRANSPARENT, null, null);
    brickArea.setBackground(new Background(bgFill));

    servoShape = new Group(
        brickArea,
        brickIcon,
        outerCircle,
        mostActiveSensorIndicator,
        innerCircle,
        frontViewIndicator
    );
    servoShape.setRotate(faceAngle);
  }

  private Line createLine(double radius, Rotate angle) {
    Line indicator = new Line(BrickNode.CENTER_X, BrickNode.CENTER_Y, BrickNode.CENTER_X, BrickNode.CENTER_Y - radius);
    angle.setPivotX(BrickNode.CENTER_X);
    angle.setPivotY(BrickNode.CENTER_Y);
    indicator.getTransforms().addAll(angle);
    indicator.setStrokeWidth(2);
    return indicator;
  }

  public void setRotateBrickSymbol(double angel){
    servoShape.setRotate(angel);
  }

  public void setMostActiveSensorAngle(double angle) {
    mostActiveSensorAngle.setAngle(angle);
  }

  public void setFrontViewAngle(double angle) {
    frontViewAngle.setAngle(angle);
  }

  private void layoutControls() {
    super.getChildren().addAll(servoShape);
  }

  @Override
  public  ServoBrickData getBrick() {
    return brick;
  }
}