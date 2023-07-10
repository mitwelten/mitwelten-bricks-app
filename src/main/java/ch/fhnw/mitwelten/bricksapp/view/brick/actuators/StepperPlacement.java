/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.view.brick.actuators;

import ch.fhnw.mitwelten.bricksapp.controller.ApplicationController;
import ch.fhnw.mitwelten.bricksapp.model.brick.actuators.StepperBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.impl.ActuatorBrickData;
import ch.fhnw.mitwelten.bricksapp.view.brick.ActuatorPlacement;
import ch.fhnw.mitwelten.bricksapp.view.brick.BrickNode;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;


public class StepperPlacement extends ActuatorPlacement {

  private final StepperBrickData brick;
  private final ApplicationController controller;

  private Group stepperShape;
  private Rotate mostActiveSensorAngle;
  private Rotate frontViewAngle;

  public StepperPlacement(ApplicationController controller, ActuatorBrickData brick) {
    super(controller, brick, () -> controller.removeBrick(brick));
    this.brick = (StepperBrickData) brick;
    this.controller = controller;

    initializeControls();
    layoutControls();
  }

  private void initializeControls() {
    final double outerCircleRadius = ((BrickNode.SYMBOL_HEIGHT - BrickNode.BRICK_HEIGHT) / 2) + 3;
    final double innerCircleRadius = outerCircleRadius - 6;

    mostActiveSensorAngle = new Rotate();
    frontViewAngle        = new Rotate();
    Line mostActiveSensorIndicator = createLine(outerCircleRadius, mostActiveSensorAngle);
    Line frontViewIndicator        = createLine(innerCircleRadius, frontViewAngle);

    Circle outerCircle = new Circle(BrickNode.CENTER_X, BrickNode.CENTER_Y, outerCircleRadius);
    Circle innerCircle = new Circle(BrickNode.CENTER_X, BrickNode.CENTER_Y, innerCircleRadius);
    innerCircle.setFill  (Color.LIGHTGRAY);
    outerCircle.setFill  (Color.GREY);
    innerCircle.setStroke(Color.BLACK);
    outerCircle.setStroke(Color.BLACK);

    BrickNode brickIcon   = new BrickNode(Color.BLUE);

    stepperShape = new Group(
        brickIcon,
        outerCircle,
        mostActiveSensorIndicator,
        innerCircle,
        frontViewIndicator
    );
    stepperShape.setRotate(faceAngle);
  }

  @Override
  protected Pane labelHook(Pane group) {
    Pane anchorPane = new AnchorPane();
    Button fnTest = new Button();
    fnTest.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    fnTest.getStyleClass().add("icon-button");

    fnTest.setTooltip(new Tooltip("Run Function Test!"));

    Region icon = new Region();
    icon.getStyleClass().add("icon");
    fnTest.setGraphic(icon);

    AnchorPane.setBottomAnchor(fnTest, 15.0);
    AnchorPane.setRightAnchor (fnTest, 5.0);

    fnTest.setOnAction(e -> controller.functionTest(brick, new int[]{ 0, 90, 180, 270, 360, 180, 0 }));

    anchorPane.getChildren().addAll(group, fnTest);
    return anchorPane;
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
    stepperShape.setRotate(angel);
  }

  public void setTargetValue(double angle) {
    mostActiveSensorAngle.setAngle(angle);
    frontViewAngle.setAngle(180 + brick.getPosition() - brick.faceAngle.getValue());
  }

  private void layoutControls() {
    super.getChildren().addAll(stepperShape);
  }

  @Override
  public StepperBrickData getBrick() {
    return brick;
  }
}