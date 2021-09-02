package de.jpx3.intave.block.state;

public final class BlockStateBoolean extends BlockStateData<Boolean> {
  private Object converter;

  private BlockStateBoolean(String name, boolean defaultValue) {
    super(name, defaultValue, Boolean.class);
  }

  @Override
  public void build() {
    this.converter = BlockStateServerBridge.booleanStateOf(name());
  }

  @Override
  public Object convert() {
    return this.converter;
  }

  public static BlockStateBoolean of(String name) {
    return new BlockStateBoolean(name, false);
  }

  public static BlockStateBoolean of(String name, boolean defaultValue) {
    return new BlockStateBoolean(name, defaultValue);
  }
}