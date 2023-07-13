package de.jpx3.intave.module.tracker.player;

import de.jpx3.intave.module.Module;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Map;

import static de.jpx3.intave.IntaveControl.ENABLE_MOVEMENT_DEBUGGER_COLLECTOR;

public final class MovementDebugTracker extends Module implements PluginMessageListener {

  @Override
  public void enable() {
    Messenger messenger = Bukkit.getMessenger();
    messenger.registerIncomingPluginChannel(plugin, "intave:movdebug", this);
  }

  @Override
  public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
    if (!ENABLE_MOVEMENT_DEBUGGER_COLLECTOR) {
      return;
    }
    User user = UserRepository.userOf(player);
    if (s.equals("intave:movdebug")) {
      Map<String, Double> movementDebugValues = user.meta().movement().clientMovementDebugValues;
      ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
      int length = byteBuf.readInt();
      if (length > 100 || length < 0) {
        user.kick("Too many debug parameters " + length);
      }

      int maxReads = 10;
      while (length > 0 && maxReads-- > 0) {
        int nameLength = byteBuf.readInt();

        if (nameLength > 100 || nameLength < 0) {
          user.kick("Invalid movement debug name length: " + nameLength);
        }

        // read chars
        char[] chars = new char[nameLength];
        for (int i = 0; i < nameLength; i++) {
          chars[i] = byteBuf.readChar();
        }
        String name = new String(chars);
        double value = byteBuf.readDouble();
        movementDebugValues.put(name, value);
        length--;
      }
    }
  }
}
