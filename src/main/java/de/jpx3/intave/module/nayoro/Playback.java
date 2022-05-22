package de.jpx3.intave.module.nayoro;

import de.jpx3.intave.module.Modules;
import de.jpx3.intave.module.nayoro.detection.PrintStreamDetectionSubscription;
import de.jpx3.intave.module.nayoro.event.EntityMoveEvent;
import de.jpx3.intave.module.nayoro.event.Event;
import de.jpx3.intave.module.nayoro.event.EventRegistry;
import de.jpx3.intave.module.nayoro.event.PropertiesEvent;
import de.jpx3.intave.shade.Position;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

abstract class Playback extends SinkEnvironment {
  private final DataInputStream dataInputStream;
  private final Environment environment;
  private final Map<String, Boolean> properties = new HashMap<>();
  private final PlaybackPlayerContainer playbackPlayer = new PlaybackPlayerContainer(new PrintStreamDetectionSubscription(System.out));
  private final Map<Integer, Position> entities = new HashMap<>();
  private final Map<Integer, Boolean> inSight = new HashMap<>();
  private final Set<Integer> entityIds = new HashSet<>();

  public Playback(DataInputStream stream, Environment environment) {
    this.dataInputStream = stream;
    this.environment = environment;
    this.playbackPlayer.setEnvironment(environment);
  }

  public abstract void start();
  public abstract void stop();

  protected Event nextEvent() {
    try {
      long offset = dataInputStream.readInt();
      int packetId = dataInputStream.readByte();
      Event event = EventRegistry.eventOf(packetId);
      event.deserialize(environment, dataInputStream);
      event.withOffset(offset);
      return event;
    } catch (IOException e) {
      return null;
    }
  }

  @Override
  public PlayerContainer mainPlayer() {
    return playbackPlayer;
  }

  @Override
  public void visit(PropertiesEvent event) {
    properties.putAll(event.properties());
  }

  @Override
  public void visit(EntityMoveEvent event) {
    int entityId = event.entityId();
    entityIds.add(entityId);
    entities.compute(entityId, (id, position) -> {
      if (position == null) {
        position = new Position();
      }
      if (event.applyX()) {
        position.setX(event.x());
      }
      if (event.applyY()) {
        position.setY(event.y());
      }
      if (event.applyZ()) {
        position.setZ(event.z());
      }
      return position;
    });
    inSight.compute(entityId, (id, last) -> event.inSight());
    visitAny(event);
  }

  @Override
  public void visitAny(Event event) {
    playbackPlayer.visitSelect(event);
    Modules.linker().nayoroEvents().fireEvent(playbackPlayer, event);
  }

  @Override
  public boolean property(String name) {
    return properties.getOrDefault(name, false);
  }

  @Override
  public Set<Integer> entities() {
    return entityIds;
  }

  @Override
  public Position positionOf(int entity) {
    if (entity == mainPlayer().id()) {
      return mainPlayer().position();
    } else {
      return entities.get(entity);
    }
  }

  @Override
  public boolean inSight(int entity) {
    return inSight.getOrDefault(entity, false);
  }

  @Override
  public Map<String, Boolean> properties() {
    return properties;
  }
}
