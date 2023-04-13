package de.jpx3.intave.test;

import com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public final class MockEmptyInventory implements PlayerInventory {
  @Override
  public ItemStack[] getArmorContents() {
    return new ItemStack[4];
  }

  @Override
  public ItemStack[] getExtraContents() {
    return new ItemStack[4];
  }

  @Override
  public ItemStack getHelmet() {
    return null;
  }

  @Override
  public ItemStack getChestplate() {
    return null;
  }

  @Override
  public ItemStack getLeggings() {
    return null;
  }

  @Override
  public ItemStack getBoots() {
    return null;
  }

  @Override
  public int getSize() {
    return 36;
  }

  @Override
  public int getMaxStackSize() {
    return 64;
  }

  @Override
  public void setMaxStackSize(int i) {

  }

  @Override
  public String getName() {
    return "MockEmptyInventory";
  }

  @Override
  public ItemStack getItem(int i) {
    return null;
  }

  @Override
  public void setItem(int i, ItemStack itemStack) {

  }

  @Override
  public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks) throws IllegalArgumentException {
    return Maps.newHashMap();
  }

  @Override
  public HashMap<Integer, ItemStack> removeItem(ItemStack... itemStacks) throws IllegalArgumentException {
    return Maps.newHashMap();
  }

  @Override
  public ItemStack[] getContents() {
    return new ItemStack[36 + 4];
  }

  @Override
  public void setContents(ItemStack[] itemStacks) throws IllegalArgumentException {

  }

  @Override
  public ItemStack[] getStorageContents() {
    return new ItemStack[36];
  }

  @Override
  public void setStorageContents(ItemStack[] itemStacks) throws IllegalArgumentException {

  }

  @Override
  public boolean contains(int i) {
    return false;
  }

  @Override
  public boolean contains(Material material) throws IllegalArgumentException {
    return false;
  }

  @Override
  public boolean contains(ItemStack itemStack) {
    return false;
  }

  @Override
  public boolean contains(int i, int i1) {
    return false;
  }

  @Override
  public boolean contains(Material material, int i) throws IllegalArgumentException {
    return false;
  }

  @Override
  public boolean contains(ItemStack itemStack, int i) {
    return false;
  }

  @Override
  public boolean containsAtLeast(ItemStack itemStack, int i) {
    return false;
  }

  @Override
  public HashMap<Integer, ? extends ItemStack> all(int i) {
    return Maps.newHashMap();
  }

  @Override
  public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException {
    return Maps.newHashMap();
  }

  @Override
  public HashMap<Integer, ? extends ItemStack> all(ItemStack itemStack) {
    return Maps.newHashMap();
  }

  @Override
  public int first(int i) {
    return -1;
  }

  @Override
  public int first(Material material) throws IllegalArgumentException {
    return -1;
  }

  @Override
  public int first(ItemStack itemStack) {
    return -1;
  }

  @Override
  public int firstEmpty() {
    return -1;
  }

  @Override
  public void remove(int i) {

  }

  @Override
  public void remove(Material material) throws IllegalArgumentException {

  }

  @Override
  public void remove(ItemStack itemStack) {

  }

  @Override
  public void clear(int i) {

  }

  @Override
  public void clear() {

  }

  @Override
  public List<HumanEntity> getViewers() {
    return Collections.emptyList();
  }

  @Override
  public String getTitle() {
    return "MockEmptyInventory";
  }

  @Override
  public InventoryType getType() {
    return InventoryType.PLAYER;
  }

  @Override
  public void setArmorContents(ItemStack[] itemStacks) {

  }

  @Override
  public void setExtraContents(ItemStack[] itemStacks) {

  }

  @Override
  public void setHelmet(ItemStack itemStack) {

  }

  @Override
  public void setChestplate(ItemStack itemStack) {

  }

  @Override
  public void setLeggings(ItemStack itemStack) {

  }

  @Override
  public void setBoots(ItemStack itemStack) {

  }

  @Override
  public ItemStack getItemInMainHand() {
    return null;
  }

  @Override
  public void setItemInMainHand(ItemStack itemStack) {

  }

  @Override
  public ItemStack getItemInOffHand() {
    return null;
  }

  @Override
  public void setItemInOffHand(ItemStack itemStack) {

  }

  @Override
  public ItemStack getItemInHand() {
    return null;
  }

  @Override
  public void setItemInHand(ItemStack itemStack) {

  }

  @Override
  public int getHeldItemSlot() {
    return 0;
  }

  @Override
  public void setHeldItemSlot(int i) {

  }

  @Override
  public int clear(int i, int i1) {
    return 0;
  }

  @Override
  public HumanEntity getHolder() {
    return null;
  }

  @Override
  public ListIterator<ItemStack> iterator() {
    return null;
  }

  @Override
  public ListIterator<ItemStack> iterator(int i) {
    return null;
  }

  @Override
  public Location getLocation() {
    return null;
  }
}
