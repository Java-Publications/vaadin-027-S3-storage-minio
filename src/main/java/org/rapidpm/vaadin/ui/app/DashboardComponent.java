package org.rapidpm.vaadin.ui.app;

import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Button;
import com.vaadin.ui.Composite;
import com.vaadin.ui.*;
import com.vaadin.ui.Image;
import com.vaadin.ui.TextField;
import io.minio.MinioClient;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.frp.model.Result;
import org.rapidpm.vaadin.ui.server.imageservice.s3.Coordinates;
import org.rapidpm.vaadin.ui.server.imageservice.s3.minio.MinioClientFunctions.BlobCoordinates;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.System.nanoTime;
import static org.rapidpm.frp.model.Result.failure;
import static org.rapidpm.frp.model.Result.success;
import static org.rapidpm.vaadin.ui.server.imageservice.BlobImagePushService.register;
import static org.rapidpm.vaadin.ui.server.imageservice.s3.minio.MinioClientFunctions.client;
import static org.rapidpm.vaadin.ui.server.imageservice.s3.minio.MinioClientFunctions.getObj;
import static org.rapidpm.vaadin.ui.server.imageservice.s3.minio.MinioClientFunctions.imageStream;

/**
 *
 */
public class DashboardComponent extends Composite implements HasLogger {

  public static final String DEFAULT_BUCKET_NAME = "images";

//  private Function<String, Image> createImage() {
//    return (imageID) -> new Image(null, createImageResource().apply(imageID));
//  }



  public Function<String, byte[]> failedImage() {
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

  private Function<String, StreamResource> createImageResource() {
    return (imageID) ->
        (
            (minioClient.isPresent())
            ? imageStream()
                .apply(minioClient.get(), new BlobCoordinates(DEFAULT_BUCKET_NAME, imageID))
                .ifFailed(failed -> logger().warning(failed))
                .map(bytes -> (StreamSource) () -> bytes)
                .map(imageSource -> new StreamResource(imageSource, imageID + "." + nanoTime()))
            : success(
                new StreamResource((StreamSource) () -> new ByteArrayInputStream(failedImage().apply(imageID)),
                                   imageID + "." + nanoTime()
                ))
        )
            .ifFailed(failed -> logger().warning(failed))
            .ifPresent(s -> s.setCacheTime(0))
            .get();
  }

  public Supplier<Coordinates> access() {
    return () -> new Coordinates(accessPoint.getValue(),
                                 accessKey.getValue(),
                                 secKey.getValue()
    );
  }

  private final TextField accessPoint = new TextField("Access point");
  private final TextField accessKey   = new TextField("accessKey");
  private final TextField secKey      = new TextField("secKey");
  private final TextField bucketName  = new TextField("bucketName");
  private final Button    connect     = new Button("connect");

  private final FormLayout layout = new FormLayout(accessPoint,
                                                   accessKey,
                                                   secKey,
                                                   bucketName,
                                                   connect
  );

  private Image  image      = new Image();
  private Layout mainLayout = new VerticalLayout();

  private Registration        registration;
  private Result<MinioClient> minioClient = failure("not yet set..");

  public DashboardComponent() {
  }

  @PostConstruct
  public void postConstruct() {
    accessPoint.setValue("http://127.0.0.1:9000");
    accessKey.setValue("UU0OL5JZLUTTOWXWDF58");
    secKey.setValue("nRkDGo84n+sYyTeaOckycOaZ7qW4YWaj/mXpnc7q");
    bucketName.setValue(DEFAULT_BUCKET_NAME);

    mainLayout.addComponents(layout, image);
    setCompositionRoot(mainLayout);
    connect.addClickListener((Button.ClickListener) event -> {
      if (registration == null) {
        minioClient = client()
            .apply(access().get())
            .ifFailed(failed -> logger().warning(failed));
        registration = register(imgID -> {
          final UI current = image.getUI();
          current
              .access(() -> {
                logger().info("DashboardComponent - imgID = " + imgID);
                image.setSource(createImageResource().apply(imgID));
              });
        });
        connect.setCaption("disconnect");
//          accessPoint.setValue("");
//          accessKey.setValue("");
//          secKey.setValue("");
      } else {
        registration.remove();
        minioClient = failure("not yet set..");
        connect.setCaption("connect");
      }
    });
  }

  //TODO to avoid Memory Leaks
  @Override
  public void detach() {
    registration.remove();
    super.detach();
  }
}