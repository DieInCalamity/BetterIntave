package de.jpx3.intave.check.world.interaction;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class Interaction {
  private final PacketContainer thePacket;
  private final World world;
  private final Player player;
  private final BlockPosition targetBlock;
  private final int targetDirection;
  private final InteractionType type;
  private final Material itemTypeInHand;
  private final EnumWrappers.Hand hand;
  private final EnumWrappers.PlayerDigType digType;
  private boolean entered = false;

  public Interaction(
    PacketContainer thePacket,
    World world, Player player,
    BlockPosition targetBlock, int targetDirection,
    InteractionType type,
    Material itemTypeInHand, EnumWrappers.Hand hand,
    EnumWrappers.PlayerDigType digType
  ) {
    this.thePacket = thePacket;
    this.world = world;
    this.player = player;
    this.targetBlock = targetBlock;
    this.targetDirection = targetDirection;
    this.type = type;
    this.itemTypeInHand = itemTypeInHand;
    this.hand = hand;
    this.digType = digType;
  }

  public PacketContainer thePacket() {
    return thePacket;
  }

  public InteractionType type() {
    return type;
  }

  public World world() {
    return world;
  }

  public Player player() {
    return player;
  }

  public Material itemTypeInHand() {
    return itemTypeInHand;
  }

  public EnumWrappers.Hand hand() {
    return hand;
  }

  public BlockPosition targetBlock() {
    return targetBlock;
  }

  public int targetDirection() {
    return targetDirection;
  }

  public EnumWrappers.PlayerDigType digType() {
    return digType;
  }

  public void enter() {
    entered = true;
  }

  public boolean entered() {
    return entered;
  }

  @Override
  public String toString() {
    return "Interaction{" +
      "targetBlock=" + targetBlock +
      ", targetDirection=" + targetDirection +
      ", type=" + type +
      ", itemTypeInHand=" + itemTypeInHand +
      ", hand=" + hand +
      ", entered=" + entered +
      '}';
  }
}
