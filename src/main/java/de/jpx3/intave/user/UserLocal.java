package de.jpx3.intave.user;

import de.jpx3.intave.cleanup.GarbageCollector;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public final class UserLocal<T> {
  private final Function<? super User, ? extends T> initializer;
  private final Map<UUID, T> map = GarbageCollector.watch(new ConcurrentHashMap<>());

  private UserLocal(Function<? super User, ? extends T> initializer) {
    this.initializer = initializer;
  }

  public T get(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User must not be null");
    }
    if (!user.hasPlayer()) {
      return initializer.apply(user);
    }
    UUID id = user.player().getUniqueId();
    return map.computeIfAbsent(id, uuid -> initializer.apply(user));
  }

  public static <T> UserLocal<T> withInitial(T value) {
    return new UserLocal<>(u -> value);
  }

  public static <T> UserLocal<T> withInitial(Supplier<? extends T> initializer) {
    return new UserLocal<>(user -> initializer.get());
  }

  public static <T> UserLocal<T> withInitial(Function<? super User, ? extends T> initializer) {
    return new UserLocal<>(initializer);
  }
}
