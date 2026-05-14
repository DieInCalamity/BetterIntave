package de.jpx3.intave.check.other.protocolscanner;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.jpx3.intave.check.CheckPart;
import de.jpx3.intave.check.other.ProtocolScanner;
import de.jpx3.intave.module.Modules;
import de.jpx3.intave.module.linker.packet.PacketId;
import de.jpx3.intave.module.linker.packet.PacketSubscription;
import de.jpx3.intave.module.violation.Violation;
import de.jpx3.intave.user.User;
import org.bukkit.entity.Player;

public class InvalidRelease extends CheckPart<ProtocolScanner> {
    public InvalidRelease(ProtocolScanner parentCheck) {
        super(parentCheck);
    }
    @PacketSubscription(packetsIn = {
            PacketId.Client.BLOCK_DIG
    })
    public void checkValidateRelease(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Player player = event.getPlayer();
        User user = userOf(player);
        EnumWrappers.PlayerDigType digType = packet.getPlayerDigTypes().readSafely(0);
        if (digType == null) return;
        if (user.protocolVersion() < 47) return;
        if (digType == EnumWrappers.PlayerDigType.RELEASE_USE_ITEM) {
            EnumWrappers.Direction face = packet.getDirections().readSafely(0);
            // Vanilla always sends DOWN
            // Fix https://github.com/Raven-APlus/RavenAPlus/blob/master/src/main/java/keystrokesmod/module/impl/movement/noslow/IntaveNoSlow.java
            if (face != EnumWrappers.Direction.DOWN) {
                Violation violation = Violation.builderFor(ProtocolScanner.class)
                        .forPlayer(player).withMessage("sent invalid release").withDetails("face " + face)
                        .withVL(3)
                        .build();
                Modules.violationProcessor().processViolation(violation);
            }
        }
    }
}
