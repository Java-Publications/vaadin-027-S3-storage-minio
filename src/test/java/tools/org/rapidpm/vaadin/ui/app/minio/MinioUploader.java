package tools.org.rapidpm.vaadin.ui.app.minio;

import org.rapidpm.frp.functions.CheckedFunction;
import org.rapidpm.frp.model.Result;
import org.rapidpm.vaadin.ui.server.imageservice.s3.Coordinates;
import org.rapidpm.vaadin.ui.server.imageservice.s3.Blob;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.Boolean.TRUE;
import static java.nio.file.Files.newDirectoryStream;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.StandardOpenOption.READ;
import static org.rapidpm.vaadin.ui.server.imageservice.s3.MinioClientFunctions.DEFAULT_BUCKET_NAME;
import static org.rapidpm.vaadin.ui.server.imageservice.s3.MinioClientFunctions.*;

/**
 *
 */
public class MinioUploader {

  private static final Consumer<String> logger = System.out::println;


  //TODO put here your target infos
  public static Supplier<Coordinates> access() {
    return () -> new Coordinates("http://127.0.0.1:9999",
                                 "minio",
                                 "minio123"
    );
  }


  public static CheckedFunction<String, DirectoryStream<Path>> dirStream() {
    return (path) -> newDirectoryStream(new File(path).toPath());
  }

  public static CheckedFunction<Path, Blob> imageBlob() {
    return (p) -> new Blob(
        DEFAULT_BUCKET_NAME,
        p.getFileName().toString(),
        newInputStream(p, READ),
        "image/jpg"
    );
  }

  /**
   * Example of nested exceptions, and how to deal with them
   * @param args
   */
  public static void main(String[] args) {

    String path = "./_data/_nasa_pics/_0512px/";

    client()
        .apply(access().get())
        .thenCombine(DEFAULT_BUCKET_NAME, bucket())
        .ifFailed(logger)
        .ifPresentOrElse(
            minioClient -> dirStream().apply(path)
                                      .ifFailed(logger)
                                      .thenCombine(minioClient, (stream, c) -> {
                                        for (final Path p : stream) {
                                          imageBlob()
                                              .apply(p)
                                              .ifPresentOrElse(
                                                  blob -> putObj()
                                                      .apply(c, blob)
                                                      .ifFailed(logger)
                                                  ,
                                                  logger
                                              );
                                        }
                                        return Result.success(TRUE);
                                      }),
            logger
        );


  }
}
