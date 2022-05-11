package de.jpx3.intave.module.tracker.player;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.jpx3.intave.adapter.MinecraftVersions;
import de.jpx3.intave.module.Module;
import de.jpx3.intave.module.Modules;
import de.jpx3.intave.module.linker.packet.ListenerPriority;
import de.jpx3.intave.module.linker.packet.PacketSubscription;
import de.jpx3.intave.packet.reader.EntityEffectReader;
import de.jpx3.intave.packet.reader.PacketReaders;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import de.jpx3.intave.user.meta.EffectMetadata;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import static de.jpx3.intave.module.linker.packet.PacketId.Server.ENTITY_EFFECT;
import static de.jpx3.intave.module.linker.packet.PacketId.Server.REMOVE_ENTITY_EFFECT;

public final class EffectTracker extends Module {
  public static final int POTION_EFFECT_SPEED = 1;
  public static final int POTION_EFFECT_SLOWNESS = 2;
  public static final int POTION_EFFECT_JUMP_BOOST = 8;

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsOut = {
      REMOVE_ENTITY_EFFECT
    }
  )
  public void sentRemoveEffect(PacketEvent event) {
    Player player = event.getPlayer();
    PacketContainer packet = event.getPacket();
    int entityId = packet.getIntegers().read(0);
    if (entityId != player.getEntityId()) {
      return;
    }
    PotionEffectType potionEffectType = effectIdOf(packet);
    Modules.feedback().synchronize(player, potionEffectType, this::receiveEffectRemoval);
  }

  private final boolean EFFECT_ACCESS = MinecraftVersions.VER1_9_0.atOrAbove();

  private PotionEffectType effectIdOf(PacketContainer packet) {
    if (EFFECT_ACCESS) {
      return packet.getEffectTypes().read(0);
    } else {
      return PotionEffectType.getById(packet.getIntegers().read(1));
    }
  }

  @PacketSubscription(
    priority = ListenerPriority.HIGH,
    packetsOut = {
      ENTITY_EFFECT
    }
  )
  public void sentEffect(PacketEvent event) {
    Player player = event.getPlayer();
    PacketContainer packet = event.getPacket();
    EntityEffectReader reader = PacketReaders.readerOf(packet);
    int entityId = reader.entityId();
    if (entityId != player.getEntityId()) {
      reader.release();
      return;
    }
    PotionEffectOutput effectOutput = new PotionEffectOutput(
      reader.effectType(),
      reader.effectAmplifier(),
      reader.effectDuration()
    );
    reader.release();
    Modules.feedback().synchronize(player, effectOutput, this::receiveEffect);
  }

  private void receiveEffectRemoval(Player player, PotionEffectType potionEffectType) {
    User user = UserRepository.userOf(player);
    EffectMetadata potionData = user.meta().potions();
    if (potionEffectType.equals(PotionEffectType.SPEED)) {
      potionData.potionEffectSpeedAmplifier(0);
      potionData.potionEffectSpeedDuration = 0;
    } else if (potionEffectType.equals(PotionEffectType.SLOW)) {
      potionData.potionEffectSlownessAmplifier(0);
      potionData.potionEffectSlownessDuration = 0;
    } else if (potionEffectType.equals(PotionEffectType.JUMP)) {
      potionData.potionEffectJumpAmplifier(0);
      potionData.potionEffectJumpDuration = 0;
    }
  }

  private void receiveEffect(Player player, PotionEffectOutput effectOutput) {
    User user = UserRepository.userOf(player);
    EffectMetadata potionData = user.meta().potions();

    int effectAmplifier = effectOutput.potionEffectAmplifier;
    int effectDuration = effectOutput.potionEffectDuration;

    switch (effectOutput.potionEffectType) {
      case POTION_EFFECT_SPEED: {
        potionData.potionEffectSpeedAmplifier(effectAmplifier + 1);
        potionData.potionEffectSpeedDuration = effectDuration - 1;
        break;
      }
      case POTION_EFFECT_SLOWNESS: {
        potionData.potionEffectSlownessAmplifier(effectAmplifier + 1);
        potionData.potionEffectSlownessDuration = effectDuration - 1;
        break;
      }
      case POTION_EFFECT_JUMP_BOOST: {
        potionData.potionEffectJumpAmplifier(effectAmplifier);
        potionData.potionEffectJumpDuration = effectDuration - 1;
        break;
      }
    }
  }

  private static class PotionEffectOutput {
    private final int potionEffectType;
    private final int potionEffectAmplifier;
    private final int potionEffectDuration;

    public PotionEffectOutput(
      int potionEffectType,
      int potionEffectAmplifier,
      int potionEffectDuration
    ) {
      this.potionEffectType = potionEffectType;
      this.potionEffectAmplifier = potionEffectAmplifier;
      this.potionEffectDuration = potionEffectDuration;
    }
  }
}