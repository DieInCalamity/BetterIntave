package de.jpx3.intave.check.world;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.annotate.Native;
import de.jpx3.intave.check.Check;
import de.jpx3.intave.check.world.placementanalysis.*;
import de.jpx3.intave.module.mitigate.AttackNerfStrategy;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.ProtocolMetadata;

import static de.jpx3.intave.IntaveControl.DISABLE_LICENSE_CHECK;
import static de.jpx3.intave.IntaveControl.GOMME_MODE;

public final class PlacementAnalysis extends Check {
  private final IntavePlugin plugin;
  public static final String COMMON_FLAG_MESSAGE = "suspicious block-placement";

  public PlacementAnalysis(IntavePlugin plugin) {
    super("PlacementAnalysis", "placementanalysis");
    this.plugin = plugin;
    this.setupSubChecks();
  }

  @Native
  public void setupSubChecks() {
    boolean useTimings = configuration().settings().boolBy("check-timings", configuration().settings().boolBy("check_timings", true));

    if (DISABLE_LICENSE_CHECK && !GOMME_MODE) {
      appendCheckPart(new Constraint(this));
      appendCheckPart(new SmartSpeed(this));
    }

    boolean enterprise = (ProtocolMetadata.VERSION_DETAILS & 0x200) != 0;
    boolean partner = (ProtocolMetadata.VERSION_DETAILS & 0x100) != 0;
    try {
      if (enterprise || partner || DISABLE_LICENSE_CHECK) {
        if (useTimings) {
          appendCheckPart(new Speed(this));
          appendCheckPart(new Sneak(this));
        }
        appendCheckPart(new Snap(this));
        appendCheckPart(new SharpRotation(this));
        appendCheckPart(new BlockRotation(this));
        appendCheckPart(new SneakAndPlace(this));
      }
    } catch (Exception | Error e) {
      // classes might be missing
    }
    appendCheckPart(new RotationSpeed(this));
    appendCheckPart(new PacketOrder(this));
    appendCheckPart(new Facing(this));
    appendCheckPart(new RoundedRotation(this));
  }

  public void applyPlacementAnalysisDamageCancel(User user, String checkId) {
    user.nerf(AttackNerfStrategy.CANCEL_FIRST_HIT, checkId);
    user.nerf(AttackNerfStrategy.DMG_LIGHT, checkId);
  }
}