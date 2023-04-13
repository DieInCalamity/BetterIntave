package de.jpx3.intave.block.access;

import de.jpx3.intave.world.WorldHeight;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FakeFallbackBlock implements Block {
  private final Reference<World> worldReference;

  public FakeFallbackBlock(World world) {
    this.worldReference = new WeakReference<>(world);
  }

  @Override
  public byte getData() {
    return 0;
  }

  @Override
  public Block getRelative(int i, int i1, int i2) {
    return this;
  }

  @Override
  public Block getRelative(BlockFace blockFace) {
    return this;
  }

  @Override
  public Block getRelative(BlockFace blockFace, int i) {
    return this;
  }

  @Override
  public Material getType() {
    return Material.AIR;
  }

  @Override
  public int getTypeId() {
    return 0;
  }

  @Override
  public byte getLightLevel() {
    return 0;
  }

  @Override
  public byte getLightFromSky() {
    return 0;
  }

  @Override
  public byte getLightFromBlocks() {
    return 0;
  }

  @Override
  public World getWorld() {
    return worldReference.get();
  }

  @Override
  public int getX() {
    return getWorld().getSpawnLocation().getBlockX();
  }

  @Override
  public int getY() {
    return WorldHeight.LOWER_WORLD_LIMIT - 1;
  }

  @Override
  public int getZ() {
    return getWorld().getSpawnLocation().getBlockZ();
  }

  @Override
  public Location getLocation() {
    return new Location(
      getWorld(),
      getX(),
      getY(),
      getZ()
    );
  }

  @Override
  public Location getLocation(Location location) {
    return location;
  }

  @Override
  public Chunk getChunk() {
    return null;
  }

  @Override
  public void setData(byte b) {

  }

  @Override
  public void setData(byte b, boolean b1) {

  }

  @Override
  public void setType(Material material) {

  }

  @Override
  public void setType(Material material, boolean b) {

  }

  @Override
  public boolean setTypeId(int i) {
    return false;
  }

  @Override
  public boolean setTypeId(int i, boolean b) {
    return false;
  }

  @Override
  public boolean setTypeIdAndData(int i, byte b, boolean b1) {
    return false;
  }

  @Override
  public BlockFace getFace(Block block) {
    return BlockFace.SELF;
  }

  @Override
  public BlockState getState() {
    return null;
  }

  @Override
  public Biome getBiome() {
    return Biome.values()[0];
  }

  @Override
  public void setBiome(Biome biome) {

  }

  @Override
  public boolean isBlockPowered() {
    return false;
  }

  @Override
  public boolean isBlockIndirectlyPowered() {
    return false;
  }

  @Override
  public boolean isBlockFacePowered(BlockFace blockFace) {
    return false;
  }

  @Override
  public boolean isBlockFaceIndirectlyPowered(BlockFace blockFace) {
    return false;
  }

  @Override
  public int getBlockPower(BlockFace blockFace) {
    return 0;
  }

  @Override
  public int getBlockPower() {
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public boolean isLiquid() {
    return false;
  }

  @Override
  public double getTemperature() {
    return 0;
  }

  @Override
  public double getHumidity() {
    return 0;
  }

  @Override
  public PistonMoveReaction getPistonMoveReaction() {
    return PistonMoveReaction.BLOCK;
  }

  @Override
  public boolean breakNaturally() {
    return false;
  }

  @Override
  public boolean breakNaturally(ItemStack itemStack) {
    return false;
  }

  @Override
  public Collection<ItemStack> getDrops() {
    return Collections.emptyList();
  }

  @Override
  public Collection<ItemStack> getDrops(ItemStack itemStack) {
    return Collections.emptyList();
  }

  @Override
  public void setMetadata(String s, MetadataValue metadataValue) {

  }

  @Override
  public List<MetadataValue> getMetadata(String s) {
    return null;
  }

  @Override
  public boolean hasMetadata(String s) {
    return false;
  }

  @Override
  public void removeMetadata(String s, Plugin plugin) {

  }
}
