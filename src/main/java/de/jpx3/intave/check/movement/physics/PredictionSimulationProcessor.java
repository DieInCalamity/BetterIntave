package de.jpx3.intave.check.movement.physics;

import de.jpx3.intave.annotate.Relocate;
import de.jpx3.intave.annotate.refactoring.IdoNotBelongHere;
import de.jpx3.intave.diagnostic.IterativeStudy;
import de.jpx3.intave.diagnostic.KeyPressStudy;
import de.jpx3.intave.diagnostic.timings.Timings;
import de.jpx3.intave.math.Hypot;
import de.jpx3.intave.math.MathHelper;
import de.jpx3.intave.module.dispatch.AttackDispatcher;
import de.jpx3.intave.player.collider.complex.ComplexColliderSimulationResult;
import de.jpx3.intave.shade.Motion;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.meta.InventoryMetadata;
import de.jpx3.intave.user.meta.MetadataBundle;
import de.jpx3.intave.user.meta.MovementMetadata;
import de.jpx3.intave.user.meta.ProtocolMetadata;

@Relocate
public final class PredictionSimulationProcessor implements SimulationProcessor {
  @Override
  public ComplexColliderSimulationResult simulate(User user, Simulator simulator) {
    boolean keyDependent = simulator.affectedByMovementKeys();
    return keyDependent ? performKeySimulation(user, simulator) : simulateWithoutKeyPress(user, simulator);
  }

  private ComplexColliderSimulationResult performKeySimulation(User user, Simulator simulator) {
    MovementMetadata movementData = user.meta().movement();
    return movementData.externalKeyApply ? performKeySimulationFromInput(user, simulator) : performKeyComparisonSimulation(user, simulator);
  }

  private ComplexColliderSimulationResult performKeySimulationFromInput(User user, Simulator simulator) {
    MovementMetadata movementData = user.meta().movement();
    int clientInputKey = movementData.clientForwardKey;
    int clientStrafeKey = movementData.clientStrafeKey;
    boolean jump = movementData.clientPressedJump && movementData.lastOnGround;
    movementData.keyForward = clientInputKey;
    movementData.keyStrafe = clientStrafeKey;
    movementData.physicsJumped = jump;
    KeyPressStudy.enterKeyPress(movementData.keyForward, movementData.keyStrafe);
    return simulateWithKeyPress(user, simulator, clientInputKey, clientStrafeKey, jump);
  }

  private final static double REQUIRED_ACCURACY_FOR_QUICK_PROC_EXIT = 0.002;
  private final static double REQUIRED_ACCURACY_FOR_FLYING_PROC_EXIT = 0.02;

  private ComplexColliderSimulationResult performKeyComparisonSimulation(User user, Simulator simulator) {
    MovementMetadata movementData = user.meta().movement();
    ComplexColliderSimulationResult simulation;
    double simulationAccuracy;
    boolean biasedSimulationFailed;

    //
    // try prediction biased simulation
    //
    simulation = simulateMovementKeyPredictionBiased(user, simulator);
    simulationAccuracy = simulation.accuracy(movementData.motion());
    biasedSimulationFailed = simulationAccuracy > REQUIRED_ACCURACY_FOR_QUICK_PROC_EXIT;

    if (biasedSimulationFailed) {
      //
      // try last-key biased simulation
      //
      simulation = simulateMovementLastKeyBiased(user, simulator);
      simulationAccuracy = simulation.accuracy(movementData.motion());
      biasedSimulationFailed = simulationAccuracy > REQUIRED_ACCURACY_FOR_QUICK_PROC_EXIT;
    }

    //
    // perform iterative simulation procedure
    //
    boolean iterativeAllowed = !user.meta().inventory().inventoryOpen();
    if (biasedSimulationFailed && iterativeAllowed) {
      SimulationStack iterative = simulateMovementIterative(user, simulator);
      simulation = iterative.bestSimulation();
      applyIterativeSimulationTo(user, iterative);
    }
    KeyPressStudy.enterKeyPress(movementData.keyForward, movementData.keyStrafe);
    return simulation;
  }

  @IdoNotBelongHere
  private void applyIterativeSimulationTo(User user, SimulationStack iterativeResult) {
    MetadataBundle meta = user.meta();
    MovementMetadata movementData = meta.movement();
    InventoryMetadata inventoryData = meta.inventory();
    if (movementData.pastPlayerAttackPhysics == 0 && movementData.sprinting && !iterativeResult.reduced()) {
      movementData.ignoredAttackReduce = true;
    }
    boolean movementSuggestsHandIsActive = iterativeResult.handActive();
    boolean packetsSuggestsHandIsActive = inventoryData.handActive();
    if (packetsSuggestsHandIsActive && !movementSuggestsHandIsActive) {
      boolean releaseHandConditions = Hypot.fast(movementData.motionX(), movementData.motionZ()) > 0.3 || movementData.lastTeleport >= 2;
      if (releaseHandConditions) {
        user.meta().inventory().releaseItemNextTick();
      }
    }
    movementData.keyForward = iterativeResult.forward();
    movementData.keyStrafe = iterativeResult.strafe();
    movementData.physicsJumped = iterativeResult.jumped();
  }

  @Override
  public ComplexColliderSimulationResult simulateWithKeyPress(
    User user, Simulator simulator, int forward, int strafe, boolean jumped
  ) {
    MetadataBundle meta = user.meta();
    MovementMetadata movementData = meta.movement();
    Motion motion = movementData.motionProcessorContext.copy();
    motion.resetTo(movementData);
    return simulator.performSimulation(
      user, motion,
      forward, strafe, false, movementData.sprintingAllowed(), jumped,
      meta.inventory().handActive()
    );
  }

  private final static double REQUIRED_PREDICTION_ACCURACY_FOR_PRED_BIAS_PROCEED = 0.1;

  private ComplexColliderSimulationResult simulateMovementKeyPredictionBiased(User user, Simulator simulator) {
    Timings.CHECK_PHYSICS_PROC_BIA.start();
    Timings.CHECK_PHYSICS_PROC_PRED_BIA.start();
    MovementMetadata movementData = user.meta().movement();
    InventoryMetadata inventoryData = user.meta().inventory();
    Motion motionVector = movementData.motionProcessorContext;
    double lastMotionX = movementData.physicsMotionX;
    double lastMotionZ = movementData.physicsMotionZ;
    boolean jumped = false;
    boolean sprinting = movementData.sprintingAllowed() || movementData.hasSprintSpeed;
    if (movementData.lastOnGround && !movementData.denyJump()) {
      double motionY = movementData.motionY();
      jumped = Math.abs(motionY - 0.2) < 1e-5 || motionY == movementData.jumpMotion();
      if (jumped && sprinting) {
        lastMotionX -= movementData.yawSine() * 0.2f;
        lastMotionZ += movementData.yawCosine() * 0.2f;
      }
    }
    if (movementData.inWater && !movementData.denyJump()) {
      jumped = movementData.motionY() > 0.0;
    }
    double differenceX = movementData.motionX() - lastMotionX;
    double differenceZ = movementData.motionZ() - lastMotionZ;
    float yaw = movementData.rotationYaw;

    boolean inventoryOpen = inventoryData.inventoryOpen();
    if (!inventoryOpen && directionPredictionError(differenceX, differenceZ, yaw) > REQUIRED_PREDICTION_ACCURACY_FOR_PRED_BIAS_PROCEED) {
      movementData.physicsJumped = false;
      movementData.keyForward = -2;
      movementData.keyStrafe = -2;
      motionVector.resetTo(movementData);
      Timings.CHECK_PHYSICS_PROC_BIA.stop();
      Timings.CHECK_PHYSICS_PROC_PRED_BIA.stop();
      return ComplexColliderSimulationResult.invalid();
    }

    int directionPrediction = directionFrom(differenceX, differenceZ, yaw);
    int keyForward = forwardKeyFrom(directionPrediction);
    int keyStrafe = strafeKeyFrom(directionPrediction);
    boolean handActive = inventoryData.handActive();
    boolean attackReduce = !AttackDispatcher.REDUCING_DISABLED && movementData.sprintingAllowed() && movementData.pastPlayerAttackPhysics == 0;
    if (sprinting && keyForward != 1) {
      keyForward = 0;
      keyStrafe = 0;
    }
    if (inventoryOpen) {
      keyForward = 0;
      keyStrafe = 0;
    }
    float moveForward = keyForward * 0.98f;
    float moveStrafe = keyStrafe * 0.98f;
    movementData.physicsJumped = jumped;
    motionVector.resetTo(movementData);
    movementData.keyForward = keyForward;
    movementData.keyStrafe = keyStrafe;
    movementData.refreshFriction(sprinting);
    ComplexColliderSimulationResult simulationResult =
      simulator.performSimulation(user, motionVector, moveForward, moveStrafe, attackReduce, sprinting, jumped, handActive);
    Timings.CHECK_PHYSICS_PROC_PRED_BIA.stop();
    Timings.CHECK_PHYSICS_PROC_BIA.stop();
    return simulationResult;
  }

  private int directionFrom(double differenceX, double differenceZ, float yaw) {
    if (Hypot.fast(differenceX, differenceZ) > 0.001) {
      double direction;
      direction = Math.toDegrees(Math.atan2(differenceZ, differenceX)) - 90d;
      direction -= yaw;
      direction %= 360d;
      if (direction < 0)
        direction += 360;
      direction = Math.abs(direction);
      direction /= 45d;
      return (int) Math.round(direction);
    }
    return -1;
  }

  private double directionPredictionError(double differenceX, double differenceZ, float yaw) {
    if (Hypot.fast(differenceX, differenceZ) > 0.001) {
      double direction;
      direction = Math.toDegrees(Math.atan2(differenceZ, differenceX)) - 90d;
      direction -= yaw;
      direction %= 360d;
      if (direction < 0)
        direction += 360;
      direction = Math.abs(direction);
      direction /= 45d;
      return Math.abs(direction - (int) Math.round(direction));
    }
    return 0;
  }

  private final static int[] forwardKeys = {1, 1, 0, -1, -1, -1, 0, 1, 1};
  private final static int[] strafeKeys = {0, -1, -1, -1, 0, 1, 1, 1, 0};

  private static int forwardKeyFrom(int direction) {
    return direction == -1 ? 0 : forwardKeys[direction];
  }

  private static int strafeKeyFrom(int direction) {
    return direction == -1 ? 0 : strafeKeys[direction];
  }

  private ComplexColliderSimulationResult simulateMovementLastKeyBiased(User user, Simulator simulator) {
    Timings.CHECK_PHYSICS_PROC_BIA.start();
    Timings.CHECK_PHYSICS_PROC_LK_BIA.start();
    MovementMetadata movementData = user.meta().movement();
    InventoryMetadata inventoryData = user.meta().inventory();
    Motion motion = movementData.motionProcessorContext;

    int keyForward = movementData.lastKeyForward;
    int keyStrafe = movementData.lastKeyStrafe;
    boolean inventoryOpen = inventoryData.inventoryOpen();

    // return if prediction bias already has calculated this keys
    if (!inventoryOpen && keyForward == movementData.keyForward && keyStrafe == movementData.keyStrafe) {
      Timings.CHECK_PHYSICS_PROC_LK_BIA.stop();
      Timings.CHECK_PHYSICS_PROC_BIA.stop();
      return ComplexColliderSimulationResult.invalid();
    }

    boolean jumped = false;
    if (movementData.lastOnGround && !movementData.denyJump()) {
      double motionY = movementData.motionY();
      jumped = Math.abs(motionY - 0.2) < 1e-5 || motionY == movementData.jumpMotion();
    }
    if (movementData.inWater && !movementData.denyJump()) {
      jumped = movementData.motionY() > 0.0;
    }

    boolean handActive = inventoryData.handActive();
    boolean attackReduce = !AttackDispatcher.REDUCING_DISABLED && movementData.sprintingAllowed() && user.meta().movement().pastPlayerAttackPhysics == 0;
    boolean sprinting = movementData.sprintingAllowed();
    if (sprinting && keyForward != 1) {
      keyForward = 0;
      keyStrafe = 0;
    }
    if (inventoryData.inventoryOpen()) {
      keyForward = 0;
      keyStrafe = 0;
    }
    float moveForward = keyForward * 0.98f;
    float moveStrafe = keyStrafe * 0.98f;
    movementData.physicsJumped = jumped;
    motion.resetTo(movementData);
    movementData.keyForward = keyForward;
    movementData.keyStrafe = keyStrafe;
    movementData.refreshFriction(sprinting);
    ComplexColliderSimulationResult simulationResult = simulator.performSimulation(user, motion, moveForward, moveStrafe, attackReduce, sprinting, jumped, handActive);
    Timings.CHECK_PHYSICS_PROC_LK_BIA.stop();
    Timings.CHECK_PHYSICS_PROC_BIA.stop();
    return simulationResult;
  }

  private final static boolean[] OPTIMISTIC_BOOLEAN_ORDER = new boolean[]{true, false};
  private final static boolean[] PESSIMISTIC_BOOLEAN_ORDER = new boolean[]{false, true};
  private final static boolean[] NEVER = new boolean[]{false};

  private final static int[][] KEYS_USAGE_ORDERED = {{1, 0}, {0, 0}, {1, -1}, {1, 1}, {0, -1}, {0, 1}, {-1, -1}, {-1, 0}, {-1, 1}};

  private SimulationStack simulateMovementIterative(User user, Simulator simulator) {
    Timings.CHECK_PHYSICS_PROC_ITR.start();
    MetadataBundle meta = user.meta();
    InventoryMetadata inventoryData = meta.inventory();
    MovementMetadata movementData = meta.movement();
    ProtocolMetadata clientData = meta.protocol();
    SimulationStack simulationStack = SimulationStack.of(user);
    boolean inLava = movementData.inLava();
    boolean inWater = movementData.inWater;
    boolean lastOnGround = movementData.lastOnGround;
    boolean estimatedJump = Math.abs(movementData.motionY() - (1 - user.sizeOf(movementData.pose()).height() % 1)) < 1e-5 || movementData.motionY() == movementData.jumpMotion();
    boolean skipUseItem = !clientData.sprintWhenHandActive() && movementData.sprinting;

    int iterativeRuns = 0;
    int nearestForwardKey = -2, nearestStrafeKey = -2;
    double nearestKeyDistance = Double.MAX_VALUE;

    SIMULATION:
    for (boolean sprinting : movementData.sprintingAllowed() || movementData.hasSprintSpeed ? /* surprisingly pessimistic */ PESSIMISTIC_BOOLEAN_ORDER : NEVER) {
      movementData.refreshFriction(sprinting);
      for (boolean useItemState : inventoryData.handActive() ? OPTIMISTIC_BOOLEAN_ORDER : PESSIMISTIC_BOOLEAN_ORDER) {
        if (skipUseItem && useItemState) {
          continue;
        }
        IterativeStudy.USE_ITEM_ITERATOR.run();
        for (boolean attackReduce : PESSIMISTIC_BOOLEAN_ORDER) {
          if (attackReduce && (movementData.pastPlayerAttackPhysics >= 1 || AttackDispatcher.REDUCING_DISABLED)) {
            continue;
          }
          IterativeStudy.ATTACK_REDUCE_ITERATOR.run();
          for (boolean jumped : estimatedJump ? OPTIMISTIC_BOOLEAN_ORDER : PESSIMISTIC_BOOLEAN_ORDER) {
            // Jumps are only allowed on the ground :(
            if (jumped && !lastOnGround && !inLava && !inWater) {
              continue;
            }
            if (jumped && movementData.denyJump()) {
              continue;
            }
            IterativeStudy.JUMP_ITERATOR.run();
            boolean hasKeyEstimate = nearestKeyDistance < 1;
            for (int i = (hasKeyEstimate ? -1 : 0); i < 9; i++) {
              int keyForward;
              int keyStrafe;
              if (i >= 0) {
                int[] keyPair = KEYS_USAGE_ORDERED[i];
                keyForward = keyPair[0];
                keyStrafe = keyPair[1];
                if (hasKeyEstimate && keyForward == nearestForwardKey && keyStrafe == nearestStrafeKey) {
                  continue;
                }
              } else {
                keyForward = nearestForwardKey;
                keyStrafe = nearestStrafeKey;
              }
              if (sprinting && keyForward != 1) {
                continue;
              }
              iterativeRuns++;
              double distance = simulateIterativeState(
                simulator,
                user,
                movementData,
                inventoryData,
                simulationStack,
                keyForward, keyStrafe,
                attackReduce, sprinting,
                jumped, useItemState,
                false
              );
              if (distance < nearestKeyDistance) {
                nearestKeyDistance = distance;
                nearestForwardKey = keyForward;
                nearestStrafeKey = keyStrafe;
              }
              if (simulationStack.smallestDistance() <= (movementData.recentlyEncounteredFlyingPacket(2) ? REQUIRED_ACCURACY_FOR_FLYING_PROC_EXIT : REQUIRED_ACCURACY_FOR_QUICK_PROC_EXIT)) {
                break SIMULATION;
              }
            }
          }
        }
      }
    }
    if (simulationStack.noMatch()) {
      simulateIterativeState(
        simulator,
        user,
        movementData,
        inventoryData,
        simulationStack,
        0,
        0,
        false,
        false,
        false,
        false,
        true
      );
    }
    IterativeStudy.USE_ITEM_ITERATOR.pass();
    IterativeStudy.ATTACK_REDUCE_ITERATOR.pass();
    IterativeStudy.JUMP_ITERATOR.pass();
    IterativeStudy.enterTrials(iterativeRuns);
    Timings.CHECK_PHYSICS_PROC_ITR.stop();
    return simulationStack;
  }

  private static String shortenBoolean(boolean bool) {
    return bool ? "1" : "0";
  }

  private static String resolveKeysFromInput(int forward, int strafe) {
    String key = "";
    if (forward == 1) {
      key += "W";
    } else if (forward == -1) {
      key += "S";
    }
    if (strafe == 1) {
      key += "A";
    } else if (strafe == -1) {
      key += "D";
    }
    return key;
  }

  private double compareReceivedMotionWithMotion(User user, Motion context) {
    MovementMetadata movementData = user.meta().movement();
    return MathHelper.distanceOf(
      context.motionX, context.motionY, context.motionZ,
      movementData.motionX(), movementData.motionY(), movementData.motionZ()
    );
  }

  private double simulateIterativeState(
    Simulator simulator,
    User user,
    MovementMetadata movementData,
    InventoryMetadata inventoryData,
    SimulationStack result,
    int keyForward,
    int keyStrafe,
    boolean attackReduce,
    boolean jumped,
    boolean sprinting,
    boolean handActive,
    boolean forceApply
  ) {
    Motion motionVector = movementData.motionProcessorContext;
    float moveForward = keyForward * 0.98f;
    float moveStrafe = keyStrafe * 0.98f;
    motionVector.resetTo(movementData);
    ComplexColliderSimulationResult collisionResult = simulator.performSimulation(
      user, motionVector, moveForward, moveStrafe,
      attackReduce, sprinting, jumped, handActive
    );
    Motion predictedMotion = collisionResult.motion();
    double distance = compareReceivedMotionWithMotion(user, predictedMotion);
    if (forceApply || inventoryData.handActive() == handActive || distance < 0.001) {
      result.tryAppendToState(collisionResult, distance, keyForward, keyStrafe, attackReduce, sprinting, jumped, handActive);
    }
    return distance;
  }

}