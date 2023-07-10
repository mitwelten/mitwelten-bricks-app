/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.view.brick;

import javafx.scene.Group;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class BrickNode extends Group {

  public static final double BRICK_HEIGHT  = 22;
  public static final double BRICK_WIDTH   = 34;
  public static final double SYMBOL_HEIGHT = 45;
  public static final double SYMBOL_WIDTH  = 45;

  public static final double CENTER_X = BrickNode.SYMBOL_WIDTH  / 2;
  public static final double CENTER_Y = BrickNode.SYMBOL_HEIGHT / 2;

  public static final double BRICK_RADIUS = 5.0;

  private final Color color;

  private Line      frontIndicator;
  private Rectangle body;
  private Region    brickArea;

  public BrickNode(Color color){
    this.color = color;
    initializeControls();
    layoutControls();
  }

  public Rectangle getBody() {
    return body;
  }

  private void layoutControls() {
    this.getChildren().addAll(brickArea, frontIndicator ,body);
  }

  private void initializeControls() {
    brickArea      = new Region();
    BackgroundFill bgFill = new BackgroundFill(Color.TRANSPARENT, null, null);
    brickArea.setMinWidth (BrickNode.SYMBOL_WIDTH);
    brickArea.setMinHeight(BrickNode.SYMBOL_HEIGHT);
    brickArea.setBackground(new Background(bgFill));

    frontIndicator = new Line(
        CENTER_X,
        CENTER_Y,
        CENTER_X,
        0
    );
    body = new Rectangle(CENTER_Y - BRICK_WIDTH / 2, CENTER_X - BRICK_HEIGHT / 2, BRICK_WIDTH, BRICK_HEIGHT);
    body.setArcHeight(BRICK_RADIUS);
    body.setArcWidth (BRICK_RADIUS);

    frontIndicator.setFill(Color.BLACK);
    body          .setFill(color);

  }
}