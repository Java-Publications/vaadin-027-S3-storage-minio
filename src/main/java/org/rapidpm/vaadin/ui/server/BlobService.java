package org.rapidpm.vaadin.ui.server;

import org.rapidpm.frp.model.Result;

/**
 *
 */
public interface BlobService {
  public Result<byte[]> loadBlob(String blobID);
}
