package de.jpx3.intave.event.dispatch;

import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.event.packet.ListenerPriority;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.entity.Player;

public final class AsymmetricConnectionHalter {
  private final IntavePlugin plugin;

  public AsymmetricConnectionHalter(IntavePlugin plugin) {
    this.plugin = plugin;
  }

  @PacketSubscription(
    priority = ListenerPriority.LOWEST,
    packetsIn = {}
  )
  public void processConnectionLag(PacketEvent event) {
  }

  private boolean connectionLag(Player player) {
    User user = UserRepository.userOf(player);
    return false;
  }

  public boolean releaseConnectionLag(User user) {
    return false;
  }
}
