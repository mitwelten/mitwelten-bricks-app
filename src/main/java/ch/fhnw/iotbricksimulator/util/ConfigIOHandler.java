package ch.fhnw.iotbricksimulator.util;

import ch.fhnw.iotbricksimulator.model.brick.BrickData;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ConfigIOHandler {

  public static boolean writeToFile(File file, List<? extends BrickData> bricks) {
    try (PrintWriter printWriter = new PrintWriter(file)) {
      printWriter.write("mock,brick,id,lat,long,faceAngle\n");
      bricks.stream()
          .map(s -> {
            boolean type = s.getID().contains("mock");
            return String.valueOf(type).concat(",").concat(s.toString());
          })
          .map(s -> s.concat("\n"))
          .peek(System.out::println)
          .forEach(printWriter::write);
    } catch (FileNotFoundException e) {
      System.err.println("Create CSV: File could not be created!");
      return false;
    }
    return true;
  }

  public static Optional<List<String>> readFromFile(File file) {
    List<String> allLines = new ArrayList<>(Collections.emptyList());
    try (
        FileInputStream inputStream = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      while ((line = br.readLine()) != null) {
        allLines.add(line);
      }
    } catch (IOException e) {
      System.err.println("Could not read file!");
      return Optional.empty();
    } catch (NullPointerException e) {
      System.err.println("No file found!");
      return Optional.empty();
    }
    return Optional.of(allLines);
  }
}