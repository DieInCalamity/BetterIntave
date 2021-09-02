package de.jpx3.intave.world.raytrace;

import de.jpx3.intave.shade.MovingObjectPosition;
import de.jpx3.intave.shade.NativeVector;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface Raytracer {
  MovingObjectPosition raytrace(World world, Player player, NativeVector eyeVector, NativeVector targetVector);
}
