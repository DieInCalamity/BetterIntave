package de.jpx3.intave.version;

import de.jpx3.intave.resource.BulkLineCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

final class ProtocolVersionRangesCompiler {
  public static ProtocolVersionRanges apply(List<String> lines) {
    int lastEnd = Integer.MIN_VALUE;
    List<ProtocolVersionRange> ranges = new ArrayList<>();
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i).trim();
      if (line.startsWith("#")) {
        continue;
      }
      if (line.startsWith("up to")) {
        // format is "up to <number> is <version>"
        String[] split = line.split(" ");
        if (split.length != 5) {
          System.out.println("Invalid line format: " + line + " at line " + i);
          Thread.dumpStack();
          continue;
        }
        int protocolVersion = Integer.parseInt(split[2]);
        String version = split[4];
        ranges.add(new ProtocolVersionRange(lastEnd + 1, protocolVersion, version));
        lastEnd = protocolVersion;
      } else {
        // format is "<number> is <version>"
        String[] split = line.split(" is ");
        if (split.length != 2) {
          System.out.println("Invalid line format: " + line + " at line " + i);
          Thread.dumpStack();
          continue;
        }
        int protocolVersion = Integer.parseInt(split[0]);
        String version = split[1];
        if (protocolVersion <= lastEnd) {
          System.out.println("Invalid line format: " + line + " at line " + i);
          Thread.dumpStack();
          continue;
        }
        ranges.add(new ProtocolVersionRange(protocolVersion, protocolVersion, version));
        lastEnd = protocolVersion;
      }
    }
    return new ProtocolVersionRanges(ranges);
  }

  private static final Collector<String, ?, ProtocolVersionRanges> RESOURCE_COLLECTOR = BulkLineCollector.withFinisher(ProtocolVersionRangesCompiler::apply);

  public static Collector<String, ?, ProtocolVersionRanges> resourceCollector() {
    return RESOURCE_COLLECTOR;
  }
}
