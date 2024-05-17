package de.jpx3.intave.module.nayoro.event;

import de.jpx3.intave.module.nayoro.Environment;
import de.jpx3.intave.module.nayoro.event.sink.EventSink;
import org.bukkit.inventory.ItemStack;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class WindowItemsEvent extends Event {
  private int windowId;
  private int count;

  private final List<InventoryItem> items = new ArrayList<>();

  public WindowItemsEvent() {
  }

  public WindowItemsEvent(
    int windowId, int count,
    Map<Integer, ItemStack> items,
    Map<Integer, Double> qualities
  ) {
    this.windowId = windowId;
    this.count = count;
    for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
      int slot = entry.getKey();
      ItemStack item = entry.getValue();
      double quality = qualities.getOrDefault(slot, 0.0);
      this.items.add(new InventoryItem(slot, item.getType().name(), item.getAmount(), quality));
    }
  }

  @Override
  public void serialize(Environment environment, DataOutput out) throws IOException {
    out.writeInt(windowId);
    out.writeInt(count);
    out.writeInt(items.size());
    for (InventoryItem item : items) {
      item.serialize(out);
    }
  }

  @Override
  public void deserialize(Environment environment, DataInput in) throws IOException {
    windowId = in.readInt();
    count = in.readInt();
    if (count > 1024) {
      throw new IOException("Too many items: " + count);
    }
    items.clear();
    int size = in.readInt();
    if (size > 1024) {
      throw new IOException("Too many items: " + size);
    }
    for (int i = 0; i < size; i++) {
      items.add(InventoryItem.deserialize(in));
    }
  }

  @Override
  public void accept(EventSink sink) {
    sink.visit(this);
  }

  public static class InventoryItem {
    private final int slot;
    private final String type;
    private final int amount;
    private final double quality;

    public InventoryItem(int slot, String type, int amount, double quality) {
      this.slot = slot;
      this.type = type;
      this.amount = amount;
      this.quality = quality;
    }

    public int slot() {
      return slot;
    }

    public String type() {
      return type;
    }

    public int amount() {
      return amount;
    }

    public double quality() {
      return quality;
    }

    public void serialize(DataOutput out) throws IOException {
      out.writeInt(slot);
      out.writeUTF(type);
      out.writeInt(amount);
      out.writeDouble(quality);
    }

    public static InventoryItem deserialize(DataInput in) throws IOException {
      return new InventoryItem(in.readInt(), in.readUTF(), in.readInt(), in.readDouble());
    }
  }

  public int windowId() {
    return windowId;
  }

  public int count() {
    return count;
  }

  public List<InventoryItem> items() {
    return items;
  }

  public InventoryItem item(int index) {
    return items.get(index);
  }

  public void addItem(InventoryItem item) {
    items.add(item);
  }

  public void removeItem(int index) {
    items.remove(index);
  }

  public void clearItems() {
    items.clear();
  }

  public static WindowItemsEvent create(
    int windowId, int slots, Map<Integer, ItemStack> items, Map<Integer, Double> qualities
  ) {
    return new WindowItemsEvent(windowId, slots, items, qualities);
  }
}
