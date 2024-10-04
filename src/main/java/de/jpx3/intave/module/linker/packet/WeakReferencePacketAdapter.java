package de.jpx3.intave.module.linker.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public abstract class WeakReferencePacketAdapter extends PacketAdapter {
  static final ListenerOptions[] ALLOW_ASYNC_SENDING = new ListenerOptions[] {ListenerOptions.ASYNC};

  public WeakReferencePacketAdapter(Plugin plugin, PacketType... types) {
    super(plugin, types);
  }

  public WeakReferencePacketAdapter(Plugin plugin, ListenerPriority listenerPriority, Iterable<? extends PacketType> types) {
    super(plugin, listenerPriority, types);
  }

  public WeakReferencePacketAdapter(Plugin plugin, ListenerPriority listenerPriority, PacketType[] types, ListenerOptions[] options) {
    super(plugin, listenerPriority, Arrays.asList(types), options);
  }

  public void tryRemovePluginReference() {
    plugin = null;
  }
}
