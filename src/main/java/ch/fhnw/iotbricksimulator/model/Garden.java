package ch.fhnw.iotbricksimulator.model;

import ch.fhnw.imvs.bricks.mock.MockProxy;
import ch.fhnw.imvs.bricks.mqtt.MqttProxy;
import ch.fhnw.iotbricksimulator.model.Notification.Notification;
import ch.fhnw.iotbricksimulator.model.brick.DistanceBrickData;
import ch.fhnw.iotbricksimulator.model.brick.ServoBrickData;
import ch.fhnw.iotbricksimulator.util.Constants;
import ch.fhnw.iotbricksimulator.util.mvcbase.ObservableValue;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Garden {
  public final ObservableValue<List<DistanceBrickData>> sensors             = new ObservableValue<>(new CopyOnWriteArrayList<>());
  public final ObservableValue<List<ServoBrickData>>    actuators           = new ObservableValue<>(new CopyOnWriteArrayList<>());
  public final ObservableValue<Boolean>                 isLoading           = new ObservableValue<>(false);
  public final ObservableValue<Boolean>                 removeButtonVisible = new ObservableValue<>(false);
  public final ObservableValue<Deque<Notification>>     notifications       = new ObservableValue<>(new ArrayDeque<>());

  public final MockProxy mockProxy = MockProxy.fromConfig(Constants.BASE_URL);
  public final MqttProxy mqttProxy = MqttProxy.fromConfig(Constants.BASE_URL);
}