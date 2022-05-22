package de.jpx3.intave.player.fake;

import org.bukkit.ChatColor;

import java.util.concurrent.ThreadLocalRandom;

public final class RandomStringGenerator {
  private static final char ALTERNATE_COLOR = '&';
  private static final int MIN_NAME_LENGTH = 4;
  private static final int MAX_NAME_LENGTH = 8;
  private static final char[] characters = "abcdefghijklmnopqrstuvwxyz0123456789_".toCharArray();

  public static String randomString() {
    int nameLength = ThreadLocalRandom.current().nextInt(MIN_NAME_LENGTH, MAX_NAME_LENGTH);
    StringBuilder name = new StringBuilder();
    for (int i = 0; i <= nameLength; i++) {
      name.append(randomCharacterInSet());
    }
    return name.toString();
  }

  public static String translateColor(String input) {
    return ChatColor.translateAlternateColorCodes(ALTERNATE_COLOR, input);
  }

  private static char randomCharacterInSet() {
    int length = characters.length;
    int i = ThreadLocalRandom.current().nextInt(0, length - 1);
    boolean uppercase = ThreadLocalRandom.current().nextInt(0, 5) % 2 == 0;
    return uppercase ? Character.toUpperCase(characters[i]) : characters[i];
  }
}