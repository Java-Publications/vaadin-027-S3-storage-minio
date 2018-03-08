package org.rapidpm.vaadin.ui.server.imageservice;

import com.vaadin.shared.Registration;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import static java.util.concurrent.ConcurrentHashMap.newKeySet;
import static org.rapidpm.vaadin.ui.server.imageservice.ImageFunctions.nextImageName;


/**
 *
 */
public class ImageTimerService {


  private ImageTimerService() {
  }

  //TODO JVM static
  private static final Set<ImagePushListener> REGISTRY = newKeySet();

  private static final Timer TIMER = new Timer(true);

  public interface ImagePushListener {
    void updateImage(final String imageID);
  }


  public static Registration register(ImagePushListener imagePushListener) {

    REGISTRY.add(imagePushListener);

    return () -> {
      REGISTRY.remove(imagePushListener);
      Logger.getAnonymousLogger().info("removed registration");
    };
  }

  //TODO run every 2 sec -> Timer
  public static void updateImages() {
    // not nice coupled
    REGISTRY.forEach(e -> e.updateImage(nextImageName().get()));
  }

  // TODO not perfect
  static {
    TIMER.scheduleAtFixedRate(
        new TimerTask() {
          @Override
          public void run() {
            ImageTimerService.updateImages();
          }
        },
        5_000,
        2_000
    );
  }
}
