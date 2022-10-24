package de.jpx3.intave.block.variant.convert;

import de.jpx3.intave.block.variant.Setting;

import java.util.Map;

/**
 * Class generated using IntelliJ IDEA
 * Created by Richard Strunk 2022
 */

public interface ConversionBridge {
  Map<Setting<?>, Comparable<?>> settingsOf(Object blockData);
}
