package de.jpx3.intave.command;

import java.util.*;

final class LevenshteinPicker {
  public static SearchResult search(Collection<String> haystacks, String needle) {
    Map<String, Integer> levenshteinCandidates = searchWithLimit(haystacks, needle, 4);
    if (!levenshteinCandidates.isEmpty()) {
      for (String candidate : levenshteinCandidates.keySet()) {
        if (needle.equalsIgnoreCase(candidate)) {
          return new SearchResult(Result.CONFIDENT, Collections.singletonList(candidate));
        }
      }
      if (levenshteinCandidates.size() == 1) {
        String haystack = levenshteinCandidates.keySet().iterator().next();
        Integer distance = levenshteinCandidates.values().iterator().next();
        return new SearchResult(distance == 0 ? Result.CONFIDENT : Result.MAYBE, Collections.singletonList(haystack));
      } else {
        Map<String, Integer> sortedLevenshteinCandidates = sortHashMapByValues(levenshteinCandidates);
        int best = sortedLevenshteinCandidates.values().iterator().next();
        List<String> candidatesSameAsBest = new ArrayList<>();
        sortedLevenshteinCandidates.forEach((key, value) -> {
          if (value == best && candidatesSameAsBest.size() < 4) {
            candidatesSameAsBest.add(key);
          }
        });
        Result result = candidatesSameAsBest.size() > 1 ? Result.INCONCLUSIVE : (best < 1 ? Result.CONFIDENT : Result.MAYBE);
        return new SearchResult(result, candidatesSameAsBest);
      }
    } else {
      return SearchResult.empty();
    }
  }

  private static Map<String, Integer> searchWithLimit(Collection<String> selectors, String selection, int limit) {
    Map<String, Integer> result = new HashMap<>();
    for (String rootString : selectors) {
      int distance = levenshteinDistance(rootString.toLowerCase(), selection.substring(0, Math.min(selection.length(), rootString.length())));
      if (distance < limit) {
        result.put(rootString.toLowerCase(), distance);
      }
    }
    return result;
  }

  private static int levenshteinDistance(String rootString, String needleString) {
    return Levenshtein.distance(rootString, needleString, 0, 1, 2, 1);
  }

  private static <K extends Comparable<? super K>, V extends Comparable<? super V>> Map<K, V> sortHashMapByValues(
    Map<K, V> passedMap
  ) {
    List<K> mapKeys = new ArrayList<>(passedMap.keySet());
    List<V> mapValues = new ArrayList<>(passedMap.values());
    Collections.sort(mapValues);
//    Collections.reverse(mapValues);
    Collections.sort(mapKeys);
    Map<K, V> sortedMap = new LinkedHashMap<>();
    for (V val : mapValues) {
      Iterator<K> keyIt = mapKeys.iterator();
      while (keyIt.hasNext()) {
        K key = keyIt.next();
        if (passedMap.get(key).equals(val)) {
          keyIt.remove();
          sortedMap.put(key, val);
          break;
        }
      }
    }
    return sortedMap;
  }

  public static class SearchResult {
    private static final SearchResult EMPTY = new SearchResult(Result.NONE, Collections.emptyList());

    private final Result result;
    private final List<String> matches;

    public SearchResult(Result result, List<String> matches) {
      this.result = result;
      this.matches = matches;
    }

    public Result result() {
      return result;
    }

    public List<String> matches() {
      return matches;
    }

    public static SearchResult empty() {
      return EMPTY;
    }
  }

  public enum Result {
    CONFIDENT,
    INCONCLUSIVE,
    MAYBE,
    NONE
  }
}
