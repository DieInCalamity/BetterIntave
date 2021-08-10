package de.jpx3.intave.world.fluid.resolver;

import de.jpx3.intave.patchy.annotate.PatchyAutoTranslation;
import de.jpx3.intave.patchy.annotate.PatchyTranslateParameters;
import de.jpx3.intave.tools.wrapper.WrappedVector;
import de.jpx3.intave.tools.wrapper.link.WrapperLinkage;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.MovementMetadata;
import de.jpx3.intave.world.fluid.FluidEngine;
import de.jpx3.intave.world.fluid.FluidTag;
import de.jpx3.intave.world.fluid.WrappedFluid;
import net.minecraft.server.v1_16_R3.*;

@PatchyAutoTranslation
public final class v16FluidResolver extends FluidEngine {
  @Override
  @PatchyAutoTranslation
  protected WrappedFluid fluidAt(User user, int x, int y, int z) {
    MovementMetadata movementData = user.meta().movement();
    World world = (World) movementData.nmsWorld();
    if (!world.isChunkLoaded(x >> 4, z >> 4)) {
      return WrappedFluid.empty();
    }
    Fluid fluid = world.getFluid(new BlockPosition(x, y, z));
    float height = fluid.d();
    FluidTag fluidTag = fluid.isEmpty() ? FluidTag.EMPTY : resolveFluidTagOf(fluid);
    return WrappedFluid.construct(fluidTag, height);
  }

  @PatchyAutoTranslation
  @PatchyTranslateParameters
  private FluidTag resolveFluidTagOf(Fluid fluid) {
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
  protected WrappedVector flowVectorAt(User user, int x, int y, int z) {
    MovementMetadata movementData = user.meta().movement();
    IWorldReader world = (World) movementData.nmsWorld();
    BlockPosition blockPosition = new BlockPosition(x, y, z);
    return WrapperLinkage.vectorOf(world.getFluid(blockPosition).c(world, blockPosition));
  }
}