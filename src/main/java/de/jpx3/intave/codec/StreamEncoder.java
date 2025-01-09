package de.jpx3.intave.codec;

public interface StreamEncoder<O, T> {
  void encode(O o, T t);
}
