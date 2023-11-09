package de.jpx3.intave.module.nayoro;

import org.jetbrains.annotations.NotNull;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class ManualBufferedOutputStream extends FilterOutputStream {
  private byte[] buffer;
  private int flushThreshold;
  private int position;

  public ManualBufferedOutputStream(OutputStream out, int bufferSize) {
    super(out);
    this.buffer = new byte[8192];
    this.flushThreshold = bufferSize;
  }

  @Override
  public synchronized void write(int b) {
    // increase buffer size if needed
    if (position >= buffer.length) {
      byte[] newBuffer = new byte[buffer.length * 2];
      System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
      buffer = newBuffer;
    }
    buffer[position++] = (byte) b;
  }

  @Override
  public synchronized void write(@NotNull byte[] b) throws IOException {
    write(b, 0, b.length);
  }

  @Override
  public synchronized void write(@NotNull byte[] b, int off, int len) throws IOException {
    int remaining = buffer.length - position;
    while (remaining < len) {
      byte[] newBuffer = new byte[buffer.length * 2];
      System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
      buffer = newBuffer;
      remaining = buffer.length - position;
    }
    System.arraycopy(b, off, buffer, position, len);
    position += len;
  }

  @Override
  public synchronized void flush() {
    try {
      if (position < flushThreshold) {
        return;
      }
      out.write(buffer, 0, position);
      position = 0;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void close() throws IOException {
    out.write(buffer, 0, position);
    position = 0;
    super.close();
  }
}
