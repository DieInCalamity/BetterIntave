package de.jpx3.intave.command.translator;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.command.TypeTranslator;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class DoubleTranslator extends TypeTranslator<Double> {
  public DoubleTranslator() {
    super(Double.class);
  }

  @Override
  public Double resolve(CommandSender commandSender, String element, String forward) {
    try {
      double value = Double.parseDouble(element);
      if (value == Double.POSITIVE_INFINITY || value == Double.NEGATIVE_INFINITY || Double.isNaN(value)) {
        commandSender.sendMessage(IntavePlugin.prefix() + ChatColor.RED + "Invalid argument " + element + ": Must be valid double");
        return null;
      }
      return value;
    } catch (Exception exception) {
      commandSender.sendMessage(IntavePlugin.prefix() + ChatColor.RED + "Invalid argument " + element + ": Must be valid number");
      return null;
    }
  }

  @Override
  public List<String> settingConstrains(CommandSender commandSender) {
    return null;
  }
}
