package de.jpx3.intave.security;

import de.jpx3.intave.annotate.Native;
import de.jpx3.intave.resource.legacy.EncryptedLegacyResource;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public final class HWIDVerification {
  private static EncryptedLegacyResource encryptedResource;
  private static String identifier;

  @Native
  public static String publicHardwareIdentifier() {
    if (encryptedResource == null) {
      encryptedResource = new EncryptedLegacyResource("hardware-id", false);
    }
    if (!encryptedResource.exists()) {
      identifier = randomString();
      encryptedResource.write(identifier);
    }
    if (identifier == null) {
//      Scanner scanner = new Scanner(new InputStreamReader(encryptedResource.read()));
//      while (scanner.hasNext()) {
//        identifier = scanner.next();
//      }
//      identifier = identifier.trim();
      identifier = encryptedResource.readAsString().trim();
    }
    return identifier;
  }

  @Native
  private static String randomString() {
    char[] available = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234567890_-$@?".toCharArray();
    StringBuilder str = new StringBuilder();
    for (int i = 0; i < 128; i++) {
      str.append(available[ThreadLocalRandom.current().nextInt(0, available.length - 1)]);
    }
    return str.toString();
  }
}
