package de.jpx3.intave.block.fluid;

import de.jpx3.intave.block.access.VolatileBlockAccess;
import de.jpx3.intave.block.variant.BlockVariant;
import de.jpx3.intave.block.variant.BlockVariantRegister;
import de.jpx3.intave.share.NativeVector;
import de.jpx3.intave.user.User;
import org.bukkit.Material;

import java.util.*;
import java.util.function.Function;

public final class DynamicFluidResolver extends FluidResolver {
  private static final Map<Material, Set<Integer>> waterloggedVariants = new EnumMap<>(Material.class);
  private static final Map<Material, Function<Integer, Float>> levelToHeight = new EnumMap<>(Material.class);

  static {
    for (Material value : Material.values()) {
      if (value.isBlock()) {
        BlockVariant variant = BlockVariantRegister.uncachedVariantOf(value, 0);
        Object waterlogged = variant.propertyOf("waterlogged");

        // waterlogged probe
        Set<Integer> variants;
        Map<Integer, Integer> variantToHeight = new HashMap<>();
        if (waterlogged != null) {
          variants = new HashSet<>();
//          variants.add(variant.variantIndex());

          for (Integer variantId : BlockVariantRegister.variantIdsOf(value)) {
            BlockVariant anyVariant = BlockVariantRegister.uncachedVariantOf(value, variantId);
            Object anyWaterlogged = anyVariant.propertyOf("waterlogged");
            Object level = anyVariant.propertyOf("level");
            if (anyWaterlogged == Boolean.TRUE) {
              variants.add(variantId);
            }
            if (level != null) {
              variantToHeight.put(variantId, (Integer) level);
              System.out.println("level: " + level + " variant: " + variantId);
            }
          }

        } else {
          variants = Collections.emptySet();
        }
        waterloggedVariants.put(value, variants);
        if (variantToHeight.isEmpty()) {
          levelToHeight.put(value, ((Function<Integer, Integer>)variantToHeight::get)
            .andThen(integer -> integer == null ? 0 : integer)
            .andThen(integer -> integer / 9.0f)
          );
        }
      }
    }
//    System.out.println(waterloggedVariants);
//    Integer id = waterloggedVariants.get(Material.getMaterial("WARPED_HANGING_SIGN")).iterator().next();
//    BlockVariantRegister.uncachedVariantOf(Material.getMaterial("WARPED_HANGING_SIGN"), id).dumpStates();
  }

  @Override
  protected Fluid fluidAt(User user, int x, int y, int z) {
    Material type = VolatileBlockAccess.typeAccess(user, x, y, z);
    Set<Integer> waterloggedVariants = DynamicFluidResolver.waterloggedVariants.get(type);
    if (waterloggedVariants.isEmpty()) {
      return Fluid.empty();
    }
    int variantId = VolatileBlockAccess.variantIndexAccess(user, user.player().getWorld(), x, y, z);
    if (waterloggedVariants.contains(variantId)) {
      if (levelToHeight.get(type) != null) {
        return Fluid.of(FluidTag.WATER, true, levelToHeight.get(type).apply(variantId));
      }
      return Fluid.of(FluidTag.WATER, true, 1.0f);
    }
    return Fluid.empty();
  }

  @Override
  protected NativeVector flowVectorAt(User user, int x, int y, int z) {
    return NativeVector.ZERO;
  }
}
