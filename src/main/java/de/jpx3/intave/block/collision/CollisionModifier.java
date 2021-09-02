package de.jpx3.intave.block.collision;

import de.jpx3.intave.shade.BoundingBox;
import de.jpx3.intave.user.User;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public abstract class CollisionModifier {
  private Material[] materials;

  protected CollisionModifier() {
  }

  protected CollisionModifier(Material... materials) {
    this.materials = materials;
  }

  public abstract List<BoundingBox> modify(User user, BoundingBox userBox, int posX, int posY, int posZ, List<BoundingBox> boxes);

  public boolean matches(Material material) {
    return Arrays.asList(materials).contains(material);
  }
}
