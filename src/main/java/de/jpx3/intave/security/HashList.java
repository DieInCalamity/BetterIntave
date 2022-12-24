package de.jpx3.intave.security;

import com.google.common.collect.ImmutableList;
import de.jpx3.intave.resource.Resource;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

public final class HashList {
  private final List<String> hashes;
  private final MessageDigest sha256Digest;

  private HashList(List<String> hashes) {
    this.hashes = hashes;
    try {
      this.sha256Digest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("Unable to find SHA-256 hashing digest", exception);
    }
  }

  public boolean containsName(String name) {
    return hashBlacklisted(hashOf(name));
  }

  public boolean containsId(UUID id) {
    return hashBlacklisted(hashOf(id.toString()));
  }

  private boolean hashBlacklisted(String input) {
    for (String blacklistedHash : hashes) {
      if (blacklistedHash.equals(input)) {
        return true;
      }
    }
    return false;
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

  public static HashList empty() {
    return new HashList(ImmutableList.of());
  }

  public static HashList from(Resource resource) {
    return new HashList(resource.readLines());
  }
}
