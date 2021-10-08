package de.jpx3.intave.block.physics;

import com.comphenix.protocol.utility.MinecraftVersion;
import com.google.common.collect.ImmutableList;
import de.jpx3.intave.block.fluid.Fluid;
import de.jpx3.intave.block.fluid.FluidTag;
import de.jpx3.intave.block.fluid.Fluids;
import de.jpx3.intave.shade.Motion;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.MovementMetadata;
import de.jpx3.intave.user.meta.ProtocolMetadata;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

final class FluidPhysics implements BlockPhysic {
  private List<Material> materials;

  @Override
  public void setup(MinecraftVersion serverVersion) {
    List<Material> materials = new ArrayList<>();
    Material stationaryLava = Material.getMaterial("STATIONARY_LAVA");
    if (stationaryLava != null) {
      materials.add(stationaryLava);
    }
    materials.add(Material.LAVA);
    this.materials = ImmutableList.copyOf(materials);
  }

  @Override
  public Motion entityCollidedWithBlock(User user, Location location, Location from, double motionX, double motionY, double motionZ) {
    ProtocolMetadata clientData = user.meta().protocol();
    if (clientData.waterUpdate()) {
      MovementMetadata movementData = user.meta().movement();
      Fluid fluid = Fluids.fluidAt(user, location);
      if (fluid.isOf(FluidTag.LAVA)) {
        movementData.aquaticUpdateInLava = true;
      }
    }
    return null;
  }

  @Override
  public List<Material> applicableMaterials() {
    return materials;
  }
}