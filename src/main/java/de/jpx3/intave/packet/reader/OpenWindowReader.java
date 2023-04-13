package de.jpx3.intave.packet.reader;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class OpenWindowReader extends AbstractPacketReader implements EntityIterable {
  public Optional<Integer> optionalEntityId() {
    Integer read = packet().getIntegers().read(2);
    if (read == null) {
      return Optional.empty();
    } else if (read == 0) {
      return Optional.empty();
    }
    return Optional.of(read);
  }

  @Override
  public @NotNull SubstitutionIterator<Integer> iterator() {
    Optional<Integer> integer = optionalEntityId();
    if (!integer.isPresent()) {
      return new SubstitutionIterator<Integer>() {
        @Override
        public void set(Integer integer) {
        }

        @Override
        public boolean hasNext() {
          return false;
        }

        @Override
        public Integer next() {
          throw new UnsupportedOperationException();
        }
      };
    } else {
      return new SubstitutionIterator<Integer>() {
        boolean hasNext = false;

        @Override
        public void set(Integer integer) {
          packet().getIntegers().write(2, integer);
        }

        @Override
        public boolean hasNext() {
          return hasNext;
        }

        @Override
        public Integer next() {
          hasNext = false;
          return integer.get();
        }
      };
    }
  }
}
