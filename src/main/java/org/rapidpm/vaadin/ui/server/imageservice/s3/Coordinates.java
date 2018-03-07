package org.rapidpm.vaadin.ui.server.imageservice.s3;

import org.rapidpm.frp.model.Triple;

/**
 *
 */
public class Coordinates extends Triple<String, String, String> {
  public Coordinates(String endpoint, String accessKey, String secretKey) {
    super(endpoint, accessKey, secretKey);
  }

  public String endpoint() { return getT1(); }

  public String accessKey() { return getT2(); }

  public String secretKey() { return getT3(); }
}
