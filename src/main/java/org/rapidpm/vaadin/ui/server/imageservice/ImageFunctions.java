package org.rapidpm.vaadin.ui.server.imageservice;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.concurrent.ThreadLocalRandom.current;

/**
 *
 */
public interface ImageFunctions {


  static Function<Integer, String> randomImageID() {
    return (boundary) -> format("%05d" , current().nextInt(boundary) + 1);
  }

  static Supplier<String> nextImageName() {
    return () -> "nasa_pic_" + randomImageID().apply(100) + ".jpg";
  }
}
