package de.jpx3.intave.block.state;

public final class BlockStateInteger extends BlockStateData<Integer> {
  private final int min;
  private final int max;
  private Object converter;

  private BlockStateInteger(String name, int min, int max, int defaultValue) {
    super(name, defaultValue, Integer.class);
    this.min = min;
    this.max = max;
  }

  @Override
  public void build() {
    this.converter = BlockStateServerBridge.integerStateOf(name(), min, max);
  }

  @Override
  public Object convert() {
    return this.converter;
  }

  public static BlockStateInteger of(String name, int min, int max) {
    return new BlockStateInteger(name, min, max, min);
  }

  public static BlockStateInteger of(String name, int min, int max, int defaultValue) {
    return new BlockStateInteger(name, min, max, defaultValue);
  }
}