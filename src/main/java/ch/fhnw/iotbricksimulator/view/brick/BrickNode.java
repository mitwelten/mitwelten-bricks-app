package ch.fhnw.iotbricksimulator.view.brick;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class BrickNode extends Group {

  public static final double HEIGHT_BRICK  = 22;
  public static final double WIDTH_BRICK   = 34;
  public static final double SYMBOL_HEIGHT = 45;
  public static final double SYMBOL_WIDTH  = 45;

  public static final double CENTER_X = BrickNode.SYMBOL_WIDTH  / 2;
  public static final double CENTER_Y = BrickNode.SYMBOL_HEIGHT / 2;

  public static final double BRICK_RADIUS = 5.0;

  private final Color color;

  private Line      frontIndicator;
  private Rectangle body;

  public BrickNode(Color color){
    this.color = color;
    initializeControls();
    layoutControls();
  }

  public Rectangle getBody() {
    return body;
  }

  private void layoutControls() {
    this.getChildren().addAll(frontIndicator ,body);
  }

  private void initializeControls() {
    frontIndicator = new Line(
        CENTER_X,
        CENTER_Y,
        CENTER_X,
        0
    );
    body = new Rectangle(CENTER_Y - WIDTH_BRICK / 2, CENTER_X - HEIGHT_BRICK / 2, WIDTH_BRICK, HEIGHT_BRICK);
    body.setArcHeight(BRICK_RADIUS);
    body.setArcWidth (BRICK_RADIUS);

    frontIndicator.setFill(Color.BLACK);
    body          .setFill(color);

  }
}