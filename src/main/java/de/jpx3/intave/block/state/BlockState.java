package de.jpx3.intave.block.state;

import de.jpx3.intave.user.User;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public final class BlockState {
  public BlockState(List<BlockStateData<?>> blockStates) {
    for (BlockStateData<?> blockState : blockStates) {
      blockState.build();
    }
  }

  public <T> T valueOf(User user, Block block, BlockStateData<T> blockStateData) {
    return blockStateData.value(user, block);
  }

  public static Builder builder() {
    return new Builder();
  }

  public final static class Builder {
    private final List<BlockStateData<?>> blockStates = new ArrayList<>();

    private Builder() {
    }

    public Builder with(BlockStateData<?> retriever) {
      this.blockStates.add(retriever);
      return this;
    }

    public BlockState build() {
      return new BlockState(blockStates);
    }
  }
}