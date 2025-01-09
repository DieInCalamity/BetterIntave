package de.jpx3.intave.codec;

import de.jpx3.intave.codec.transform.QuadFunction;
import de.jpx3.intave.codec.transform.TriFunction;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface StreamCodec<I, O, T> extends StreamEncoder<O, T>, StreamDecoder<I, T> {
  static <I, O, T> StreamCodec<I, O, T> of(StreamEncoder<O, T> encoder, StreamDecoder<I, T> decoder) {
    return new StreamCodec<I, O, T>() {
      @Override
      public void encode(O o, T t) {
        encoder.encode(o, t);
      }

      @Override
      public T decode(I i) {
        return decoder.decode(i);
      }
    };
  }

  static <I, O, T> StreamCodec<I, O, T> ofMember(StreamMemberEncoder<O, T> encoder, StreamDecoder<I, T> decoder) {
    return new StreamCodec<I, O, T>() {
      @Override
      public void encode(O o, T t) {
        encoder.encode(t, o);
      }

      @Override
      public T decode(I i) {
        return decoder.decode(i);
      }
    };
  }

  static <B, C, T1> StreamCodec<B, B, C> forRecord(
    StreamCodec<B, B, T1> valueOneConverter,
    Function<C, T1> valueOneGetter,
    Function<T1, C> constructor
  ) {
    return new StreamCodec<B, B, C>() {
      @Override
      public void encode(B b, C c) {
        valueOneConverter.encode(b, valueOneGetter.apply(c));
      }

      @Override
      public C decode(B b) {
        return constructor.apply(valueOneConverter.decode(b));
      }
    };
  }

  static <B, C, T1, T2> StreamCodec<B, B, C> forRecord(
    StreamCodec<B, B, T1> valueOneConverter,
    Function<C, T1> valueOneGetter,
    StreamCodec<B, B, T2> valueTwoConverter,
    Function<C, T2> valueTwoGetter,
    BiFunction<T1, T2, C> constructor
  ) {
    return new StreamCodec<B, B, C>() {
      @Override
      public void encode(B b, C c) {
        valueOneConverter.encode(b, valueOneGetter.apply(c));
        valueTwoConverter.encode(b, valueTwoGetter.apply(c));
      }

      @Override
      public C decode(B b) {
        T1 t1 = valueOneConverter.decode(b);
        T2 t2 = valueTwoConverter.decode(b);
        return constructor.apply(t1, t2);
      }
    };
  }

  static <B, C, T1, T2, T3> StreamCodec<B, B, C> forRecord(
    StreamCodec<B, B, T1> valueOneConverter,
    Function<C, T1> valueOneGetter,
    StreamCodec<B, B, T2> valueTwoConverter,
    Function<C, T2> valueTwoGetter,
    StreamCodec<B, B, T3> valueThreeConverter,
    Function<C, T3> valueThreeGetter,
    TriFunction<T1, T2, T3, C> constructor
  ) {
    return new StreamCodec<B, B, C>() {
      @Override
      public void encode(B b, C c) {
        valueOneConverter.encode(b, valueOneGetter.apply(c));
        valueTwoConverter.encode(b, valueTwoGetter.apply(c));
        valueThreeConverter.encode(b, valueThreeGetter.apply(c));
      }

      @Override
      public C decode(B b) {
        T1 t1 = valueOneConverter.decode(b);
        T2 t2 = valueTwoConverter.decode(b);
        T3 t3 = valueThreeConverter.decode(b);
        return constructor.apply(t1, t2, t3);
      }
    };
  }

  static <B, C, T1, T2, T3, T4> StreamCodec<B, B, C> forRecord(
    StreamCodec<B, B, T1> valueOneConverter,
    Function<C, T1> valueOneGetter,
    StreamCodec<B, B, T2> valueTwoConverter,
    Function<C, T2> valueTwoGetter,
    StreamCodec<B, B, T3> valueThreeConverter,
    Function<C, T3> valueThreeGetter,
    StreamCodec<B, B, T4> valueFourConverter,
    Function<C, T4> valueFourGetter,
    QuadFunction<T1, T2, T3, T4, C> constructor
  ) {
    return new StreamCodec<B, B, C>() {
      @Override
      public void encode(B b, C c) {
        valueOneConverter.encode(b, valueOneGetter.apply(c));
        valueTwoConverter.encode(b, valueTwoGetter.apply(c));
        valueThreeConverter.encode(b, valueThreeGetter.apply(c));
        valueFourConverter.encode(b, valueFourGetter.apply(c));
      }

      @Override
      public C decode(B b) {
        T1 t1 = valueOneConverter.decode(b);
        T2 t2 = valueTwoConverter.decode(b);
        T3 t3 = valueThreeConverter.decode(b);
        T4 t4 = valueFourConverter.decode(b);
        return constructor.apply(t1, t2, t3, t4);
      }
    };
  }
}
