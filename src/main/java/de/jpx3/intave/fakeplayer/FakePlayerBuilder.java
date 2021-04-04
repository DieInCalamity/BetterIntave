package de.jpx3.intave.fakeplayer;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.base.Preconditions;
import de.jpx3.intave.fakeplayer.movement.types.Movement;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class FakePlayerBuilder {
  private Player parentPlayer = null;
  private int entityID = -1;
  private String name;
  private UUID uuid = null;
  private String tabListPrefix = "";
  private String prefix = "";
  private Movement movement = null;
  private int timeout = 10_000;
  private boolean invisible = false;
  private boolean visibleInTablist = true;
  private boolean equipArmor = true;
  private boolean equipHeldItem = true;
  private FakePlayerAttackSubscriber fakePlayerAttackSubscriber = () -> {};

  private FakePlayerBuilder() {
  }

  public static FakePlayerBuilder createBuilder() {
    return new FakePlayerBuilder();
  }

  public FakePlayerBuilder setParentPlayer(Player player) {
    this.parentPlayer = player;
    return this;
  }

  public FakePlayerBuilder setUUID(UUID uuid) {
    this.uuid = uuid;
    return this;
  }

  public FakePlayerBuilder setEntityID(int entityID) {
    this.entityID = entityID;
    return this;
  }

  public FakePlayerBuilder setName(String name) {
    this.name = name;
    return this;
  }

  public FakePlayerBuilder setTabListPrefix(String tabListPrefix) {
    this.tabListPrefix = tabListPrefix;
    return this;
  }

  public FakePlayerBuilder setPrefix(String prefix) {
    this.prefix = prefix;
    return this;
  }

  public FakePlayerBuilder setMovement(Movement movement) {
    this.movement = movement;
    return this;
  }

  public FakePlayerBuilder setTimeout(int timeout) {
    this.timeout = timeout;
    return this;
  }

  public FakePlayerBuilder setInvisible(boolean invisible) {
    this.invisible = invisible;
    return this;
  }

  public FakePlayerBuilder setInTablist(boolean visibleInTablist) {
    this.visibleInTablist = visibleInTablist;
    return this;
  }

  public FakePlayerBuilder setEquipArmor(boolean equipArmor) {
    this.equipArmor = equipArmor;
    return this;
  }

  public FakePlayerBuilder setEquipHeldItem(boolean equipHeldItem) {
    this.equipHeldItem = equipHeldItem;
    return this;
  }

  public FakePlayerBuilder setAttackSubscriber(FakePlayerAttackSubscriber subscriber) {
    this.fakePlayerAttackSubscriber = subscriber;
    return this;
  }

  public FakePlayer build() {
    Preconditions.checkNotNull(this.parentPlayer);
    Preconditions.checkNotNull(this.uuid);
    Preconditions.checkState(this.entityID >= 0, "EntityId can not be negative!");
    Preconditions.checkNotNull(this.name);
    Preconditions.checkNotNull(this.movement);
    int nameLength = this.name.length();
    Preconditions.checkState(nameLength <= 16, "Name is too long (" + nameLength + ")");
    return new FakePlayer(
      this.movement,
      this.parentPlayer,
      new WrappedGameProfile(this.uuid, this.name),
      this.tabListPrefix,
      this.prefix,
      this.entityID,
      this.timeout,
      this.invisible,
      this.visibleInTablist,
      this.equipArmor,
      this.equipHeldItem,
      this.fakePlayerAttackSubscriber
    );
  }
}
