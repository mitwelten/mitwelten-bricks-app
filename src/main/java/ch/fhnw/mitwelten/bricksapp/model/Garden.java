/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.model;

import ch.fhnw.imvs.bricks.mock.MockProxy;
import ch.fhnw.imvs.bricks.mqtt.MqttProxy;
import ch.fhnw.mitwelten.bricksapp.model.Notification.Notification;
import ch.fhnw.mitwelten.bricksapp.model.brick.impl.ActuatorBrickData;
import ch.fhnw.mitwelten.bricksapp.model.brick.impl.SensorBrickData;
import ch.fhnw.mitwelten.bricksapp.util.Constants;
import ch.fhnw.mitwelten.bricksapp.util.mvcbase.ObservableValue;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Garden {
  public final ObservableValue<List<SensorBrickData>>   sensors             = new ObservableValue<>(new CopyOnWriteArrayList<>());
  public final ObservableValue<List<ActuatorBrickData>> actuators           = new ObservableValue<>(new CopyOnWriteArrayList<>());
  public final ObservableValue<Boolean>                 isLoading           = new ObservableValue<>(false);
  public final ObservableValue<Boolean>                 removeButtonVisible = new ObservableValue<>(false);
  public final ObservableValue<Boolean>                 runningUpdateLoop   = new ObservableValue<>(false);
  public final ObservableValue<Deque<Notification>>     notifications       = new ObservableValue<>(new ArrayDeque<>());

  public final MockProxy mockProxy = MockProxy.fromConfig(Constants.BASE_URL);
  public final MqttProxy mqttProxy = MqttProxy.fromConfig(Constants.BASE_URL);
}