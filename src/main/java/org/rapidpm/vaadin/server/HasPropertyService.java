package org.rapidpm.vaadin.server;


/**
 *
 */
public interface HasPropertyService {

  default PropertyService properties() {
    return PropertyServiceInMemory.instance();
  }
}
