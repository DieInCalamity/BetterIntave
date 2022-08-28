package de.jpx3.intave.user.meta;

import de.jpx3.intave.annotate.Relocate;
import de.jpx3.intave.user.User;
import org.bukkit.entity.Player;

@Relocate
public final class MetadataBundle {
    private final ViolationMetadata violation;
    private final MovementMetadata movement;
    private final AbilityMetadata ability;
    private final EffectMetadata potion;
    private final ProtocolMetadata protocol;
    private final ConnectionMetadata connection;
    private final InventoryMetadata inventory;
    private final AttackMetadata attack;
    private final PunishmentMetadata punishment;
    private final PhysicsMetadata physics;

    public MetadataBundle(Player player, User user) {
        this.violation = new ViolationMetadata();
        this.protocol = new ProtocolMetadata(player, user);
        this.ability = new AbilityMetadata(player);
        this.potion = new EffectMetadata(player);
        this.inventory = new InventoryMetadata(player);
        this.connection = new ConnectionMetadata(player);
        this.movement = new MovementMetadata(player, user);
        this.attack = new AttackMetadata(player);
        this.punishment = new PunishmentMetadata(player);
        this.physics = new PhysicsMetadata(player);
    }

    public ViolationMetadata violationLevel() {
        return violation;
    }

    public MovementMetadata movement() {
        return movement;
    }

    public InventoryMetadata inventory() {
        return inventory;
    }

    public AbilityMetadata abilities() {
        return ability;
    }

    public EffectMetadata potions() {
        return potion;
    }

    public ConnectionMetadata connection() {
        return connection;
    }

    public ProtocolMetadata protocol() {
        return protocol;
    }

    public AttackMetadata attack() {
        return attack;
    }

    public PunishmentMetadata punishment() {
        return punishment;
    }

    public PhysicsMetadata physics() {
        return physics;
    }

    public void setup() {
        movement.setup();
        physics.setup();
    }
}
