package de.jpx3.intave.block.variant.convert;

import de.jpx3.intave.block.variant.Setting;
import de.jpx3.intave.block.variant.Settings;
import de.jpx3.intave.klass.rewrite.PatchyAutoTranslation;
import net.minecraft.server.v1_14_R1.*;

import java.util.*;

@PatchyAutoTranslation
final class v14ConversionBridge implements ConversionBridge{
  @PatchyAutoTranslation
  public Map<Setting<?>, Comparable<?>> settingsOf(Object blockData) {
    IBlockData data = (IBlockData) blockData;
    Set<IBlockState<?>> states = data.getStateMap().keySet();
    if (states.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<Setting<?>, Comparable<?>> configuration = new HashMap<>();
    for (IBlockState<?> state : states) {
      Setting<?> setting = SettingCache.computeSettingIfAbsent(state, this::convertSetting);
      configuration.put(setting, convertEnumToIndexIfPresent(data.get(state)));
    }
    return configuration;
  }

  @PatchyAutoTranslation
  private Setting<?> convertSetting(Object blockState) {
    IBlockState<?> state = (IBlockState<?>) blockState;
    String name = state.a();
    if (state instanceof BlockStateInteger) {
      BlockStateInteger blockStateInteger = (BlockStateInteger) state;
      Collection<Integer> values = blockStateInteger.getValues();
      IntSummaryStatistics statistics = values.stream().mapToInt(value -> value).summaryStatistics();
      return Settings.integerSetting(name, statistics.getMin(), statistics.getMax());
    } else if (state instanceof BlockStateBoolean) {
      return Settings.booleanSetting(name);
    } else if (state instanceof BlockStateEnum) {
      return Settings.enumSetting(name, state.b(), state.getValues());
    }
    throw new IllegalStateException("Unknown block state " + state + " (" + name + ")");
  }

  @PatchyAutoTranslation
  private static Comparable<?> convertEnumToIndexIfPresent(Comparable<?> initial) {
    if (initial.getClass().isEnum()) {
      return ((Enum<?>) initial).name();
    }
    return initial;
  }
}
