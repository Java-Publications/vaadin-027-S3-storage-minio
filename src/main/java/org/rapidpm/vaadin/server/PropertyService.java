package org.rapidpm.vaadin.server;

/**
 *
 */
public interface PropertyService {

  String resolve(String key);

  boolean hasKey(String key);
}
