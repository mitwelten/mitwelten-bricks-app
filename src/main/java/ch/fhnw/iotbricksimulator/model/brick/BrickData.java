package ch.fhnw.iotbricksimulator.model.brick;

import ch.fhnw.imvs.bricks.core.Brick;
import ch.fhnw.iotbricksimulator.util.Constants;
import ch.fhnw.iotbricksimulator.util.Util;
import ch.fhnw.iotbricksimulator.util.mvcbase.ObservableValue;
import ch.fhnw.iotbricksimulator.util.Location;

import java.util.Date;

public abstract class BrickData {

  public final ObservableValue<Location> location;
  public final ObservableValue<Double>   faceAngle;

  private final Brick inner;

  public BrickData(Brick inner){
    location   = new ObservableValue<>(new Location(Constants.SPAWN_POSITION_X, Constants.SPAWN_POSITION_Y));
    faceAngle  = new ObservableValue<>(0.0);
    this.inner = inner;
  }

  public String getID() {
    return inner.getID();
  }

  public double getBatteryVoltage() {
    return inner.getBatteryVoltage();
  }

  public Date getTimestamp() {
    return inner.getTimestamp();
  }

  public String getTimestampIsoUtc() {
    return inner.getTimestampIsoUtc();
  }

  public String toStringFormatted() {
    Location coordinates = Util.toCoordinates(location.getValue().lon(), location.getValue().lat());
    return inner.getID() +
//        "\nx: "   + brick.location.getValue().lon() +
//        "\ny: "   + brick.location.getValue().lat() +
        "\nx:\t"   + coordinates.lat() +
        "\ny:\t"   + coordinates.lon() +
        "\nfa:\t"  + faceAngle.getValue();
  }

  @Override
  public String toString() {
    return inner.getID()
        + "," + location.getValue().lat()
        + "," + location.getValue().lon()
        + "," + faceAngle.getValue();
  }
}
