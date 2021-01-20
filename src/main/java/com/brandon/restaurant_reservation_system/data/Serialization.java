/*
 * Copyright (c) 2020 Brandon Murch
 */

package com.brandon.restaurant_reservation_system.data;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serialization {

  public static void serialize(String fileName, Object object) {
    FileOutputStream fileOutput = null;
    ObjectOutputStream outputStream = null;
    try {
      fileOutput = new FileOutputStream(fileName + ".ser");
      outputStream = new ObjectOutputStream(fileOutput);
      outputStream.writeObject(object);
      outputStream.close();
      fileOutput.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      close(fileOutput);
      close(outputStream);
    }
  }

  public static <T extends Updatable> boolean deserialize(String fileName, T object) {
    FileInputStream fileInput = null;
    ObjectInputStream objectInput = null;
    try {
      fileInput = new FileInputStream(fileName + ".ser");
      objectInput = new ObjectInputStream(fileInput);
      Object result = objectInput.readObject();
      object.update(result);
      return true;
    } catch (IOException | ClassNotFoundException e) {
      return false;
    } finally {
      close(objectInput);
      close(fileInput);
    }
  }

  private static void close(Closeable stream) {
    if (stream != null) {
      try {
        stream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

}


