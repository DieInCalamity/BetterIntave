package de.jpx3.intave.detect.checks.world;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.IntaveCheck;
import de.jpx3.intave.detect.checks.world.placementanalysis.PlacementFacingAnalyzer;
import de.jpx3.intave.detect.checks.world.placementanalysis.PlacementPacketOrderAnalyzer;
import de.jpx3.intave.detect.checks.world.placementanalysis.PlacementSpeedAnalyzer;

public final class PlacementAnalysis extends IntaveCheck {
  private final IntavePlugin plugin;
  public final static String COMMON_FLAG_MESSAGE = "suspicious block-placement";

  public PlacementAnalysis(IntavePlugin plugin) {
    super("PlacementAnalysis", "placementanalysis");
    this.plugin = plugin;
    this.setupSubChecks();
  }

  public void setupSubChecks() {
    appendCheckPart(new PlacementFacingAnalyzer(this));
    appendCheckPart(new PlacementSpeedAnalyzer(this));
    appendCheckPart(new PlacementPacketOrderAnalyzer(this));
  }
}