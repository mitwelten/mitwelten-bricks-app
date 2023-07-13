/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.view.brick.sensors;

import ch.fhnw.mitwelten.bricksapp.controller.ApplicationController;
import ch.fhnw.mitwelten.bricksapp.model.brick.BrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.impl.SensorBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.sensors.PaxBrickData;
import ch.fhnw.mitwelten.bricksapp.view.brick.BrickNode;
import ch.fhnw.mitwelten.bricksapp.view.brick.SensorPlacement;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.zip.GZIPOutputStream;

public class PaxPlacement extends SensorPlacement {

  private final PaxBrickData brick;

  private Label  valueLabel;
  private HBox   labelContainer;
  private Group  paxNode;
  private Circle outerCircle;

  public PaxPlacement(ApplicationController controller, SensorBrickData brick) {
    super(controller, brick, () -> controller.removeBrick(brick), Color.YELLOW);
    this.brick = (PaxBrickData) brick;
    initializeControls();
    layoutControls();
  }

  private void initializeControls() {
    valueLabel     = new Label("100");
    labelContainer = new HBox(valueLabel);

    outerCircle = new Circle(BrickNode.CENTER_X, BrickNode.CENTER_Y, BrickNode.BRICK_HEIGHT);
    outerCircle.setFill  (Color.rgb(255,255,200, 0.3));

    paxNode = new Group(outerCircle,labelContainer);
    valueLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 12));
  }

  private void layoutControls() {
    labelContainer.setMinWidth(BrickNode.SYMBOL_WIDTH);
    labelContainer.setMinHeight(BrickNode.SYMBOL_HEIGHT);
    labelContainer.setAlignment(Pos.CENTER);
    labelContainer.relocate(-BrickNode.SYMBOL_WIDTH / 2, -BrickNode.SYMBOL_HEIGHT / 2);
    super.getChildren().addAll(paxNode);
  }

  @Override
  public void setHighlighted(boolean isHighlighted) {}

  @Override
  public void setActivityValue(int value) {
    valueLabel.setText(String.valueOf(value));
    outerCircle.setRadius(BrickNode.BRICK_HEIGHT + (value / 2.0));
  }

  @Override
  public PaxBrickData getBrick() {
    return brick;
  }

  @Override
  public void setRotateBrickSymbol(double angel) {
    super.setRotateBrickSymbol(angel);
  }
}
