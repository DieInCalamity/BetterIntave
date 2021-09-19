package de.jpx3.intave.block.fluid.resolver;

import de.jpx3.intave.block.fluid.Fluid;
import de.jpx3.intave.block.fluid.FluidEngine;
import de.jpx3.intave.block.fluid.FluidTag;
import de.jpx3.intave.clazz.rewrite.PatchyAutoTranslation;
import de.jpx3.intave.clazz.rewrite.PatchyTranslateParameters;
import de.jpx3.intave.shade.NativeVector;
import de.jpx3.intave.shade.link.WrapperLinkage;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.MovementMetadata;
import net.minecraft.server.v1_13_R2.*;

@PatchyAutoTranslation
public final class v13FluidResolver extends FluidEngine {
  @Override
  @PatchyAutoTranslation
  protected Fluid fluidAt(User user, int x, int y, int z) {
    MovementMetadata movementData = user.meta().movement();
    World world = (World) movementData.nmsWorld();
    if (!world.isChunkLoaded(x >> 4, z >> 4, false)) {
      return Fluid.empty();
    }
    net.minecraft.server.v1_13_R2.Fluid fluid = world.getFluid(new BlockPosition(x, y, z));
    FluidTag fluidTag = resolveFluidTagOf(fluid);
    if (fluidTag == FluidTag.EMPTY) {
      return Fluid.empty();
    }
    float height = fluid.getHeight();
    return Fluid.construct(fluidTag, fluid.d(), height);
  }

  @PatchyAutoTranslation
  @PatchyTranslateParameters
  private FluidTag resolveFluidTagOf(net.minecraft.server.v1_13_R2.Fluid fluid) {
    if (fluid.e()) {
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
    IWorldReader world = (World) movementData.nmsWorld();
    BlockPosition blockPosition = new BlockPosition(x, y, z);
    return WrapperLinkage.vectorOf(world.getFluid(blockPosition).a(world, blockPosition));
  }
}