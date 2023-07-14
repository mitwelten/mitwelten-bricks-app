/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.controller;

import ch.fhnw.mitwelten.bricksapp.model.BrickType;
import ch.fhnw.mitwelten.bricksapp.model.Garden;
import ch.fhnw.mitwelten.bricksapp.model.Notification.NotificationType;
import ch.fhnw.mitwelten.bricksapp.model.brick.BrickData;
import ch.fhnw.mitwelten.bricksapp.util.Constants;
import ch.fhnw.mitwelten.bricksapp.util.IOHandler;
import ch.fhnw.mitwelten.bricksapp.util.Location;
import ch.fhnw.mitwelten.bricksapp.util.mvcbase.ControllerBase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MetaDataController extends ControllerBase<Garden> {

  public static final String PAX_LOCATION_URL   = "https://data.mitwelten.org/api/v3/sensordata/pax_locations";
  public static final String PAX_DEPLOYMENT_URL = "https://data.mitwelten.org/api/v3/deployment/";

  private final BiConsumer<NotificationType, String> createNotification;
  private int simulatedIdCounter = 0;
  private final Set<String> ids;

  protected MetaDataController(Garden model, BiConsumer<NotificationType, String> createNotification) {
    super(model);
    this.createNotification = createNotification;
    this.ids = new HashSet<>();
    File paxLocationFile = new File(Constants.FILE_LOCATION_PATH + "paxLocation");
    isFileDataOutdated(paxLocationFile);
  }

  public boolean isValidId(String id) {
    if(id == null || id.equals("")){
      createNotification.accept(
          NotificationType.ERROR,
          "Error: Brick ID: ID must not be empty!"
      );
      return true;
    }
    if(!ids.add(id)) {
      createNotification.accept(
          NotificationType.ERROR,
          "Error: Brick ID: ID is already assigned"
      );
      return true;
    }
    return false;
  }

  public String getSimulatedId() {
    return Constants.SIM_ID_PREFIX + simulatedIdCounter++;
  }

  public boolean removeId(BrickData data) {
    String id = data.getID();
    if (!ids.remove(id)){
      createNotification.accept(
          NotificationType.ERROR,
          "Remove Brick: Id " + id + " not assigned!"
      );
      return false;
    }
    return true;
  }

  public Map<BrickType, Set<String>> initIds() {
    return Arrays.stream(BrickType.values())
        .collect(Collectors.toMap(
            Function.identity(),
            type -> loadIds(type.toString()))
        );
  }

  private Set<String> loadIds(String fileName) {
    File file = new File(Constants.FILE_ID_PATH + fileName);
    // if file older than 1h
    if(BrickType.PAX.toString().equals(fileName)) isFileDataOutdated(file);

    Optional<List<String>> result = IOHandler.readFromFile(file);
    if(result.isPresent()) {
      return new TreeSet<>(result.get());
    }
    return Collections.emptySet();
  }

  // is called from ctor and GUI
  private synchronized void isFileDataOutdated(File file) {
    if (System.currentTimeMillis() - file.lastModified() > 3.6e6) {
      refreshPaxData();
    }
  }

  private void refreshPaxData() {
    System.out.println("Updating PAX data...");
    updateModel(set(model.isLoading, true));

    new Thread(() -> {

      List<String> allNodeLabels = new ArrayList<>(Collections.emptyList());
      Map<String, Location> paxLocations = new HashMap<>(Collections.emptyMap());
      String allJsonIds = IOHandler.jsonFromUrl(PAX_LOCATION_URL,"");
      JSONArray allIds = new JSONArray(allJsonIds);

      for (int i = 0; i < allIds.length(); i++) {
        JSONObject deployment = allIds.getJSONObject(i);
        int deployment_id = deployment.getInt("deployment_id");
        String deploymentDatas = IOHandler.jsonFromUrl(PAX_DEPLOYMENT_URL, String.valueOf(deployment_id));
        try {
          String nodeLabel = new JSONObject(deploymentDatas).getJSONObject("node").getString("node_label");
          allNodeLabels.add(nodeLabel);
          try {
            JSONObject location = new JSONObject(deploymentDatas).getJSONObject("location");
            paxLocations.put(nodeLabel, new Location(location.getDouble("lat"), location.getDouble("lon")));
          } catch(Exception e){
            System.err.println("Could not fetch deployment location data!\n" + e.getMessage());
          }
        } catch(Exception e){
          System.err.println("Could not fetch PAX deployment data!\n" + e.getMessage());
        }
      }

      IOHandler.writeToFile(
          new File(Constants.FILE_ID_PATH + "Pax"),
          allNodeLabels.stream().map(s -> s + "\n").toList()
      );

      IOHandler.writeToFile(new File(Constants.FILE_LOCATION_PATH + "paxLocation"),
          paxLocations.keySet()
              .stream()
              .map(key -> {
                Location l = paxLocations.get(key);
                return key + "," + l.lat() + "," + l.lon() + "\n";
              }).toList());

      System.out.println("PAX data updated!");
      updateModel(set(model.isLoading, false));

    }).start();
  }

  public Map<String, Location> initPaxLocations() {
    Map<String, Location> resultMap = Collections.emptyMap();
    File locationFile = new File(Constants.FILE_LOCATION_PATH + "paxLocation");
    if (!locationFile.exists()) refreshPaxData();

    Optional<List<String>> content = IOHandler.readFromFile(locationFile);
    if(content.isPresent()){
      resultMap = content.get().stream()
          .map(line -> line.split(","))
          .collect(Collectors.toMap(
              words -> words[0],
              words -> new Location(Double.parseDouble(words[1]), Double.parseDouble(words[2]))));
    }
    return resultMap;
  }
}