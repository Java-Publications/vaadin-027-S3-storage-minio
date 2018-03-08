package org.rapidpm.vaadin.ui.server.imageservice;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

  static Function<String, byte[]> failedImage() {
    return (imageID) -> {
      BufferedImage image = new BufferedImage(512, 512,
                                              BufferedImage.TYPE_INT_RGB
      );
      Graphics2D drawable = image.createGraphics();
      drawable.setStroke(new BasicStroke(5));
      drawable.setColor(Color.WHITE);
      drawable.fillRect(0, 0, 512, 512);
      drawable.setColor(Color.BLACK);
      drawable.drawOval(50, 50, 300, 300);

      drawable.setFont(new Font("Montserrat", Font.PLAIN, 20));
      drawable.drawString(imageID, 75, 216);
      drawable.setColor(new Color(0, 165, 235));
      try {
        // Write the image to a buffer
        ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", imagebuffer);

        // Return a stream from the buffer
        return imagebuffer.toByteArray();
      } catch (IOException e) {
        return new byte[0];
      }
    };
  }

  static Function<String, InputStream> failedImageAsInputStream() {
    return (imageID) -> failedImage()
        .andThen(ByteArrayInputStream::new)
        .apply(imageID);
  }


}
