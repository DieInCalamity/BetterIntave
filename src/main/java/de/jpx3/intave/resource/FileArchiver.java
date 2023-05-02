package de.jpx3.intave.resource;

import com.google.common.base.Preconditions;
import de.jpx3.intave.access.IntaveBootFailureException;
import de.jpx3.intave.access.IntaveInternalException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class FileArchiver {
  public static void archiveAndDeleteFile(File oldFile, File archiveFile) {
    validate(oldFile, archiveFile);
    archiveFile(oldFile, archiveFile);
    tryDeleteFile(oldFile);
  }

  public static void archiveFile(File oldFile, File archiveFile) {
    validate(oldFile, archiveFile);
    tryCreateFile(archiveFile);
    moveFileToArchive(oldFile, archiveFile);
  }

  public static void archiveAndDeleteFiles(File archiveFile, File... oldFiles) {
    for (File oldFile : oldFiles) {
      validate(oldFile, archiveFile);
    }
    archiveFiles(archiveFile, oldFiles);
    for (File oldFile : oldFiles) {
      tryDeleteFile(oldFile);
    }
  }

  public static void archiveFiles(File archiveFile, File... oldFiles) {
    for (File oldFile : oldFiles) {
      validate(oldFile, archiveFile);
    }
    tryCreateFile(archiveFile);
    moveFilesToArchive(archiveFile, oldFiles);
  }

  private static void validate(File oldFile, File archiveFile) {
    Preconditions.checkNotNull(oldFile);
    Preconditions.checkNotNull(archiveFile);
    validateInputFile(oldFile);
    validateArchiveFile(archiveFile);
  }

  private static void validateInputFile(File file) {
    if (file.isDirectory()) {
      throw new IllegalArgumentException("Can't pack directory");
    }
    if (!file.exists()) {
      throw new IllegalArgumentException("Input file does not exist");
    }
    if (!file.canRead()) {
      throw new IllegalArgumentException("Can't read input file");
    }
  }

  private static void validateArchiveFile(File file) {
    if (file.isDirectory()) {
      throw new IllegalArgumentException("Can't have folder as archive");
    }
    if (!file.getName().endsWith(".zip")) {
      throw new IllegalArgumentException("Archive needs a .zip suffix");
    }
    if (file.exists()) {
      throw new IllegalArgumentException("Archive already exists?");
    }
  }

  private static void moveFileToArchive(File file, File archiveFile) {
    try (
      FileInputStream in = new FileInputStream(file);
      ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(archiveFile.toPath()))
    ) {
      out.putNextEntry(new ZipEntry(file.getName()));
      out.setLevel(Deflater.BEST_COMPRESSION);
      int count;
      byte[] buffer = new byte[8192];
      while ((count = in.read(buffer)) != -1) {
        out.write(buffer, 0, count);
      }
    } catch (IOException exception) {
      throw new IntaveBootFailureException(exception);
    }
  }

  private static void moveFilesToArchive(File archiveFile, File... files) {
    try (
      ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(archiveFile.toPath()))
    ) {
      out.setLevel(Deflater.BEST_COMPRESSION);
      for (File file : files) {
        try (
          FileInputStream in = new FileInputStream(file)
        ) {
          out.putNextEntry(new ZipEntry(file.getName()));
          int count;
          byte[] buffer = new byte[8192];
          while ((count = in.read(buffer)) != -1) {
            out.write(buffer, 0, count);
          }
        }
      }
    } catch (IOException exception) {
      throw new IntaveBootFailureException(exception);
    }
  }

  private static void tryCreateFile(File file) {
    try {
      file.createNewFile();
    } catch (IOException e) {
      throw new IntaveInternalException(e);
    }
  }

  private static void tryDeleteFile(File file) {
    file.delete();
  }
}
