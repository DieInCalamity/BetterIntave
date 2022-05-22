package de.jpx3.intave.version;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class ProtocolVersionRanges implements Iterable<ProtocolVersionRange> {
  private final Collection<ProtocolVersionRange> versionRanges;

  public ProtocolVersionRanges(List<ProtocolVersionRange> versionRanges) {
    this.versionRanges = versionRanges;
  }

  public Stream<ProtocolVersionRange> stream() {
    return versionRanges.stream();
  }

  public ProtocolVersionRange newest() {
    return versionRanges.stream()
      .max(ProtocolVersionRange::compareTo)
      .orElseThrow(() -> new IllegalStateException("No max version range found"));
  }

  public String byProtocolVersion(int version) {
    ProtocolVersionRange protocolVersionRange =
      versionRanges.stream()
        .filter(range -> range.includes(version))
        .findFirst()
        .orElseGet(this::newest);
    return protocolVersionRange.version();
  }

  @NotNull
  @Override
  public Iterator<ProtocolVersionRange> iterator() {
    return versionRanges.iterator();
  }

  @Override
  public void forEach(Consumer<? super ProtocolVersionRange> action) {
    versionRanges.forEach(action);
  }

  @Override
  public Spliterator<ProtocolVersionRange> spliterator() {
    return versionRanges.spliterator();
  }

  public int byVersion(String version) {
    return -1;
  }
}
