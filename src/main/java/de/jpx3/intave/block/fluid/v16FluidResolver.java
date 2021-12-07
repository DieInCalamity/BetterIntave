package de.jpx3.intave.block.fluid;

import de.jpx3.intave.klass.rewrite.PatchyAutoTranslation;
import de.jpx3.intave.klass.rewrite.PatchyTranslateParameters;
import de.jpx3.intave.shade.NativeVector;
import de.jpx3.intave.shade.link.WrapperLinkage;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.MovementMetadata;
import net.minecraft.server.v1_16_R3.*;

@PatchyAutoTranslation
public final class v16FluidResolver extends FluidResolver {
  @Override
  @PatchyAutoTranslation
  protected Fluid fluidAt(User user, int x, int y, int z) {
    MovementMetadata movementData = user.meta().movement();
    World world = (World) movementData.nmsWorld();
    IBlockAccess blockAccess = world.getChunkProvider().c(x >> 4, z >> 4);
    if (blockAccess == null) {
      return Fluid.empty();
    }
    net.minecraft.server.v1_16_R3.Fluid fluid = blockAccess.getFluid(new BlockPosition(x, y, z));
    FluidTag fluidTag = resolveFluidTagOf(fluid);
    if (fluidTag == FluidTag.EMPTY) {
      return Fluid.empty();
    }
    float height = fluid.d();
    return Fluid.construct(fluidTag, fluid.isSource(), height);
  }

  @PatchyAutoTranslation
  @PatchyTranslateParameters
  private FluidTag resolveFluidTagOf(net.minecraft.server.v1_16_R3.Fluid fluid) {
    if (fluid.isEmpty()) {
      return FluidTag.EMPTY;
    }
    //noinspection unchecked
    if (fluid.a((Tag<FluidType>) FluidTag.WATER.nativeTag())) {
      return FluidTag.WATER;
    }
    //noinspection unchecked
    if (fluid.a((Tag<FluidType>) FluidTag.LAVA.nativeTag())) {
      return FluidTag.LAVA;
    }
    return FluidTag.EMPTY;
  }

  @Override
  @PatchyAutoTranslation
  protected NativeVector flowVectorAt(User user, int x, int y, int z) {
    MovementMetadata movementData = user.meta().movement();
    World world = (World) movementData.nmsWorld();
    IBlockAccess blockAccess = world.getChunkProvider().c(x >> 4, z >> 4);
    if (blockAccess == null) {
      return NativeVector.ZERO;
    }
    BlockPosition blockPosition = new BlockPosition(x, y, z);
    return WrapperLinkage.vectorOf(blockAccess.getFluid(blockPosition).c(blockAccess, blockPosition));
  }
}