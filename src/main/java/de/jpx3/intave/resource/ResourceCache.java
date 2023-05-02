package de.jpx3.intave.resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

final class ResourceCache implements Resource {
  private final Resource cache, source;
  private final long expireDuration;

  public ResourceCache(Resource cache, Resource source, long expireDuration) {
    this.cache = cache;
    this.source = source;
    this.expireDuration = expireDuration;
  }

  @Override
  public boolean available() {
    return cache.available();
  }

  @Override
  public long lastModified() {
    return cache.lastModified();
  }

  @Override
  public void write(InputStream inputStream) {
    throw new UnsupportedOperationException();
  }

  @Override
  public InputStream read() {
    if (badCache()) {
      return reloadCache();
    }
    return cache.read();
  }

  private synchronized InputStream reloadCache() {
    if (!badCache()) {
      return cache.read();
    }
    InputStream read = source.read();
    ByteArrayOutputStream inputBytes;
    try {
      if (read == null || read.available() == 0) {
        // try again
        if (read != null) read.close();
        read = source.read();
        if (read == null || read.available() == 0) {
//          System.out.println("Source resource is empty");
//          Thread.dumpStack();
          // cache fallback
          if (read != null) read.close();
          return cache.read();
        }
      }
      inputBytes = new ByteArrayOutputStream();
      byte[] buf = new byte[4096];
      int i;
      while ((i = read.read(buf)) != -1) {
        inputBytes.write(buf, 0, i);
      }
      read.close();
    } catch (Exception exception) {
      return cache.read();
    }
    byte[] bytes = inputBytes.toByteArray();
    if (bytes.length == 0) {
      return read;
    }
    cache.write(inputBytes.toByteArray());
    return new ByteArrayInputStream(bytes);
  }

  private boolean badCache() {
    boolean cacheExists = cache.available();
    boolean cacheOutdated = System.currentTimeMillis() - cache.lastModified() > expireDuration;
    return !cacheExists || cacheOutdated;
  }

  @Override
  public void delete() {
    cache.delete();
  }
}
