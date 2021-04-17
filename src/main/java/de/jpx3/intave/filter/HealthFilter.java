package de.jpx3.intave.filter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.event.packet.PacketDescriptor;
import de.jpx3.intave.event.packet.PacketSubscription;
import de.jpx3.intave.event.packet.Sender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public final class HealthFilter extends Filter {
  private final IntavePlugin plugin;

  public HealthFilter(IntavePlugin plugin) {
    super("health");
    this.plugin = plugin;
  }

  @PacketSubscription(
    packets = {
      @PacketDescriptor(sender = Sender.SERVER, packetName = "ENTITY_METADATA")
    }
  )
  public void depriveHealth(PacketEvent event) {
    if(!enabled()) {
      return;
    }
    try {
      if (event.getPacket().getIntegers().getValues().isEmpty()) {
        return;
      }
      PacketContainer packet = event.getPacket();
      Entity entity = event.getPacket().getEntityModifier(event).readSafely(0);
      if (entity == null) {
        return;
      }
      if (entity instanceof LivingEntity && entity.getUniqueId() != event.getPlayer().getUniqueId())
        if (packet.getWatchableCollectionModifier().read(0) != null) {
          packet = packet.deepClone();
          event.setPacket(packet);
          if (event.getPacket().getType() == PacketType.Play.Server.ENTITY_METADATA) {
            WrappedDataWatcher watcher = new WrappedDataWatcher(packet.getWatchableCollectionModifier().read(0));
            stripHealthFromDataWatcher(watcher);
            packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
          }
        }
    } catch (Exception ignored) {}
  }

  private void stripHealthFromDataWatcher(WrappedDataWatcher watcher) {
    if (watcher != null && watcher.getObject(6) != null && watcher.getFloat(6) != 0.0F) {
      watcher.setObject(6, Float.NaN);
    }
  }

  @Override
  protected boolean enabled() {
    return false;
//    return super.enabled();
  }
}
