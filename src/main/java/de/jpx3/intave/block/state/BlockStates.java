package de.jpx3.intave.block.state;

import java.util.ArrayList;
import java.util.List;

public final class BlockStates {
  private final static List<BlockStateData<?>> blockStates = new ArrayList<>();

  public static void setup(BlockStateData<?> blockState) {
    blockStates.add(blockState);
  }

  public BlockStateData<?> ofNativeState(Object nativeBlockState) {
    return null;
  }
}
