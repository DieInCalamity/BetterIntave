package de.jpx3.intave.module.linker.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import de.jpx3.intave.cleanup.Shutdown;
import org.bukkit.plugin.Plugin;

public abstract class WeakReferencePacketAdapter extends PacketAdapter {
  public WeakReferencePacketAdapter(Plugin plugin, PacketType... types) {
    super(plugin, types);
  }

  public WeakReferencePacketAdapter(Plugin plugin, ListenerPriority listenerPriority, Iterable<? extends PacketType> types) {
    super(plugin, listenerPriority, types);
  }

  public WeakReferencePacketAdapter(Plugin plugin, ListenerPriority listenerPriority, PacketType... types) {
    super(plugin, listenerPriority, types);
  }

  private void setupShutdownTask() {
    Shutdown.addTask(this::tryRemovePluginReference);
  }

  public void tryRemovePluginReference() {
    plugin = null;
  }
}
