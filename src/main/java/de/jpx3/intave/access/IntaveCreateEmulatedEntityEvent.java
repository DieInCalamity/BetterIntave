package de.jpx3.intave.access;

import de.jpx3.intave.IntavePlugin;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;

public class IntaveCreateEmulatedEntityEvent extends AbstractIntaveExternalEvent {
  protected WeakReference<Player> observer;
  protected int reservedEntityId;

  protected IntaveCreateEmulatedEntityEvent(Player observer, int reservedEntityId) {
    this.observer = new WeakReference<>(observer);
    this.reservedEntityId = reservedEntityId;
  }

  public final Player observer() {
    return observer.get();
  }

  public final int reservedEntityId() {
    return reservedEntityId;
  }

  public void __INTERNAL__renew(Player observer, int reservedEntityId) {
    this.observer = new WeakReference<>(observer);
    this.reservedEntityId = reservedEntityId;
  }

  @Override
  public void refClear() {
    this.observer = null;
  }

  public static IntaveCreateEmulatedEntityEvent empty(IntavePlugin handle) {
    return construct(handle, null,0);
  }

  public static IntaveCreateEmulatedEntityEvent construct(IntavePlugin handle, Player observer, int reservedEntityId) {
    if(handle != IntavePlugin.singletonInstance()) {
      return null;
    }
    return new IntaveCreateEmulatedEntityEvent(observer, reservedEntityId);
  }
}
