package org.rapidpm.vaadin.ui.server.imageservice.s3;

import org.rapidpm.frp.model.Quad;

import java.io.InputStream;

/**
 *
 */
public class Blob extends Quad<String, String, InputStream, String> {

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
