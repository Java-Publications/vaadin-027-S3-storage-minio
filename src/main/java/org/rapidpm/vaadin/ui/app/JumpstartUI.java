/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.rapidpm.vaadin.ui.app;

import com.vaadin.ui.Component;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.vaadin.ui.server.CoreUIService;

import static java.lang.System.setProperty;
import static org.rapidpm.vaadin.ui.server.CoreUIService.MyUI.COMPONENT_SUPPLIER_TO_USE;


public class JumpstartUI extends CoreUIService implements HasLogger {

  static {
    setProperty(COMPONENT_SUPPLIER_TO_USE, MySupplier.class.getName());
  }

  public static class MySupplier implements CoreUIService.ComponentSupplier {
    @Override
    public Component get() {
      return new DashboardComponent().postConstruct();
    }
  }
}
