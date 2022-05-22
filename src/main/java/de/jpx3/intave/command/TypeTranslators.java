package de.jpx3.intave.command;

import de.jpx3.intave.command.translator.*;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public final class TypeTranslators {
  private static final Map<Class<?>, TypeTranslator<?>> typeTranslatorMap = new HashMap<>();

  static {
    add(IntegerTranslator.class);
    add(StringArrayTranslator.class);
    add(StringTranslator.class);
    add(PlayerTranslator.class);
    add(PlayerArrayTranslator.class);
  }

  private static void add(Class<? extends TypeTranslator<?>> typeTranslatorClass) {
    TypeTranslator<?> typeTranslator;
    try {
      typeTranslator = typeTranslatorClass.newInstance();
    } catch (InstantiationException | IllegalAccessException exception) {
      throw new IllegalStateException(exception);
    }
    typeTranslatorMap.put(typeTranslator.type(), typeTranslator);
  }

  public static Object tryTranslate(CommandSender player, Class<?> type, String element, String forward) {
    if (type.isEnum()) {
      Enum<?>[] enumConstants = (Enum<?>[]) type.getEnumConstants();
      for (Enum<?> enumConstant : enumConstants) {
        if (enumConstant.name().equalsIgnoreCase(element) || niceifyEnumName(enumConstant.name()).equalsIgnoreCase(element)) {
          return enumConstant;
        }
      }
      List<String> types = Arrays.stream(enumConstants)
        .map(enumConstant -> niceifyEnumName(enumConstant.name()))
        .collect(Collectors.toList());
      return "Unknown element \"" + element + "\": Expected " + describeListSelection(types);
    }
    TypeTranslator<?> typeTranslator = typeTranslatorMap.get(type);
    if (typeTranslator == null) {
      return "Invalid type: " + type;
    }
    return typeTranslator.resolve(player, element, forward);
  }

  private static String describeListSelection(List<String> elements) {
    int size = elements.size();
    if (size == 0) {
      return "nothing";
    } else if (size == 1) {
      return "only " + elements.get(0);
    } else {
      return "either " + String.join(", ", elements.subList(0, size - 1)) + " or " + elements.get(size - 1);
    }
  }

  public static List<String> findTabCompletes(CommandSender player, Class<?> type, String element, String forward) {
    if (type.isEnum()) {
      Enum<?>[] enumConstants = (Enum<?>[]) type.getEnumConstants();
      List<String> strings = new ArrayList<>();
      for (Enum<?> enumConstant : enumConstants) {
        strings.add(niceifyEnumName(enumConstant.name()));
      }
      return strings;
    }
    TypeTranslator<?> typeTranslator = typeTranslatorMap.get(type);
    return typeTranslator == null ? Collections.emptyList() : typeTranslator.settingConstrains(player);
  }

  private static String niceifyEnumName(String input) {
    return firstToUppercase(input.toLowerCase(Locale.ROOT).replace("_", ""));
  }

  private static String firstToUppercase(String string) {
    return string.substring(0, 1).toUpperCase(Locale.ROOT) + string.substring(1).toLowerCase(Locale.ROOT);
  }
}
