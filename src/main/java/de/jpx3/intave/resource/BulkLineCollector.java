package de.jpx3.intave.resource;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class BulkLineCollector {
  public static <R> Collector<String, ?, R> withFinisher(Function<List<String>, R> finisher) {
    return Collectors.collectingAndThen(Collectors.toList(), finisher);
  }
}
