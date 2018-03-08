package org.rapidpm.vaadin.ui.app;

import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.Registration;
import com.vaadin.ui.*;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.vaadin.ui.server.imageservice.s3.BlobCoordinates;
import org.rapidpm.vaadin.ui.server.imageservice.s3.Coordinates;

import java.io.InputStream;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static java.lang.System.nanoTime;
import static org.rapidpm.vaadin.ui.server.imageservice.ImageFunctions.failedImageAsInputStream;
import static org.rapidpm.vaadin.ui.server.imageservice.ImageTimerService.register;
import static org.rapidpm.vaadin.ui.server.imageservice.s3.MinioClientFunctions.*;

/**
 *
 */
public class DashboardComponent extends Composite implements HasLogger {

  /**
   * Vaadin needed transformation
   *
   * @return
   */
  private BiFunction<InputStream, String, StreamResource> asStreamSource() {
    return (is, fileName) -> new StreamResource(() -> is, fileName);
  }

  private Supplier<Coordinates> access() {
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

  private Registration registration;

  public DashboardComponent postConstruct() {
    accessPoint.setValue("http://127.0.0.1:9999");
    accessKey.setValue("minio");
    secKey.setValue("minio123");
    bucketName.setValue(DEFAULT_BUCKET_NAME);

    mainLayout.addComponents(layout, image);
    setCompositionRoot(mainLayout);
    connect.addClickListener((Button.ClickListener) event -> {
      if (registration == null) {
        registration = register(imageID -> client()
            .apply(access().get())
            .ifPresentOrElse(
                minioClient -> imageStream()
                    .apply(minioClient, new BlobCoordinates(DEFAULT_BUCKET_NAME, imageID))
                    .ifFailed(failed -> logger().warning(failed))
                    .map(bytes -> new StreamResource(
                        (StreamSource) () -> bytes,
                        imageID + "." + nanoTime()
                    ))
                    .ifPresentOrElse(
                        ok -> image.getUI()
                                   .access(() -> image.setSource(ok)),
                        failed -> {
                          logger().warning(failed);
                          image.getUI()
                               .access(() -> image.setSource(
                                   asStreamSource()
                                       .apply(failedImageAsInputStream().apply(imageID),
                                              imageID
                                       ))
                               );
                        }
                    ),
                failed -> logger().warning(failed)
            ));
        connect.setCaption("disconnect");
//          accessPoint.setValue("");
//          accessKey.setValue("");
//          secKey.setValue("");
      } else {
        registration.remove();
        registration = null;
        connect.setCaption("connect");
      }
    });
    return this;
  }

  //TODO to avoid Memory Leaks
  @Override
  public void detach() {
    if (registration != null) registration.remove();
    super.detach();
  }
}