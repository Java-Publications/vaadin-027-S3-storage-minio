package org.rapidpm.vaadin.ui.server.imageservice.s3;

import org.rapidpm.frp.model.Pair;

/**
 *
 */
public class BlobCoordinates extends Pair<String, String> {

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
