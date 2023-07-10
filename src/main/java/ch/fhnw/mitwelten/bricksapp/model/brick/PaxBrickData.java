/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.model.brick;

import ch.fhnw.imvs.bricks.sensors.PaxBrick;
import ch.fhnw.mitwelten.bricksapp.util.mvcbase.ObservableValue;

public class PaxBrickData extends BrickData {

    public final ObservableValue<Integer> value;
    public final ObservableValue<Boolean> isMostActive;
    private final PaxBrick inner;

    public PaxBrickData(PaxBrick inner) {
        super(inner);
        value        = new ObservableValue<>(0);
        isMostActive = new ObservableValue<>(false);
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