package de.jpx3.intave.event.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

public final class ForwardingPacketAdapter extends IntavePacketAdapter {
  private final static boolean TEMP_PLAYER_CHECK;
  static {
    TEMP_PLAYER_CHECK = Arrays.stream(PacketEvent.class.getMethods())
      .anyMatch(method -> method.getName().equalsIgnoreCase("isPlayerTemporary"));
  }

  private final Collection<LocalPacketAdapter> targetList;

  public ForwardingPacketAdapter(
    IntavePlugin plugin,
    PacketType packetType,
    Collection<LocalPacketAdapter> targetList
  ) {
    super(plugin, ListenerPriority.LOWEST, packetType);
    this.targetList = targetList;
  }

  @Override
  public void onPacketSending(PacketEvent event) {
    if (TEMP_PLAYER_CHECK) {
      // perform temporary check
      if (event.isPlayerTemporary()) {
        return;
      }
    }
    Player player = event.getPlayer();
    if (player == null) {
      return;
    }
    User user = UserRepository.userOf(player);
    if (user == null) {
      return;
    }
    if (user.shouldIgnoreNextOutboundPacket()) {
      user.receiveNextOutboundPacket();
      return;
    }
    for (LocalPacketAdapter localPacketAdapter : targetList) {
      localPacketAdapter.onPacketSending(event);
    }
  }

  @Override
  public void onPacketReceiving(PacketEvent event) {
    if (TEMP_PLAYER_CHECK) {
      // perform temporary check
      if (event.isPlayerTemporary()) {
//          Timings.packetProcessing.stop();
        return;
      }
    }
    User user = UserRepository.userOf(event.getPlayer());
    if (user == null) {
      return;
    }
    if (user.shouldIgnoreNextPacket()) {
      user.receiveNextPacket();
      return;
    }
    for (LocalPacketAdapter localPacketAdapter : targetList) {
      localPacketAdapter.onPacketReceiving(event);
    }
  }
}
