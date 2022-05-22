package de.jpx3.intave.module.patcher;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public final class SynchronizedSet<E> implements Set<E> {
  private final Set<E> parent;

  public SynchronizedSet(Set<E> parent) {
    this.parent = parent;
  }

  public synchronized int size() {
    return parent.size();
  }

  public synchronized boolean isEmpty() {
    return parent.isEmpty();
  }

  public synchronized boolean contains(Object o) {
    return parent.contains(o);
  }

  public synchronized Iterator<E> iterator() {
    return parent.iterator();
  }

  public synchronized Object[] toArray() {
    return parent.toArray();
  }

  public synchronized <T> T[] toArray(@NotNull T @NotNull [] a) {
    return parent.toArray(a);
  }

  public synchronized boolean add(E e) {
    return parent.add(e);
  }

  public synchronized boolean remove(Object o) {
    return parent.remove(o);
  }

  public synchronized boolean containsAll(@NotNull Collection<?> c) {
    return parent.containsAll(c);
  }

  public synchronized boolean addAll(@NotNull Collection<? extends E> c) {
    return parent.addAll(c);
  }

  public synchronized boolean retainAll(@NotNull Collection<?> c) {
    return parent.retainAll(c);
  }

  public synchronized boolean removeAll(@NotNull Collection<?> c) {
    return parent.removeAll(c);
  }

  public synchronized void clear() {
    parent.clear();
  }
}
