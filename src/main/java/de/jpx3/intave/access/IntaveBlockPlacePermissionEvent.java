package de.jpx3.intave.access;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public final class IntaveBlockPlacePermissionEvent extends AbstractIntaveExternalEvent implements Cancellable {
  private Player player;
  private World world;
  private boolean mainHand;
  private int blockX, blockY, blockZ;
  private int enumDirection;
  private int typeId;
  private byte data;
  private boolean cancelled;

  protected IntaveBlockPlacePermissionEvent() {
  }

  public void copy(
    Player player,
    World world,
    boolean mainHand,
    int blockX, int blockY, int blockZ,
    int enumDirection,
    int typeId, byte data
  ) {
    this.player = player;
    this.world = world;
    this.mainHand = mainHand;
    this.blockX = blockX;
    this.blockY = blockY;
    this.blockZ = blockZ;
    this.enumDirection = enumDirection;
    this.typeId = typeId;
    this.data = data;
    this.cancelled = false;
  }

  public Player player() {
    return player;
  }

  public World world() {
    return world;
  }

  public boolean isMainHand() {
    return mainHand;
  }

  public int blockX() {
    return blockX;
  }

  public int blockY() {
    return blockY;
  }

  public int blockZ() {
    return blockZ;
  }

  public int enumDirection() {
    return enumDirection;
  }

  public int typeId() {
    return typeId;
  }

  public byte data() {
    return data;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  @Override
  public void refClear() {
    player = null;
  }

  public static IntaveBlockPlacePermissionEvent empty() {
    return new IntaveBlockPlacePermissionEvent();
  }
}
