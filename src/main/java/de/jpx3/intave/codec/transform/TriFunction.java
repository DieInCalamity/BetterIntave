package de.jpx3.intave.codec.transform;

public interface TriFunction<A, B, C, R> {
    R apply(A a, B b, C c);
}
