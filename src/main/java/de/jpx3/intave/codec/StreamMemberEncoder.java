package de.jpx3.intave.codec;

public interface StreamMemberEncoder<O, T> {
  void encode(T t, O o);
}
