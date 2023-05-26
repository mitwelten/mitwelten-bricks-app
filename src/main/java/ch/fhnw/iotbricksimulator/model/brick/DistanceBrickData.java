package ch.fhnw.iotbricksimulator.model.brick;

import ch.fhnw.imvs.bricks.sensors.DistanceBrick;
import ch.fhnw.iotbricksimulator.util.mvcbase.ObservableValue;

public class DistanceBrickData extends BrickData {

    public final ObservableValue<Integer> value;
    public final ObservableValue<Boolean> isMostActive;
    private final DistanceBrick inner;

    public DistanceBrickData(DistanceBrick inner) {
        super(inner);
        value        = new ObservableValue<>(0);
        isMostActive = new ObservableValue<>(false);
        this.inner   = inner;
    }

    public int getDistance() {
        return inner.getDistance();
    }

    @Override
    public String toStringFormatted() {
        return super.toStringFormatted()
            + "\nval:\t" + value.getValue();
    }

    @Override
    public String toString() {
        return "DistanceBrick" + "," + super.toString();
    }
}