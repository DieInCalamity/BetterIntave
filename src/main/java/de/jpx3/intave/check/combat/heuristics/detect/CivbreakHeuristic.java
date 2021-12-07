package de.jpx3.intave.check.combat.heuristics.detect;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.jpx3.intave.check.MetaCheckPart;
import de.jpx3.intave.check.combat.Heuristics;
import de.jpx3.intave.module.linker.packet.PacketSubscription;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.CheckCustomMetadata;
import org.bukkit.entity.Player;

import static de.jpx3.intave.module.linker.packet.PacketId.Client.*;

public final class CivbreakHeuristic extends MetaCheckPart<Heuristics, CivbreakHeuristic.CivbreakMeta> {
  
  public CivbreakHeuristic(Heuristics parentCheck) {
    super(parentCheck, CivbreakMeta.class);
  }
  
  /*
  What is civbreak?
  Civbreak abuses a server bug where you can instant break a block on a block position where you
  already destroyed one block. So civbreak only sends multiple STOP_DESTROY_BLOCK packets after the
  player destroyed the block once.
   */
  @PacketSubscription(
    packetsIn = {
      BLOCK_DIG
    }
  )
  public void receiveInteractionPacket(PacketEvent event) {
    Player player = event.getPlayer();
    User user = userOf(player);
    CivbreakMeta meta = metaOf(user);
    PacketContainer packet = event.getPacket();
    
    EnumWrappers.PlayerDigType playerDigType = packet.getPlayerDigTypes().readSafely(0);
    
    if (playerDigType == EnumWrappers.PlayerDigType.START_DESTROY_BLOCK) {
      meta.isMining = true;
    }
    if (playerDigType == EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK) {
      if (!meta.isMining) {
        event.setCancelled(true);
      }
      
      meta.isMining = false;
    }
  }
  
  public final static class CivbreakMeta extends CheckCustomMetadata {
    private boolean isMining;
  }
}