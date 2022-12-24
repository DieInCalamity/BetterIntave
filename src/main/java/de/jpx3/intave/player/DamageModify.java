package de.jpx3.intave.player;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import static org.bukkit.event.entity.EntityDamageEvent.DamageModifier.BASE;

public final class DamageModify {
  public static double attackDamageOf(Player player) {
    if (MinecraftVersions.VER1_9_0.atOrAbove()) {
      return (float) player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
    } else {
      try {
        User user = UserRepository.userOf(player);
        EntityLiving entityLiving = (EntityLiving) user.playerHandle();
        return entityLiving.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
      } catch (Exception e) {
        e.printStackTrace();
        return 1.0F;
      }
    }
  }

  public static double sharpnessDamageOf(ItemStack heldItem) {
    if (heldItem == null) {
      return 0;
    }
    return heldItem.getEnchantmentLevel(Enchantment.DAMAGE_ALL) * 1.25;
  }

  public static void withNewDamageApplier(
    EntityDamageEvent damageEvent,
    DamageModifier modifier,
    UnaryOperator<Double> modifierFunction
  ) {
    Map<DamageModifier, Function<? super Double, Double>> damageModifierMap = modifierFunctionsOf(damageEvent);
    damageModifierMap.put(modifier, modifierFunction::apply);
    double baseDamage = damageEvent.getDamage(BASE);
    for (DamageModifier damageModifier : DamageModifier.values()) {
      if (!damageModifierMap.containsKey(damageModifier)) {
        continue;
      }
      float apply = damageModifierMap.get(damageModifier).apply(baseDamage).floatValue();
      baseDamage += apply;
      if (!damageModifier.equals(BASE)) {
        damageEvent.setDamage(damageModifier, apply);
      }
    }
  }

  public static void modifyDamageApplier(
    EntityDamageEvent damageEvent,
    DamageModifier modifier,
    BinaryOperator<Double> modifierFunction
  ) {
    Map<DamageModifier, Function<? super Double, Double>> damageModifierMap = modifierFunctionsOf(damageEvent);
//    Function<Double, Double> newFunction = modifierFunction::apply;
    Function<? super Double, Double> oldFunction = damageModifierMap.getOrDefault(modifier, Functions.identity());
    Function<Double, Double> combinedFunction = t -> modifierFunction.apply(t, oldFunction.apply(t));
    damageModifierMap.put(modifier, combinedFunction);
    double baseDamage = damageEvent.getDamage(BASE);
    for (DamageModifier damageModifier : DamageModifier.values()) {
      if (!damageModifierMap.containsKey(damageModifier)) {
        continue;
      }
      float apply = damageModifierMap.get(damageModifier).apply(baseDamage).floatValue();
      baseDamage += apply;
      if (!damageModifier.equals(BASE)) {
        damageEvent.setDamage(damageModifier, apply);
      }
    }
  }

  public static void refreshModifiers(
    EntityDamageEvent damageEvent
  ) {
    Map<DamageModifier, Function<? super Double, Double>> damageModifierMap = modifierFunctionsOf(damageEvent);
    double baseDamage = damageEvent.getDamage(BASE);
    for (DamageModifier damageModifier : DamageModifier.values()) {
      if (!damageModifierMap.containsKey(damageModifier)) {
        continue;
      }
      float apply = damageModifierMap.get(damageModifier).apply(baseDamage).floatValue();
      baseDamage += apply;
      if (!damageModifier.equals(BASE)) {
        damageEvent.setDamage(damageModifier, apply);
      }
    }
  }

  private static final Field DAMAGE_MODIFIER_FUNCTION_FIELD;

  static {
    try {
      DAMAGE_MODIFIER_FUNCTION_FIELD = EntityDamageEvent.class.getDeclaredField("modifierFunctions");
      DAMAGE_MODIFIER_FUNCTION_FIELD.setAccessible(true);
    } catch (NoSuchFieldException exception) {
      throw new IllegalStateException(exception);
    }
  }

  private static Map<DamageModifier, Function<? super Double, Double>> modifierFunctionsOf(EntityDamageEvent damageEvent) {
    try {
      //noinspection unchecked
      return (Map<DamageModifier, Function<? super Double, Double>>) DAMAGE_MODIFIER_FUNCTION_FIELD.get(damageEvent);
    } catch (IllegalAccessException exception) {
      throw new IllegalStateException(exception);
    }
  }
}
