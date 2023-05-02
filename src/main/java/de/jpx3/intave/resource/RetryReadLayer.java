package de.jpx3.intave.resource;

import java.io.IOException;
import java.io.InputStream;

final class RetryReadLayer implements Resource {
  private final Resource target;
  private final int attempts;

  public RetryReadLayer(Resource target, int attempts) {
    this.target = target;
    this.attempts = attempts;
  }

  @Override
  public boolean available() {
    return target.available();
  }

  @Override
  public long lastModified() {
    return target.lastModified();
  }

  @Override
  public void write(InputStream inputStream) {
    target.write(inputStream);
  }

  @Override
  public InputStream read() {
    InputStream read = target.read();
    int remAttempts = attempts;
    try {
      while ((read == null || read.available() == 0) && remAttempts-- > 0) {
        try {
          if (read != null) read.close();
        } catch (Exception ignored) {}
        read = target.read();
      }
    } catch (IOException exception) {
      exception.printStackTrace();
    }
    return read;
  }

  @Override
  public void delete() {
    target.delete();
  }
}
