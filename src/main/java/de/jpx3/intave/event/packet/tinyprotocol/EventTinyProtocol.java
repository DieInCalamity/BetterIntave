package de.jpx3.intave.event.packet.tinyprotocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.packet.PacketRegistry;
import de.jpx3.intave.event.packet.LocalPacketAdapter;
import de.jpx3.intave.tools.annotate.DoNotFlowObfuscate;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

@DoNotFlowObfuscate
public final class EventTinyProtocol extends TinyProtocol {
  private final InjectionService injectionService;

  public EventTinyProtocol(Plugin plugin, InjectionService injectionService) {
    super(plugin);
    this.injectionService = injectionService;
  }

  @Override
  public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
    PacketType packetType = PacketRegistry.getPacketType(packet.getClass());
    if (packetType != null) {
      Collection<LocalPacketAdapter> subscriptions = injectionService.subscriptionsOf(packetType);
      if(subscriptions != null && !subscriptions.isEmpty()) {
        PacketEvent packetEvent = PacketEvent.fromServer(packet, PacketContainer.fromPacket(packet), receiver);
        subscriptions.forEach(subscription -> subscription.onPacketSending(packetEvent));
        if(packetEvent.isCancelled()) {
          return packet;
        }
      }
    }
    return super.onPacketOutAsync(receiver, channel, packet);
  }
}
