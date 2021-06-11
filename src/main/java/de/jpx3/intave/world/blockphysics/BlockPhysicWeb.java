package de.jpx3.intave.world.blockphysics;

import com.comphenix.protocol.utility.MinecraftVersion;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserMetaClientData;
import de.jpx3.intave.user.UserMetaMovementData;
import de.jpx3.intave.world.blockaccess.BlockTypeAccess;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;

import static de.jpx3.intave.user.UserMetaClientData.VER_1_15;

final class BlockPhysicWeb implements BlockPhysic {
  private List<Material> material;

  @Override
  public void setup(MinecraftVersion serverVersion) {
    material = Collections.singletonList(BlockTypeAccess.WEB);
  }

  @Override
  public Vector entityCollidedWithBlock(User user, Location location, Location from, double motionX, double motionY, double motionZ) {
    UserMetaMovementData movementData = user.meta().movementData();
    movementData.inWeb = true;
    movementData.artificialFallDistance = 0;

    UserMetaClientData clientData = user.meta().clientData();
    if (clientData.protocolVersion() >= VER_1_15) {
      return new Vector(motionX * 0.25, motionY * 0.05f, motionZ * 0.25);
    }
    return null;
  }


  @Override
  public List<Material> materials() {
    return material;
  }
}