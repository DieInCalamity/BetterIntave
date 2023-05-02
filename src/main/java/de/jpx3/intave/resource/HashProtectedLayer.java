package de.jpx3.intave.resource;

import de.jpx3.intave.annotate.Native;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

final class HashProtectedLayer implements Resource {
  private final String path;
  private final Resource target;
  private final Resource hashResource;

  public HashProtectedLayer(String path, Resource target, Resource hashResource) {
    this.path = path;
    this.target = target;
    this.hashResource = hashResource;
  }

  @Override
  public boolean available() {
    return target.available() && hashResource.available();
  }

  @Override
  public long lastModified() {
    return target.lastModified();
  }

  @Override
  public void write(InputStream inputStream) {
    byte[] bytes = readFully(inputStream);
    byte[] hash = hash(bytes);
    hashResource.write(hash);
    target.write(bytes);
  }

  @Override
  public OutputStream writeStream() {
    if (!writeStreamSupported()) {
      throw new UnsupportedOperationException("Write stream is not supported for this resource");
    }
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException exception) {
      throw new RuntimeException(exception);
    }
    return new DigestOutputStream(target.writeStream(), md) {
      @Override
      @Native
      public void close() throws IOException {
        super.close();
        MessageDigest md = getMessageDigest();
        md.update("good password".getBytes(StandardCharsets.UTF_8));
        md.update("bad password".getBytes(StandardCharsets.UTF_8));
        byte[] hash = md.digest();
        hashResource.write(hash);
      }
    };
  }

  @Override
  public boolean writeStreamSupported() {
    return target.writeStreamSupported();
  }

  @Override
  public InputStream read() {
    if (!hashResource.available()) {
      return null;
    }
    InputStream read = target.read();
    byte[] bytes = readFully(read);
    byte[] hash = hash(bytes);
    // read hash from file
    byte[] hashFromFile = readFully(hashResource.read());
    // compare hashes
    if (!Arrays.equals(hash, hashFromFile)) {
      throw new RuntimeException("Hash mismatch, have you changed/moved the file?");
    }
    return new ByteArrayInputStream(bytes);
  }

  private byte[] readFully(InputStream inputStream) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int read;
    try {
      while ((read = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, read);
      }
      inputStream.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return outputStream.toByteArray();
  }

  @Native
  private byte[] hash(byte[] bytes) {
    // hash with SHA-256
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException exception) {
      throw new RuntimeException(exception);
    }
    md.update(bytes);
    md.update("good password".getBytes(StandardCharsets.UTF_8));
    md.update("bad password".getBytes(StandardCharsets.UTF_8));
    return md.digest();
  }

  @Override
  public void delete() {
    target.delete();
    hashResource.delete();
  }
}
