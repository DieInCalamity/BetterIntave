package de.jpx3.intave.security.blacklist;

import com.google.common.collect.ImmutableList;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public final class BlackList {
  private final List<String> blacklistedHashes;
  private final MessageDigest sha256Digest;

  private BlackList(List<String> blacklistedHashes) {
    this.blacklistedHashes = blacklistedHashes;
    try {
      this.sha256Digest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("Unable to find SHA-256 hashing digest", exception);
    }
  }

  public boolean nameBlacklisted(String name) {
    return hashBlacklisted(hashOf(name));
  }

  public boolean idBlacklisted(UUID id) {
    return hashBlacklisted(hashOf(id.toString()));
  }

  private boolean hashBlacklisted(String input) {
    return blacklistedHashes.stream().anyMatch(blacklistedHash -> blacklistedHash.equals(input));
  }

  private String hashOf(String input) {
    return bytesToHex(sha256Digest.digest(input.getBytes(StandardCharsets.UTF_8)));
  }

  private static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (byte daBite : hash) {
      String hex = Integer.toHexString(0xff & daBite);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }

  public static BlackList empty() {
    return new BlackList(ImmutableList.of());
  }

  public static BlackList fromInputStream(InputStream inputStream) {
    List<String> blacklistedHash = new ArrayList<>();
    try {
      Scanner scanner = new Scanner(inputStream);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if (line.length() == 64) {
          blacklistedHash.add(line);
        }
      }
      inputStream.close();
    } catch (Exception ignored) {
    }
    return new BlackList(ImmutableList.copyOf(blacklistedHash));
  }
}
