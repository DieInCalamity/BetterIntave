package de.jpx3.intave.check.combat.heuristics.detect;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.check.MetaCheckPart;
import de.jpx3.intave.check.combat.Heuristics;
import de.jpx3.intave.math.MathHelper;
import de.jpx3.intave.module.linker.packet.ListenerPriority;
import de.jpx3.intave.module.linker.packet.PacketSubscription;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.CheckCustomMetadata;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.jpx3.intave.module.linker.packet.PacketId.Client.*;

public final class PacketDependenciesHeuristic extends MetaCheckPart<Heuristics, PacketDependenciesHeuristic.PacketDependentHeuristicMeta> {
  private final IntavePlugin plugin;

  /*
  What the check does:

  The check should measure the time diffrences between multiple packets which are send by the client (measured in ticks / movement packets)
  The time diffrences betweeen theses packets then should be put into a standard deviation method which should give dependencies between theses packets
  when the returned value is pretty low.

  Some packets could false flagg which should then can be blacklisted manually.

  Example:
  Current Tick        PacketType
  1                   HELD_ITEM_SLOT ----|
  2                                      |-> tick diffrence is 2
  3                   BLOCK_PLACE -------|
  4                   HELD_ITEM_SLOT -------|
  5                                         |-> tick diffrence is 2
  6                   BLOCK_PLACE ----------|
  7                   HELD_ITEM_SLOT ----|
  8                                      |-> tick diffrence is 2
  9                   BLOCK_PLACE -------|

  calculateStandardDeviation(2, 2, 2) which should be near 0
  */
  public PacketDependenciesHeuristic(Heuristics parentCheck) {
    super(parentCheck, PacketDependenciesHeuristic.PacketDependentHeuristicMeta.class);
    this.plugin = IntavePlugin.singletonInstance();
  }

  private static int ticksToSave = 200;

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsIn = {
      POSITION, POSITION_LOOK, FLYING, LOOK
    }
  )
  public void receiveMovement(PacketEvent event) {
    Player player = event.getPlayer();
    User user = userOf(player);
    PacketDependentHeuristicMeta meta = metaOf(user);

    HashMap<Integer, SaveMultipleTicks> multipleDependencies = new HashMap<>();
    for (int firstTick = meta.currentTick; firstTick > meta.currentTick - ticksToSave; firstTick--) {
      ArrayList<PacketType> firstPacketTypes = meta.packetTypeList.get(firstTick);
      if(firstPacketTypes != null) {
        HashMap<Integer, SaveOneTick> dependencies = new HashMap<>();
        /*
        Speicher pro PacketType ein anderes packetType was davor gesendet wurde in der abhängigkeit mit dem ersten packetType ab.
         */
        for (int secondTick = firstTick - 1; secondTick > meta.currentTick - ticksToSave; secondTick--) {
          ArrayList<PacketType> secondPacketTypes = meta.packetTypeList.get(secondTick);
          if(secondPacketTypes != null) {

            for (PacketType firstPacketType : firstPacketTypes) {
              for (PacketType secondPacketType : secondPacketTypes) {
                int id = packetTypesToInt(firstPacketType, secondPacketType);
                if(!dependencies.containsKey(id)) {
                  int tickDiffrence = firstTick - secondTick;
                  if(tickDiffrence < 20) {
                    SaveOneTick save = new SaveOneTick(firstPacketType, secondPacketType, tickDiffrence);
                    dependencies.put(id, save);
                  }
                }
              }
            }
          }
        }

        for (Map.Entry<Integer, SaveOneTick> entry : dependencies.entrySet()) {
          int id = entry.getKey();
          SaveOneTick save = entry.getValue();

          SaveMultipleTicks saveMultipleTicks = multipleDependencies.get(id);
          if(saveMultipleTicks != null) {
            saveMultipleTicks.ticks.add(save.tickDiffrence);
          } else {
            saveMultipleTicks = new SaveMultipleTicks(save.firstPacketType, save.secondPacketType);
            saveMultipleTicks.ticks.add(save.tickDiffrence);
            multipleDependencies.put(id, saveMultipleTicks);
          }
        }
      }
    }

    for (SaveMultipleTicks value : multipleDependencies.values()) {
      double standardDeviation = standardDeviation(value.ticks);
      String standardDeviationString = MathHelper.formatDouble(standardDeviation, 4);
      player.sendMessage("std: " + standardDeviationString
        + " " + value.firstPacketType.name().toLowerCase()
        + " " + value.secondPacketType.name().toLowerCase()
        + " " + value.ticks.size());
    }
    prepareNextTick(meta);
  }

  private double standardDeviation(List<? extends Number> sd) {
    double sum = 0, newSum = 0;
    for (Number v : sd) {
      sum = sum + v.doubleValue();
    }
    double mean = sum / sd.size();
    for (Number v : sd) {
      newSum = newSum + (v.doubleValue() - mean) * (v.doubleValue() - mean);
    }
    return Math.sqrt(newSum / sd.size());
  }

  private int packetTypesToInt(PacketType first, PacketType second) {
    return first.getCurrentId() + second.getCurrentId() * 10000;
  }

  private void addTickToPacketTypeList(PacketDependentHeuristicMeta meta, PacketType packetType) {
    ArrayList<PacketType> packetTypeArrayList = meta.packetTypeList.get(meta.currentTick);
    if(packetTypeArrayList == null) {
      packetTypeArrayList = new ArrayList<>();
      meta.packetTypeList.put(meta.currentTick, packetTypeArrayList);
    }
    packetTypeArrayList.add(packetType);
  }

  private void prepareNextTick(PacketDependentHeuristicMeta meta) {
    meta.currentTick++;

    if(meta.currentTick > ticksToSave) {
      meta.packetTypeList.remove(meta.currentTick - ticksToSave);
    }
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsIn = {
      ENTITY_ACTION,
      USE_ENTITY,
      ARM_ANIMATION,
      BLOCK_DIG,
      BLOCK_PLACE,
      HELD_ITEM_SLOT
    }
  )
  public void receivePackets(PacketEvent event) {
    PacketDependentHeuristicMeta meta = metaOf(userOf(event.getPlayer()));
    addTickToPacketTypeList(meta, event.getPacketType());
  }

  public final static class PacketDependentHeuristicMeta extends CheckCustomMetadata {

    int currentTick;
    HashMap<Integer, ArrayList<PacketType>> packetTypeList = new HashMap<>();
  }
}

class SaveOneTick {
  PacketType firstPacketType;
  PacketType secondPacketType;
  int tickDiffrence;
  public SaveOneTick(PacketType firstPacketType, PacketType secondPacketType, int tickDiffrence) {
    this.firstPacketType = firstPacketType;
    this.secondPacketType = secondPacketType;
    this.tickDiffrence = tickDiffrence;
  }
}
class SaveMultipleTicks {
  PacketType firstPacketType;
  PacketType secondPacketType;
  List<Integer> ticks = new ArrayList<>();
  public SaveMultipleTicks(PacketType firstPacketType, PacketType secondPacketType) {
    this.firstPacketType = firstPacketType;
    this.secondPacketType = secondPacketType;
  }
}
