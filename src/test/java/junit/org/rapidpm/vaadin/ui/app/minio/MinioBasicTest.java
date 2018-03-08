package junit.org.rapidpm.vaadin.ui.app.minio;

import com.vaadin.server.StreamResource;
import io.minio.MinioClient;
import org.junit.jupiter.api.*;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.frp.functions.CheckedBiFunction;
import org.rapidpm.frp.model.Pair;
import org.rapidpm.vaadin.ui.server.imageservice.s3.Coordinates;
import org.rapidpm.vaadin.ui.server.imageservice.s3.BlobCoordinates;
import org.rapidpm.vaadin.ui.server.imageservice.s3.Blob;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.lang.System.nanoTime;
import static org.rapidpm.vaadin.ui.server.imageservice.s3.MinioClientFunctions.DEFAULT_BUCKET_NAME;
import static org.rapidpm.vaadin.ui.server.imageservice.s3.MinioClientFunctions.*;

/**
 * docker pull minio/minio
 * docker run -p 9000:9000 minio/minio server ./_data/_minio
 * <p>
 * AccessKey: UNFKCH719PCV06WCB1D7
 * SecretKey: FC5U0PVqcr/LB8Z82W3+Im85iQUNd+gbwVCyPjHD
 */
public class MinioBasicTest implements HasLogger {


  public static final String CONTENT_TYPE = "text/plain;charset=utf-8";

  public Supplier<Coordinates> access() {
    return () -> new Coordinates("http://127.0.0.1:9001",
                                 "minio",
                                 "minio123"
    );
  }

  public CheckedBiFunction<MinioClient, Pair<String, String>, Stream<String>> readToString() {
    return (minioClient, obj) -> {
      final InputStream inputStream = minioClient.getObject(obj.getT1(), obj.getT2());
      final Reader reader = new BufferedReader(
          new InputStreamReader(inputStream,
                                Charset.forName(StandardCharsets.UTF_8.name())
          ));
      return ((BufferedReader) reader).lines();
    };
  }

  public <T> Predicate<Pair<Stream<T>, Stream<T>>> compare() {
    return (p) -> {
      final Iterator<T> iter1  = p.getT1().iterator();
      final Iterator<T> iter2  = p.getT2().iterator();
      boolean           result = true;
      while (iter1.hasNext() && iter2.hasNext()) {
        if (!iter1.next().equals(iter2.next())) {
          result = false;
        }
      }
      return result;
    };
  }

  /**
   * Will show that the bucket is overwritten
   * every time. No error, no message
   */
  @Test
  void test001() {

    final String bucketName = "test001-bucket";
    final String objectName = "test001.txt";

    final String               timestamp             = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    final ByteArrayInputStream inputStreamHelloWorld = new ByteArrayInputStream(("Hello World - " + timestamp).getBytes());

    client()
        .apply(access().get())
        .thenCombine(bucketName, bucket())
        .ifFailed(Assertions::fail)
        .thenCombine(new Blob(bucketName,
                              objectName,
                              inputStreamHelloWorld,
                              CONTENT_TYPE
                     ),
                     putObj()
        )
        .ifFailed(Assertions::fail);
  }

  @Test
  void test002() {
    final String               bucketName            = "test002-bucket";
    final String               objectName            = "test002.txt";
    final String               timestamp             = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    final String               value                 = "Hello World - " + timestamp;
    final ByteArrayInputStream inputStreamHelloWorld = new ByteArrayInputStream(value.getBytes());

    client()
        .apply(access().get())
        .thenCombine(bucketName, bucket())
        .ifFailed(Assertions::fail)
        .thenCombine(new Blob(bucketName,
                              objectName,
                              inputStreamHelloWorld,
                              CONTENT_TYPE
                     ),
                     putObj()
        )
        .ifFailed(Assertions::fail);

    //reading now with right timestamp
    client()
        .apply(access().get())
        .ifFailed(Assertions::fail)
        .thenCombine(Pair.next(bucketName, objectName),
                     readToString()
        )
        .ifPresentOrElse(
            ok -> Assertions.assertTrue(this.<String>compare().test(new Pair<>(ok, Stream.of(value)))),
            Assertions::fail
        );
  }


  @Test
  @Disabled
  void test003() {
    final String imageID = "nasa_pic_00057.jpg";

    client()
        .apply(access().get())
        .ifFailed(Assertions::fail)
        .ifPresent(minioClient -> {
          final StreamResource streamResource = imageStream()
              .apply(minioClient, new BlobCoordinates(DEFAULT_BUCKET_NAME, imageID))
              .ifFailed(failed -> logger().warning(failed))
              .map(bytes -> (StreamResource.StreamSource) () -> bytes)
              .map(imageSource -> new StreamResource(imageSource, imageID + "." + nanoTime()))
              .get();
          Assertions.assertNotNull(streamResource);
        });
  }




}
