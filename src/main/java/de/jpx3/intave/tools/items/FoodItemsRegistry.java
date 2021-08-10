package de.jpx3.intave.tools.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.jpx3.intave.event.dispatch.PlayerAbilityEvaluator;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import de.jpx3.intave.user.meta.AbilityMetadata;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

import static de.jpx3.intave.tools.items.BukkitItemResolver.materialByName;

public final class FoodItemsRegistry {
  // <Material, foodLevelAffected>
  private static final Map<Material, Boolean> materialFoodList = Maps.newHashMap();

  public FoodItemsRegistry() {
    setupFood();
  }

  private static final class ConsumableItem {
    private final Material material;
    private final boolean foodLevelAffected;

    public ConsumableItem(Material material, boolean foodLevelAffected) {
      this.material = material;
      this.foodLevelAffected = foodLevelAffected;
    }
  }

  private void setupFood() {
    List<ConsumableItem> foodItems = Lists.newArrayList(
      resolveConsumable("apple", true),
      resolveConsumable("bread", true),
      resolveConsumable("porkchop", true),
      resolveConsumable("cooked_porkchop", true),
      resolveConsumable("pork", true),
      resolveConsumable("grilled_pork", true),
      resolveConsumable("cookie", true),
      resolveConsumable("melon", true),
      resolveConsumable("beef", true),
      resolveConsumable("raw_beef", true),
      resolveConsumable("cooked_beef", true),
      resolveConsumable("chicken", true),
      resolveConsumable("cooked_chicken", true),
      resolveConsumable("rotten_flesh", true),
      resolveConsumable("spider_eye", true),
      resolveConsumable("baked_potato", true),
      resolveConsumable("poisonous_potato", true),
      resolveConsumable("golden_carrot", true),
      resolveConsumable("pumpkin_pie", true),
      resolveConsumable("rabbit", true),
      resolveConsumable("cooked_rabbit", true),
      resolveConsumable("mutton", true),
      resolveConsumable("cooked_mutton", true),
      resolveConsumable("mushroom_soup", true),
      resolveConsumable("raw_fish", true),
      resolveConsumable("cooked_fish", true),
      resolveConsumable("raw_chicken", true),
      resolveConsumable("carrot_item", true),
      resolveConsumable("potato_item", true),
      resolveConsumable("rabbit_stew", true),
      resolveConsumable("golden_apple", false),
      resolveConsumable("enchanted_golden_apple", false)
    );

    for (ConsumableItem foodItem : foodItems) {
      if (foodItem != null) {
        materialFoodList.put(foodItem.material, foodItem.foodLevelAffected);
      }
    }
  }

  public boolean foodConsumable(Player player, Material type) {
    User user = UserRepository.userOf(player);
    AbilityMetadata abilityData = user.meta().abilities();
    boolean creative = abilityData.inGameMode(PlayerAbilityEvaluator.GameMode.CREATIVE);
    if (creative) {
      return false;
    }
    if (materialFoodList.containsKey(type)) {
      int foodLevel = user.player().getFoodLevel();
      boolean foodLevelAffected = materialFoodList.get(type);
      return !foodLevelAffected || foodLevel < 20;
    }
    return false;
  }

  private ConsumableItem resolveConsumable(String name, boolean foodLevelAffected) {
    Material material = materialByName(name);
    return material != null ? new ConsumableItem(material, foodLevelAffected) : null;
  }
}