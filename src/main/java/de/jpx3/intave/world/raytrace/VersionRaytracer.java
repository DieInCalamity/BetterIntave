package de.jpx3.intave.world.raytrace;

import de.jpx3.intave.tools.wrapper.WrappedMovingObjectPosition;
import de.jpx3.intave.tools.wrapper.WrappedVector;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface VersionRaytracer {
  WrappedMovingObjectPosition raytrace(World world, Player player, WrappedVector eyeVector, WrappedVector targetVector);
}
