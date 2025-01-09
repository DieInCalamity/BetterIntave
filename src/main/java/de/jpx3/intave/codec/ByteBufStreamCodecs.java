package de.jpx3.intave.codec;

import io.netty.buffer.ByteBuf;

public final class ByteBufStreamCodecs {
  public static final StreamCodec<ByteBuf, ByteBuf, Boolean> BOOLEAN = StreamCodec.of(ByteBuf::writeBoolean, ByteBuf::readBoolean);
  public static final StreamCodec<ByteBuf, ByteBuf, Integer> INTEGER = StreamCodec.of(ByteBuf::writeInt, ByteBuf::readInt);
  public static final StreamCodec<ByteBuf, ByteBuf, Long> LONG = StreamCodec.of(ByteBuf::writeLong, ByteBuf::readLong);
  public static final StreamCodec<ByteBuf, ByteBuf, Float> FLOAT = StreamCodec.of(ByteBuf::writeFloat, ByteBuf::readFloat);
  public static final StreamCodec<ByteBuf, ByteBuf, Double> DOUBLE = StreamCodec.of(ByteBuf::writeDouble, ByteBuf::readDouble);
}
