package de.jpx3.intave.codec.transform;

public interface QuadFunction<A, B, C, D, R> {
    R apply(A a, B b, C c, D d);
}
