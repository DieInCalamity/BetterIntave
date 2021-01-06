package de.jpx3.intave.world.permission;

import de.jpx3.dynref.annotate.DynRefAutoTranslation;
import de.jpx3.dynref.annotate.DynRefTranslateParameters;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;

@DynRefAutoTranslation
public final class CustomCraftBlock extends CraftBlock {
  private final int typeId, data;

  @DynRefAutoTranslation
  @DynRefTranslateParameters
  public CustomCraftBlock(CraftChunk chunk, int x, int y, int z, int typeId, int data) {
    super(chunk, x, y, z);
    this.typeId = typeId;
    this.data = data;
  }

  @Override
  public int getTypeId() {
    return typeId;
  }

  @Override
  public byte getData() {
    return (byte) data;
  }
}
