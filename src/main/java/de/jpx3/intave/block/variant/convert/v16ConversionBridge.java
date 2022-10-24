package de.jpx3.intave.block.variant.convert;

import de.jpx3.intave.block.variant.*;
import de.jpx3.intave.klass.rewrite.PatchyAutoTranslation;
import net.minecraft.server.v1_16_R1.IBlockState;

import java.util.*;

@PatchyAutoTranslation
public final class v16ConversionBridge implements ConversionBridge {
  @PatchyAutoTranslation
  public Map<Setting<?>, Comparable<?>> settingsOf(Object blockData) {
    net.minecraft.server.v1_16_R1.IBlockData data = (net.minecraft.server.v1_16_R1.IBlockData) blockData;
    Set<IBlockState<?>> states = data.getStateMap().keySet();
    if (states.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<Setting<?>, Comparable<?>> configuration = new HashMap<>();
    for (net.minecraft.server.v1_16_R1.IBlockState<?> state : states) {
      Setting<?> setting = SettingCache.computeSettingIfAbsent(state, this::convert);
      configuration.put(setting, convertEnumToIndexIfPresent(data.get(state)));
    }
    return configuration;
  }

  @PatchyAutoTranslation
  private Setting<?> convert(Object blockState) {
    net.minecraft.server.v1_16_R1.IBlockState<?> state = (net.minecraft.server.v1_16_R1.IBlockState<?>) blockState;
    String name = state.getName();
    if (state instanceof net.minecraft.server.v1_16_R1.BlockStateInteger) {
      net.minecraft.server.v1_16_R1.BlockStateInteger blockStateInteger =
        (net.minecraft.server.v1_16_R1.BlockStateInteger) state;
      Collection<Integer> values = blockStateInteger.getValues();
      IntSummaryStatistics statistics = values.stream().mapToInt(value -> value).summaryStatistics();
      return Settings.integerSetting(name, statistics.getMin(), statistics.getMax());
    } else if (state instanceof net.minecraft.server.v1_16_R1.BlockStateBoolean) {
      return Settings.booleanSetting(name);
    } else if (state instanceof net.minecraft.server.v1_16_R1.BlockStateEnum) {
      return Settings.enumSetting(name, state.getType(), state.getValues());
    }
    throw new IllegalStateException("Unknown block state " + state + " (" + name + ")");
  }

  @PatchyAutoTranslation
  private static Comparable<?> convertEnumToIndexIfPresent(Comparable<?> initial) {
    if (initial.getClass().isEnum()) {
      return ((Enum<?>) initial).ordinal();
    }
    return initial;
  }
}
