package de.jpx3.intave.world.raytrace;

import de.jpx3.intave.clazz.rewrite.PatchyAutoTranslation;
import de.jpx3.intave.shade.MovingObjectPosition;
import de.jpx3.intave.shade.NativeVector;
import net.minecraft.server.v1_13_R2.FluidCollisionOption;
import net.minecraft.server.v1_13_R2.Vec3D;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.entity.Player;

@PatchyAutoTranslation
public final class v13Raytracer implements Raytracer {
  @Override
  @PatchyAutoTranslation
  public MovingObjectPosition raytrace(World world, Player player, NativeVector eyeVector, NativeVector targetVector) {
    net.minecraft.server.v1_13_R2.MovingObjectPosition movingObjectPosition = ((CraftWorld) world).getHandle().rayTrace(
      (Vec3D) eyeVector.convertToNativeVec3(),
      (Vec3D) targetVector.convertToNativeVec3(),
      FluidCollisionOption.NEVER,
      false, false
    );
    return MovingObjectPosition.fromNativeMovingObjectPosition(movingObjectPosition);
  }
}
