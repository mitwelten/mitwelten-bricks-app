package ch.fhnw.mitwelten.bricksapp.controller;

import ch.fhnw.mitwelten.bricksapp.model.Garden;
import ch.fhnw.mitwelten.bricksapp.model.brick.sensors.DistanceBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.actuators.MotorBrickData;
import ch.fhnw.mitwelten.bricksapp.util.Constants;
import ch.fhnw.mitwelten.bricksapp.util.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppControllerTest {

  @Test
  void testRemoveButton() {
    //given
    Garden model         = new Garden();
    boolean initialState = model.removeButtonVisible.getValue();

    ApplicationController controller = new ApplicationController(model);

    //when
    controller.setRemoveButtonVisible(true);
    controller.awaitCompletion();

    //then
    assertEquals(!initialState, model.removeButtonVisible.getValue());

    //when
    controller.setRemoveButtonVisible(false);
    controller.awaitCompletion();

    //then
    assertEquals(initialState, model.removeButtonVisible.getValue());
  }

  @Test
  void testAddAndRemoveDistanceBrick() {
    //given
    Garden model = new Garden();
    ApplicationController controller = new ApplicationController(model);

    //when
    DistanceBrickData brick = controller.createMockSensor();
    controller.awaitCompletion();

    //then
    assertEquals(1, model.sensors.getValue().size());
    assertEquals(brick, model.sensors.getValue().get(0));

    //when
    controller.removeBrick(brick);
    controller.awaitCompletion();

    //then
    assertTrue(model.sensors.getValue().isEmpty());
  }

  @Test
  void testAddAndRemoveServoBrick() {
    //given
    Garden model = new Garden();
    ApplicationController controller = new ApplicationController(model);

    //when
    MotorBrickData brick = controller.createMockActuator();
    controller.awaitCompletion();

    //then
    assertEquals(1, model.actuators.getValue().size());
    assertEquals(brick, model.actuators.getValue().get(0));

    //when
    controller.removeBrick(brick);
    controller.awaitCompletion();

    //then
    assertTrue(model.actuators.getValue().isEmpty());
  }

  @Test
  void testMoveBrick() {
    //given
    Garden model = new Garden();
    ApplicationController controller = new ApplicationController(model);
    MotorBrickData servo    = controller.createMockActuator();
    DistanceBrickData distance = controller.createMockSensor();

    double lat = 123.45, lon = 54.321;
    Location target          = new Location(lat, lon);
    Location initialLocation = new Location(Constants.SPAWN_POSITION_X, Constants.SPAWN_POSITION_Y);

    //when
    controller.move(target, servo);
    controller.move(target, distance);
    controller.awaitCompletion();

    //then
    assertEquals(target, model.actuators.getValue().get(0).location.getValue());
    assertEquals(target, model.sensors.getValue().get(0).location.getValue());

    //when
    controller.move(initialLocation, servo);
    controller.move(initialLocation, distance);
    controller.awaitCompletion();

    //then
    assertEquals(initialLocation, model.actuators.getValue().get(0).location.getValue());
    assertEquals(initialLocation, model.sensors.getValue().get(0).location.getValue());
  }
}