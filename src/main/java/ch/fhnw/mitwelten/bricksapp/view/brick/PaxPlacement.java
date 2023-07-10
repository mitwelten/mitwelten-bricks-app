/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.view.brick;

import ch.fhnw.mitwelten.bricksapp.controller.ApplicationController;
import ch.fhnw.mitwelten.bricksapp.model.brick.BrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.PaxBrickData;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PaxPlacement extends BrickPlacement {

  private PaxBrickData brick;
  private Label valueLabel;
  private BrickNode brickIcon;
  private Group paxNode;

  public PaxPlacement(ApplicationController controller, BrickData brick) {
    super(controller, brick, () -> controller.removeBrick(brick));
    this.brick = (PaxBrickData) brick;
    initializeControls();
    layoutControls();
  }

  private void initializeControls() {
    BackgroundFill bgFill = new BackgroundFill(Color.TRANSPARENT, null, null);
    Region brickArea = new Region();
    brickArea.setMinWidth (BrickNode.SYMBOL_WIDTH);
    brickArea.setMinHeight(BrickNode.SYMBOL_HEIGHT);
    brickArea.setBackground(new Background(bgFill));

    brickIcon  = new BrickNode(Color.YELLOW);
    valueLabel = new Label("0");
    valueLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
    paxNode    = new Group(brickArea, brickIcon, valueLabel);
  }

  private void layoutControls() {
    valueLabel.relocate(BrickNode.WIDTH_BRICK / 2, BrickNode.HEIGHT_BRICK / 2);
    super.getChildren().add(paxNode);
  }

  public void setActivityValue(int value) {
    valueLabel.setText(String.valueOf(value));
  }

  @Override
  public PaxBrickData getBrick() {
    return brick;
  }

  @Override
  public void setRotateBrickSymbol(double angel) {
    brickIcon.setRotate(angel);
  }
}
