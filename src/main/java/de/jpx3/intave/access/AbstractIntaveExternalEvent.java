package de.jpx3.intave.access;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Jpx3 on 10.11.2017.
 */

public abstract class AbstractIntaveExternalEvent extends Event {
  private static final HandlerList handlers = new HandlerList();

  public AbstractIntaveExternalEvent() {
    super(true);
  }

  public abstract void refClear();

  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
