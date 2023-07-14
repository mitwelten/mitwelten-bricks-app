/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.view.brick;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public  class BrickNode extends Group {

  public static final double BRICK_HEIGHT  = 22;
  public static final double BRICK_WIDTH   = 34;
  public static final double SYMBOL_HEIGHT = 44;
  public static final double SYMBOL_WIDTH  = 44;
  public static final double CENTER_X      = 0;
  public static final double CENTER_Y      = 0;

  public static final double BRICK_RADIUS = 5.0;

  private final Color color;

  private Group  brickShape;
  private Rotate brickRotation;

  public BrickNode(Color color){
    this.color = color;
    initializeControls();
    layoutControls();
  }

  private void layoutControls() {
    this.relocate(-22,-22);
    this.getChildren().addAll(brickShape);
  }

  private void initializeControls() {
    Line frontIndicator = new Line(
        CENTER_X,
        CENTER_Y,
        CENTER_X,
        -BRICK_HEIGHT
    );
    Rectangle body = new Rectangle(
        CENTER_Y - BRICK_WIDTH / 2,
        CENTER_X - BRICK_HEIGHT / 2,
        BRICK_WIDTH,
        BRICK_HEIGHT
    );

    body.setArcHeight(BRICK_RADIUS);
    body.setArcWidth (BRICK_RADIUS);

    brickRotation = new Rotate(0.0, BrickNode.CENTER_X, BrickNode.CENTER_Y);

    frontIndicator.setFill(Color.BLACK);
    body.setFill(color);

    brickShape = new Group(frontIndicator, body);
    brickShape.getTransforms().add(brickRotation);
  }

  public void setRotateBrickSymbol(double angle){
   brickRotation.setAngle(angle);
  }
}