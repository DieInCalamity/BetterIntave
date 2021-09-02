package de.jpx3.intave.check.world;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.check.Check;
import de.jpx3.intave.check.world.breakspeedlimiter.CompletionDurationCheck;
import de.jpx3.intave.check.world.breakspeedlimiter.RestartCheck;

public final class BreakSpeedLimiter extends Check {
  public BreakSpeedLimiter(IntavePlugin plugin) {
    super("BreakSpeedLimiter", "breakspeedlimiter");
    setupParts();
  }

  public void setupParts() {
    appendCheckPart(new CompletionDurationCheck(this));
    appendCheckPart(new RestartCheck(this));
  }
}