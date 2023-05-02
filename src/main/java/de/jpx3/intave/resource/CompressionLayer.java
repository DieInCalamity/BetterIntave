package de.jpx3.intave.resource;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.*;

public final class CompressionLayer implements Resource {
  private final Resource target;

  private final Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
  private final Inflater inflater = new Inflater();

  public CompressionLayer(Resource target) {
    this.target = target;
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
    target.write(new DeflaterInputStream(inputStream, deflater));
  }

  @Override
  public InputStream read() {
    return new InflaterInputStream(target.read(), inflater);
  }

  @Override
  public OutputStream writeStream() {
    return new DeflaterOutputStream(target.writeStream(), deflater);
  }

  @Override
  public boolean writeStreamSupported() {
    return target.writeStreamSupported();
  }

  @Override
  public void delete() {
    target.delete();
  }
}
