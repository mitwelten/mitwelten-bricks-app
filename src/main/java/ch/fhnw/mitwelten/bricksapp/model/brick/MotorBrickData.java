/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.model.brick;

import ch.fhnw.imvs.bricks.impl.AnalogOutputBrick;
import ch.fhnw.mitwelten.bricksapp.util.mvcbase.ObservableValue;

public class MotorBrickData extends BrickData {

    public final ObservableValue<Double> mostActiveAngle;
    public final ObservableValue<Double> viewPortAngle;
    private final AnalogOutputBrick inner;
    private int target = 0;

    public MotorBrickData(AnalogOutputBrick inner) {
        super(inner);
        this.inner = inner;
        mostActiveAngle = new ObservableValue<>(0d);
        viewPortAngle   = new ObservableValue<>(0d);
    }

    public int getPosition(){
        return inner.getPosition();
    }

    public void setPosition(int i) {
        try {
            inner.setPosition(i);
            target = i;
        } catch (IllegalArgumentException e){
            System.err.println("Could not set target position!");
            System.err.println(e.getMessage());
        }
    }

    public int getTargetPosition(){
        return target;
    }

    @Override
    public String toStringFormatted() {
        return super.toStringFormatted()
            + "\nval:\t" + Math.round(mostActiveAngle.getValue());
    }

    @Override
    public String toString() {
        return "Actuator," + super.toString();
    }
}