/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.model.brick;

import ch.fhnw.imvs.bricks.actuators.ServoBrick;
import ch.fhnw.mitwelten.bricksapp.util.mvcbase.ObservableValue;

public class ServoBrickData extends BrickData {

    public final ObservableValue<Double> mostActiveAngle;
    public final ObservableValue<Double> viewPortAngle;
    private final ServoBrick inner;

    public ServoBrickData(ServoBrick inner) {
        super(inner);
        this.inner = inner;
        mostActiveAngle = new ObservableValue<>(0d);
        viewPortAngle   = new ObservableValue<>(0d);
    }

    public void getPosition(int i) {
        inner.setPosition(i);
    }

    @Override
    public String toStringFormatted() {
        return super.toStringFormatted()
            + "\nval:\t:" + Math.round(mostActiveAngle.getValue());
    }

    @Override
    public String toString() {
        return "ServoBrick" + "," + super.toString();
    }
}