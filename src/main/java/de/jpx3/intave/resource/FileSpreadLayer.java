package de.jpx3.intave.resource;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

final class FileSpreadLayer implements Resource {
  private final File targetFile;
  private final Resource targetResource;
  private final Function<File, Resource> fileToResource;
  private final Resource[] spread;

  public FileSpreadLayer(File targetFile, Function<File, Resource> fileToResource, int spreadAmount) {
    this.targetFile = targetFile;
    this.targetResource = fileToResource.apply(targetFile);
    this.fileToResource = fileToResource;
    this.spread = new Resource[spreadAmount];
    if (targetResource.available()) {
      setupSpreadFiles();
    }
  }

  private boolean prepared = false;

  public synchronized void setupSpreadFiles() {
    if (prepared) {
      return;
    }
    prepared = true;
    for (int i = 0; i < spread.length; i++) {
      File copy = new File(targetFile + letterFrom(i));
      spread[i] = fileToResource.apply(copy);
      boolean exists = copy.exists();
      boolean empty = copy.length() == 0;
      if (!exists || empty) {
        spread[i].write(targetResource.read());
      }
    }
  }

  private String letterFrom(int i) {
    return String.valueOf((char) (i + 'a'));
  }

  @Override
  public boolean available() {
    if (!targetResource.available()) return false;
    return Arrays.stream(spread).anyMatch(resource -> resource != null && resource.available());
  }

  @Override
  public long lastModified() {
    return targetResource.lastModified();
  }

  @Override
  public void write(InputStream inputStream) {
    targetResource.write(inputStream);
    setupSpreadFiles();
    copyMainToSpread();
  }

  private void copyMainToSpread() {
    for (Resource resource : spread) {
      resource.write(targetResource.read());
    }
  }

  @Override
  public InputStream read() {
    setupSpreadFiles();
    int attemptsRemaining = 1000;
    while (available() && attemptsRemaining-- > 0) {
      int random = ThreadLocalRandom.current().nextInt(0, spread.length);
      Resource spreadResource = spread[random];
      if (spreadResource.available()) {
        return spreadResource.read();
      }
      if (attemptsRemaining < 100) {
        try {
          Thread.sleep(10);
        } catch (InterruptedException ignored) {}
      }
    }
    return targetResource.read();
  }

  @Override
  public void delete() {
    targetResource.delete();
    eraseSpread();
  }

  private void eraseSpread() {
    for (Resource resource : spread) {
      if (resource != null) {
        resource.delete();
      }
    }
  }
}
