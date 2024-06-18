package de.jpx3.intave.user.storage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerStorage implements Storage {
  private static final int STORAGE_VERSION = 5;

  private final Map<Class<? extends Storage>, Storage> subStorages = new ConcurrentHashMap<>();
  private final Map<Integer, Storage> storageList = new HashMap<>();
  private final UUID id;
  private long creation;

  PlayerStorage(UUID id) {
    this.id = id;
    this.creation = System.currentTimeMillis();
  }

  @Override
  public void writeTo(ByteArrayDataOutput output) {
    output.writeByte(STORAGE_VERSION);
    output.writeLong(id.getMostSignificantBits());
    output.writeLong(id.getLeastSignificantBits());
    output.writeLong(creation);
    for (Storage child : storageList.values()) {
      output.writeByte(child.id());
      output.writeByte(child.version());
      child.writeTo(output);
    }
    output.writeByte(-1);
    output.writeByte(-1);
  }

  private static final String INVALID_ID_ERROR = "Invalid entry fetched, expected %s but received id %s";

  @Override
  public void readFrom(ByteArrayDataInput input) {
    try {
      int version = input.readByte();
      if (version != STORAGE_VERSION) {
        return;
      }
    } catch (Exception exception) {
      return;
    }
    long mostSigBits = input.readLong();
    long leastSigBits = input.readLong();
    UUID id = new UUID(mostSigBits, leastSigBits);
    if (!id.equals(this.id)) {
      String errorMessage = String.format(INVALID_ID_ERROR, this.id, id);
      throw new IllegalStateException(errorMessage);
    }
    creation = input.readLong();
    do {
      int storageId = input.readByte();
      int storageVersion = input.readByte();
      if (storageId == -1 && storageVersion == -1) {
        break;
      }
      Storage storage = storageList.get(storageId);
      if (storage != null && storage.version() == storageVersion) {
        storage.readFrom(input);
      }
    } while (true);
  }

  <T extends Storage> void append(Class<T> storageClass) {
    T value = instanceOf(storageClass);
    if (storageList.get(value.id()) != null) {
      throw new IllegalStateException("Storage with id " + value.id() + " already exists");
    }
    subStorages.put(storageClass, value);
    storageList.put(value.id(), value);
  }

  public <T extends Storage> T storageOf(Class<T> storageClass) {
    //noinspection unchecked
    return (T) subStorages.get(storageClass);
  }

  private <T> T instanceOf(Class<T> tClass) {
    try {
      return tClass.newInstance();
    } catch (Exception exception) {
      throw new IllegalStateException("Failed to create instance of " + tClass.getName(), exception);
    }
  }

  public int id() {
    throw new UnsupportedOperationException("No id here");
  }

  @Override
  public int version() {
    return STORAGE_VERSION;
  }

  @Override
  public boolean sameContentsAs(Storage other) {
    if (!(other instanceof PlayerStorage)) {
      return false;
    }
    PlayerStorage storage = (PlayerStorage) other;
    return storage.id.equals(id) && storage.creation == creation && storage.sameContentsAs(this);
  }

  public boolean sameContentsAs(PlayerStorage storage) {
    for (Storage child : storageList.values()) {
      Storage other = storage.storageList.get(child.id());
      if (other == null || !child.sameContentsAs(other)) {
        return false;
      }
    }
    return true;
  }
}
