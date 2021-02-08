package de.jpx3.intave.encrypt;

import com.google.common.collect.Lists;
import de.jpx3.intave.detect.checks.combat.heuristics.Anomaly;
import de.jpx3.intave.detect.checks.combat.heuristics.Confidence;
import joptsimple.internal.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public final class HeuristicsPatternEncryption {
  @Test
  public void testPatternEncryption() {
    List<Anomaly> anomalies = Lists.newArrayList(
      Anomaly.anomalyOf("81", Confidence.LIKELY, Anomaly.Type.KILLAURA, "description", 0),
      Anomaly.anomalyOf("83", Confidence.LIKELY, Anomaly.Type.KILLAURA, "description", 0),
      Anomaly.anomalyOf("83", Confidence.LIKELY, Anomaly.Type.KILLAURA, "description", 0),
      Anomaly.anomalyOf("11", Confidence.LIKELY, Anomaly.Type.KILLAURA, "description", 0),
      Anomaly.anomalyOf("12", Confidence.LIKELY, Anomaly.Type.KILLAURA, "description", 0),
      Anomaly.anomalyOf("13", Confidence.LIKELY, Anomaly.Type.KILLAURA, "description", 0),
      Anomaly.anomalyOf("14", Confidence.LIKELY, Anomaly.Type.KILLAURA, "description", 0)
    );
    String encryptAnomalies = encryptAnomalies(anomalies);
    System.out.println("encrypted:" + encryptAnomalies);

    String decryptPatterns = decryptPatterns(encryptAnomalies);
    System.out.println("decrypted:" + decryptPatterns);

    String expectedResult = anomalies.stream().map(Anomaly::key).distinct().map(x -> "p[" + x + "]").collect(Collectors.joining(" "));
    Assertions.assertEquals(expectedResult, decryptPatterns);
  }

  @Test
  public void decrypt() {
    String encrypted = "sgwQuA";
    System.out.println(decryptPatterns(encrypted));
  }

  private String encryptAnomalies(List<Anomaly> anomalies) {
    List<String> usableAnomalies = anomalies.stream()
      .map(Anomaly::key)
      .distinct()
      .collect(Collectors.toList());
    return usableAnomalies
      .stream()
      .map((anomaly) -> encryptPattern(anomaly, usableAnomalies.size()))
      .collect(Collectors.joining());
  }

  private String encryptPattern(String pattern, int size) {

    // 3 bits sub-check   -> 7    = 0 0 0 0 0 1 1 1
    // 5 bits check       -> 31   = 1 1 1 1 1 0 0 0
    // >= 33 && <= 126

    int subCheck = Integer.parseInt(pattern.substring(pattern.length() - 1));
    int mainCheck = Integer.parseInt(pattern.substring(0, pattern.length() - 1));
    if (mainCheck > 31) {
      throw new IllegalArgumentException("Invalid pattern key: main-check cannot be greater than 31");
    }
    if (subCheck > 7) {
      throw new IllegalArgumentException("Invalid pattern key: sub-check cannot be greater than 7");
    }

    int checkCombined = mainCheck << 3 | subCheck;
    checkCombined ^= 452938422 ^ 987509231 ^ size;
    for (int i = 0; i < size * 2; i++) {
      checkCombined ^= size * 28037423 * i;
      checkCombined ^= 928344123 * size;
      checkCombined ^= i * 4203874;
    }
    byte[] encode = Base64.getEncoder().encode(new byte[]{(byte) checkCombined});
    String result = new String(encode).replace("=", "");
    return result.length() > 10 ? result.substring(0, 10) : result;
  }

  private String decryptPatterns(String patterns) {
    int size = patterns.length() / 2;
    List<String> decryptedPatterns = Lists.newArrayList();
    while (patterns.length() > 0) {
      if (patterns.length() % 2 == 0) {
        String pattern = patterns.substring(0, 2);
        String decrypted = decryptPattern(pattern, size);
        decryptedPatterns.add("p[" + decrypted + "]");
      }
      patterns = patterns.substring(1);
    }
    return Strings.join(decryptedPatterns, " ");
  }

  private String decryptPattern(String pattern, int size) {
    byte[] encode = Base64.getDecoder().decode(pattern);
    int checkCombined = encode[0];
    for (int i = 0; i < size * 2; i++) {
      checkCombined ^= size * 28037423 * i;
      checkCombined ^= 928344123 * size;
      checkCombined ^= i * 4203874;
    }
    checkCombined ^= 452938422 ^ 987509231 ^ size;
    int subCheck = checkCombined & 0b111;
    int mainCheck = (checkCombined & 0b11111000) >> 3;
    return mainCheck + String.valueOf(subCheck);
  }
}