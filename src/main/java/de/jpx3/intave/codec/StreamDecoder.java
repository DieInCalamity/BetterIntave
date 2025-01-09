package de.jpx3.intave.codec;

public interface StreamDecoder<I, T> {
  T decode(I i);
}
