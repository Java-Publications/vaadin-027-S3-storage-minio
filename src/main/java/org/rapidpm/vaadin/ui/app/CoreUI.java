package org.rapidpm.vaadin.ui.app;

import com.vaadin.ui.Component;
import org.rapidpm.vaadin.server.CoreUIService;

import static java.lang.System.setProperty;
import static org.rapidpm.vaadin.server.CoreUIService.MyUI.COMPONENT_SUPPLIER_TO_USE;

/**
 *
 */
public class CoreUI extends CoreUIService {

  static {
    setProperty(COMPONENT_SUPPLIER_TO_USE, MyComponentSupplier.class.getName());
  }

  public static class MyComponentSupplier implements ComponentSupplier {

    @Override
    public Component get() {
      return null;
    }
  }
}
