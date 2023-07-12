/*----------------------------------------------------------------------------------------
 Copyright (c) 2023 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 Licensed under MIT License
 ---------------------------------------------------------------------------------------*/

package ch.fhnw.mitwelten.bricksapp.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ConfigIOHandler {

  public static boolean writeToFile(File file, List<String> bricks) {
    try (PrintWriter printWriter = new PrintWriter(file)) {
      bricks.forEach(printWriter::write);
    } catch (FileNotFoundException e) {
      System.err.println("Failed to wirte file" + file.getName());
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
      System.err.println("ConfigIOHandler: Could not read file! " + file.getName());
      return Optional.empty();
    } catch (NullPointerException e) {
      System.err.println("No file found!");
      return Optional.empty();
    }
    return Optional.of(allLines);
  }
}