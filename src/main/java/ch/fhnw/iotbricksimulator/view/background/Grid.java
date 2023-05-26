package ch.fhnw.iotbricksimulator.view.background;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import ch.fhnw.iotbricksimulator.util.Constants;

import static ch.fhnw.iotbricksimulator.util.Constants.*;

public class Grid extends Pane {

  public static final int GAP = 50;

  private static final double DECIMALS_FACTOR  =  10e4;

  public Grid(){
    drawGrid();
  }

  private void drawGrid(){
    double start = 0, end = Constants.WINDOW_WIDTH;
    int nofLines = WINDOW_HEIGHT / GAP;
    double coordinateCapLon = (RIGHT_LONG - LEFT_LONG) / nofLines;
    double coordinateCapLat = (TOP_LAT - BOTTOM_LAT)   / nofLines;


    for (int i = 1; i <= nofLines; i++) {
      Line horizontalLine = new Line(start, WINDOW_HEIGHT- (i * GAP), end, WINDOW_HEIGHT- (i * GAP));
      Line verticalLine   = new Line(i * GAP, start, i * GAP, end);
      horizontalLine.setStrokeWidth(0.2);
      verticalLine  .setStrokeWidth(0.2);

      drawVerticalLine(coordinateCapLon, i, verticalLine);
      drawHorizontalLine(coordinateCapLat, i, horizontalLine);
    }
  }

  private void drawHorizontalLine(double coordinateCap, int i, Line horizontalLine) {
    this.getChildren().add(
        new Group(
            new Text(
                horizontalLine.getStartX() + 5,
                horizontalLine.getStartY() - 5,
                String.valueOf(Math.round((BOTTOM_LAT + coordinateCap * i) * DECIMALS_FACTOR) / DECIMALS_FACTOR)
            ),
            horizontalLine
        )
    );
  }

  private void drawVerticalLine(double coordinateCap, int i, Line verticalLine) {
    Text label = new Text(
        verticalLine.getStartX() - 15,
        WINDOW_HEIGHT - 20,
        String.valueOf(Math.round((LEFT_LONG + coordinateCap * i) * DECIMALS_FACTOR) / DECIMALS_FACTOR)
    );
    label.setRotate(-90);
    this.getChildren().add(
        new Group(
            label,
            verticalLine
        )
    );
  }
}