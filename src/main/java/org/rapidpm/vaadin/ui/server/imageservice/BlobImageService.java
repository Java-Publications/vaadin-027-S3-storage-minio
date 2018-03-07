package org.rapidpm.vaadin.ui.server.imageservice;

import io.minio.MinioClient;
import org.rapidpm.frp.model.Result;
import org.rapidpm.vaadin.ui.server.BlobService;

/**
 *
 */
public class BlobImageService implements BlobService {



  //connection to the source

  public void setMinioClient(MinioClient minioClient){

  }


  @Override
  public Result<byte[]> loadBlob(String blobID) {







    return Result.ofNullable(null);
  }
}
