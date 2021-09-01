package de.jpx3.intave.reflect.entity.type;

import de.jpx3.intave.reflect.entity.size.HitboxSize;

import java.util.HashMap;
import java.util.Map;

final class EntityTypeDataRegistry implements EntityTypeDataResolver {
  private final static Map<Integer, EntityTypeData> entityTypeRegister = new HashMap<>();

  public static void setup() {
    registerTileEntity(1, "Item", HitboxSize.of(0.25F, 0.25F));
    registerTileEntity(2, "XPOrb", HitboxSize.of(0.5F, 0.5F));
    registerTileEntity(8, "LeashKnot", HitboxSize.of(0.5F, 0.5F));
    registerTileEntity(9, "Painting", HitboxSize.of(0.5F, 0.5F));
    registerTileEntity(10, "Arrow", HitboxSize.of(0.5F, 0.5F));
    registerTileEntity(11, "Snowball", HitboxSize.of(0.25F, 0.25F));
    registerTileEntity(12, "Fireball", HitboxSize.of(3.0F, 3.0F));
    registerTileEntity(13, "SmallFireball", HitboxSize.of(1.0F, 1.0F));
    registerTileEntity(14, "ThrownEnderpearl", HitboxSize.of(0.25F, 0.25F));
    registerTileEntity(15, "EyeOfEnderSignal", HitboxSize.of(0.25F, 0.25F));
    registerTileEntity(16, "ThrownPotion", HitboxSize.of(0.25F, 0.25F));
    registerTileEntity(17, "ThrownExpBottle", HitboxSize.of(0.25F, 0.25F));
    registerTileEntity(18, "ItemFrame", HitboxSize.of(0.5F, 0.5F));
    registerTileEntity(19, "WitherSkull", HitboxSize.of(0.3125F, 0.3125F));
    registerTileEntity(20, "PrimedTnt", HitboxSize.of(0.98F, 0.98F));
    registerTileEntity(21, "FallingSand", HitboxSize.of(0.98F, 0.98F));
    registerTileEntity(22, "FireworksRocketEntity", HitboxSize.of(0.25F, 0.25F));
    registerTileEntity(30, "ArmorStand", HitboxSize.of(0.5F, 1.975F));
    registerTileEntity(41, "Boat", HitboxSize.of(1.5F, 0.6F));
    registerTileEntity(42, "Minecart", HitboxSize.of(0.98F, 0.7F));
    registerTileEntity(43, "MinecartChest", HitboxSize.of(0.98F, 0.7F));
    registerTileEntity(44, "MinecartFurnace", HitboxSize.of(0.98F, 0.7F));
    registerTileEntity(45, "MinecartTNT", HitboxSize.of(0.98F, 0.7F));
    registerTileEntity(46, "MinecartHopper", HitboxSize.of(0.98F, 0.7F));
    registerTileEntity(47, "MinecartMobSpawner", HitboxSize.of(0.98F, 0.7F));
    registerTileEntity(40, "MinecartCommandBlock", HitboxSize.of(0.98F, 0.7F));
    registerLivingEntity(48, "Mob", HitboxSize.of(0, 0));
    registerLivingEntity(49, "Monster", HitboxSize.of(0, 0));
    registerLivingEntity(50, "Creeper", HitboxSize.of(0.6F, 1.95F));
    registerLivingEntity(51, "Skeleton", HitboxSize.of(0.6F, 1.95F));
    registerLivingEntity(52, "Spider", HitboxSize.of(1.4F, 0.9F));
    registerLivingEntity(53, "Giant", HitboxSize.of(0.6f * 6f, 1.95f * 6f));
    registerLivingEntity(54, "Zombie", HitboxSize.of(0.6F, 1.95F));
    registerLivingEntity(55, "Slime", HitboxSize.of(0.51000005F, 0.51000005F));
    registerLivingEntity(56, "Ghast", HitboxSize.of(4.0F, 4.0F));
    registerLivingEntity(57, "PigZombie", HitboxSize.of(0.6F, 1.95F));
    registerLivingEntity(58, "Enderman", HitboxSize.of(0.6F, 2.9F));
    registerLivingEntity(59, "CaveSpider", HitboxSize.of(0.7F, 0.5F));
    registerLivingEntity(60, "Silverfish", HitboxSize.of(0.4F, 0.3F));
    registerLivingEntity(61, "Blaze", HitboxSize.of(0.6F, 1.95F));
    registerLivingEntity(62, "LavaSlime", HitboxSize.of(0.51000005F, 0.51000005F));
    registerLivingEntity(63, "EnderDragon", HitboxSize.of(16.0F, 8.0F));
    registerLivingEntity(64, "WitherBoss", HitboxSize.of(0.9F, 3.5F));
    registerLivingEntity(65, "Bat", HitboxSize.of(0.5F, 0.9F));
    registerLivingEntity(66, "Witch", HitboxSize.of(0.6F, 1.95F));
    registerLivingEntity(67, "Endermite", HitboxSize.of(0.4F, 0.3F));
    registerLivingEntity(68, "Guardian", HitboxSize.of(0.85F, 0.85F));
    registerLivingEntity(90, "Pig", HitboxSize.of(0.9F, 0.9F));
    registerLivingEntity(91, "Sheep", HitboxSize.of(0.9F, 1.3F));
    registerLivingEntity(92, "Cow", HitboxSize.of(0.9F, 1.3F));
    registerLivingEntity(93, "Chicken", HitboxSize.of(0.4F, 0.7F));
    registerLivingEntity(94, "Squid", HitboxSize.of(0.95F, 0.95F));
    registerLivingEntity(95, "Wolf", HitboxSize.of(0.6F, 0.8F));
    registerLivingEntity(96, "MushroomCow", HitboxSize.of(0.9F, 1.3F));
    registerLivingEntity(97, "SnowMan", HitboxSize.of(0.7F, 1.9F));
    registerLivingEntity(98, "Ozelot", HitboxSize.of(0.6F, 0.7F));
    registerLivingEntity(99, "VillagerGolem", HitboxSize.of(1.4F, 2.9F));
    registerLivingEntity(100, "Horse", HitboxSize.of(1.4F, 1.6F));
    registerLivingEntity(101, "Rabbit", HitboxSize.of(0.6F, 0.7F));
    registerLivingEntity(105, "Player", HitboxSize.of(0.6F, 1.8F));
    registerLivingEntity(120, "Villager", HitboxSize.of(0.6F, 1.8F));
    registerTileEntity(200, "EnderCrystal", HitboxSize.of(2.0F, 2.0F));
  }

  private static void registerLivingEntity(int identifier, String name, HitboxSize dimensions) {
    entityTypeRegister.put(identifier, new EntityTypeData(name, dimensions, identifier, true, 9));
  }

  private static void registerTileEntity(int identifier, String name, HitboxSize dimensions) {
    entityTypeRegister.put(identifier, new EntityTypeData(name, dimensions, identifier, false, 10));
  }

  @Override
  public EntityTypeData resolveFor(int entityType, boolean isLivingEntity) {
    if (entityType != -1) {
      return entityTypeRegister.get(entityType);
    } else {
      return null;
    }
  }
}
