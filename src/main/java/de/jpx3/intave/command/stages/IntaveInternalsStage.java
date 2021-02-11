package de.jpx3.intave.command.stages;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.command.CommandStage;
import de.jpx3.intave.command.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class IntaveInternalsStage extends CommandStage {
  private static IntaveInternalsStage singletonInstance;
  private final IntavePlugin plugin;

  private IntaveInternalsStage() {
    super(IntaveCommandStage.singletonInstance(), "internals", 1);
    plugin = IntavePlugin.singletonInstance();
  }

  @SubCommand(
    selectors = "sendnotify",
    usage = "<message...>",
    permission = "intave.command.internals.sendnotify",
    description = "Send notifications"
  )
  public void internalCommand(CommandSender commandSender, String[] message) {
    String fullMessage = Arrays.stream(message).map(s -> s + " ").collect(Collectors.joining()).trim();
    plugin.violationProcessor().broadcastNotify(fullMessage);
  }

  public static IntaveInternalsStage singletonInstance() {
    if(singletonInstance == null) {
      singletonInstance = new IntaveInternalsStage();
    }
    return singletonInstance;
  }
}
