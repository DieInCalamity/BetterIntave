package de.jpx3.intave.resource.legacy;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Deprecated
public interface LegacyResource {
  InputStream read();

  default String readAsString() {
    return collectLines(Collectors.joining());
  }

  default List<String> readLines() {
    return collectLines(Collectors.toList());
  }

  default <C, R> R collectLines(Collector<? super String, C, R> collector) {
    C container = collector.supplier().get();
    BiConsumer<C, ? super String> accumulator = collector.accumulator();
    Function<C, R> finisher = collector.finisher();
    try (InputStream inputStream = read()) {
      if (inputStream == null) {
        return finisher.apply(container);
      }
      Scanner scanner = new Scanner(inputStream, "UTF-8");
      while (scanner.hasNextLine()) {
        accumulator.accept(container, scanner.nextLine());
      }
    } catch (IOException exception) {
      exception.printStackTrace();
    }
    return finisher.apply(container);
  }
}
