package de.jpx3.intave.packet.reader;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.jpx3.intave.adapter.MinecraftVersions;

public final class EntityUseReader extends EntityReader {

  public boolean isAttackPacket() {
    return useAction() == EnumWrappers.EntityUseAction.ATTACK;
  }

  public boolean isSecondary() {
    return useAction() == EnumWrappers.EntityUseAction.INTERACT_AT;
  }

  public EnumWrappers.EntityUseAction useAction() {
    if (MinecraftVersions.VER26_1_1.atOrAbove()) {
      PacketType type = packet().getType();
      if (type == PacketType.Play.Client.ATTACK) {
        return EnumWrappers.EntityUseAction.ATTACK;
      }
      boolean isSecondary = packet().getBooleans().read(0);
      return isSecondary ? EnumWrappers.EntityUseAction.INTERACT_AT : EnumWrappers.EntityUseAction.INTERACT;
    } else {
      EnumWrappers.EntityUseAction action = packet().getEntityUseActions().readSafely(0);
      if (action == null) {
        action = packet().getEnumEntityUseActions().read(0).getAction();
      }
      return action;
    }
  }
}
