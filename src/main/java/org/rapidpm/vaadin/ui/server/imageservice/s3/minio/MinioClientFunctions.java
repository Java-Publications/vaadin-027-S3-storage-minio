package org.rapidpm.vaadin.ui.server.imageservice.s3.minio;

import io.minio.MinioClient;
import org.rapidpm.frp.functions.CheckedBiFunction;
import org.rapidpm.frp.functions.CheckedFunction;
import org.rapidpm.frp.model.Pair;
import org.rapidpm.frp.model.Quad;
import org.rapidpm.frp.model.Result;
import org.rapidpm.vaadin.ui.server.imageservice.s3.Coordinates;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.function.BiFunction;

/**
 *
 */
public interface MinioClientFunctions {

  static CheckedFunction<Coordinates, MinioClient> client() {
    return (coord) -> new MinioClient(coord.endpoint(),
                                      coord.accessKey(),
                                      coord.secretKey()
    );
  }


  static CheckedBiFunction<MinioClient, String, MinioClient> bucket() {
    return (minioClient, bucketName) -> {
      if (!minioClient.bucketExists(bucketName)) {
        minioClient.makeBucket(bucketName);
      }
      return minioClient;
    };
  }


  static CheckedBiFunction<MinioClient, Blob, MinioClient> putObj() {
    return (minioClient, obj) -> {
      minioClient.putObject(obj.getT1(),
                            obj.getT2(),
                            obj.getT3(),
                            obj.getT4()
      );
      return minioClient;
    };
  }


  class BlobCoordinates extends Pair<String, String> {

    public BlobCoordinates(String bucketName, String objName) {
      super(bucketName, objName);
    }

    public String bucketName() {
      return getT1();
    }

    public String objectName() {
      return getT2();
    }
  }

  class Blob extends Quad<String, String, InputStream, String> {

    public Blob(String bucketName, String objName, InputStream byteStream, String contentType) {
      super(bucketName, objName, byteStream, contentType);
    }

    public String bucketName() {
      return getT1();
    }

    public String objectName() {
      return getT2();
    }

    public InputStream byteStream() {
      return getT3();
    }

    public String contentType() {
      return getT4();
    }
  }


  static CheckedFunction<InputStream, byte[]> asByteArray() {
    return (inputStream) -> {
      final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      int                         nRead;
      byte[]                      data   = new byte[1024];
      while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, nRead);
      }
      // Java9 inputStream.readAllBytes();
      return buffer.toByteArray();
    };
  }


  static CheckedBiFunction<MinioClient, BlobCoordinates, InputStream> getObj() {
    return (minioClient, obj) -> minioClient
        .getObject(
            obj.bucketName(),
            obj.objectName()
        );
  }

  static BiFunction<MinioClient, BlobCoordinates, Result<InputStream>> imageStream() {
    return (minioClient, blobCoord) -> getObj().apply(minioClient, blobCoord);
  }
}
