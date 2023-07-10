/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.model.brick.sensors;

import ch.fhnw.imvs.bricks.sensors.PaxBrick;
import ch.fhnw.mitwelten.bricksapp.model.brick.impl.SensorBrickData;
import ch.fhnw.mitwelten.bricksapp.util.Location;
import ch.fhnw.mitwelten.bricksapp.util.mvcbase.ObservableValue;

public class PaxBrickData extends SensorBrickData {

    private final PaxBrick inner;

    public PaxBrickData(PaxBrick inner, Location location, double faceAngle) {
        super(inner, location, faceAngle);

        this.inner   = inner;
    }

    public int getValue() {
        return inner.getValue();
    }

    @Override
    public String toStringFormatted() {
        return super.toStringFormatted()
            + "\nval:\t" + value.getValue();
    }

    @Override
    public String toString() {
        return "Pax Brick" + "," + super.toString();
    }
}