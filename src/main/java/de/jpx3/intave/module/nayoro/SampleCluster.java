package de.jpx3.intave.module.nayoro;

import de.jpx3.intave.access.IntaveBootFailureException;
import de.jpx3.intave.resource.Resource;
import de.jpx3.intave.resource.Resources;
import de.jpx3.intave.security.ContextSecrets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Locale;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static de.jpx3.intave.IntaveControl.GOMME_MODE;

public final class SampleCluster {
  private Resource zipResource;

  private SampleCluster(Resource zipResource) {

  }

  public static SampleCluster fromSamples(Sample... samples) {
    File dataFolder = dataFolder();
    File sampleClusterFile;
    do {
      sampleClusterFile = new File(dataFolder, randomId() + ".cluster");
    } while (sampleClusterFile.exists());
    try (
      OutputStream outputStream = Files.newOutputStream(sampleClusterFile.toPath());
      ZipOutputStream out = new ZipOutputStream(outputStream)
    ) {
      for (Sample sample : samples) {
        Resource resource = sample.resource();
        String id = sample.id();
        out.putNextEntry(new ZipEntry(id + ".sample"));
        out.setLevel(Deflater.BEST_COMPRESSION);
        InputStream in = resource.read();
        int count;
        byte[] buffer = new byte[8192];
        while ((count = in.read(buffer)) != -1) {
          out.write(buffer, 0, count);
        }
        in.close();
        out.closeEntry();
        resource.delete();
      }
    } catch (IOException exception) {
      throw new IntaveBootFailureException(exception);
    }
    return new SampleCluster(Resources.resourceFromFile(sampleClusterFile));
  }

  private static String randomId() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  private static File dataFolder() {
    String operatingSystem = System.getProperty("os.name").toLowerCase(Locale.ROOT);
    File workDirectory;
    String filePath;
    if (operatingSystem.contains("win")) {
      filePath = System.getenv("APPDATA") + "/Intave/Samples/";
    } else {
      if (GOMME_MODE) {
        filePath = ContextSecrets.secret("cache-directory") + "samples/";
      } else {
        filePath = System.getProperty("user.home") + "/.intave/samples/";
      }
    }
    workDirectory = new File(filePath);
    if (!workDirectory.exists()) {
      workDirectory.mkdir();
    }
    return workDirectory;
  }
}
