package de.jpx3.intave.world.collision.patches;

import com.google.common.collect.Lists;
import de.jpx3.intave.adapter.MinecraftVersion;
import de.jpx3.intave.tools.wrapper.WrappedAxisAlignedBB;
import de.jpx3.intave.user.User;
import de.jpx3.intave.user.UserRepository;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: 04/28/21 DOES NOT YET SUPPORT STRAIGHT PANES WTF PLZ FIX RICHY 

public final class BlockThinPatch extends BoundingBoxPatch {
  protected static final WrappedAxisAlignedBB[] STATES_8 = new WrappedAxisAlignedBB[] {
    new WrappedAxisAlignedBB(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F), // base a
    new WrappedAxisAlignedBB(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F), // base b
    new WrappedAxisAlignedBB(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F), // north
    new WrappedAxisAlignedBB(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F), // east
    new WrappedAxisAlignedBB(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F), // south
    new WrappedAxisAlignedBB(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F), // west
  };

  protected static final List<WrappedAxisAlignedBB> EMPTY_STATE_8 = Lists.newArrayList(STATES_8[0], STATES_8[1]);

  protected static final WrappedAxisAlignedBB[] STATES_9 = new WrappedAxisAlignedBB[] {
    new WrappedAxisAlignedBB(0.4375D, 0.0D, 0.4375D, 0.5625D, 1.0D, 0.5625D), // base
    new WrappedAxisAlignedBB(0.4375D, 0.0D, 0.0D, 0.5625D, 1.0D, 0.5625D), // north
    new WrappedAxisAlignedBB(0.4375D, 0.0D, 0.4375D, 1.0D, 1.0D, 0.5625D), // east
    new WrappedAxisAlignedBB(0.4375D, 0.0D, 0.4375D, 0.5625D, 1.0D, 1.0D), // south
    new WrappedAxisAlignedBB(0.0D, 0.0D, 0.4375D, 0.5625D, 1.0D, 0.5625D), // west
  };

  protected static final List<WrappedAxisAlignedBB> EMPTY_STATE_9 = Lists.newArrayList(STATES_9[0]);

  public BlockThinPatch() {
    super(Material.STAINED_GLASS_PANE, Material.IRON_FENCE);
    Arrays.stream(STATES_8).forEach(box -> box.setOriginBox(true));
    Arrays.stream(STATES_9).forEach(box -> box.setOriginBox(true));
  }

  @Override
  protected List<WrappedAxisAlignedBB> patch(World world, Player player, Block block, List<WrappedAxisAlignedBB> bbs) {
    return patch(world, player, block.getType(), block.getData(), bbs);
  }

  @Override
  protected List<WrappedAxisAlignedBB> patch(World world, Player player, Material type, int blockState, List<WrappedAxisAlignedBB> bbs) {
    User user = UserRepository.userOf(player);
    if (MinecraftVersion.VER1_9_0.atOrAbove()) {
      if (!user.meta().clientData().combatUpdate()) {
        // update 1.9 to 1.8
        int[] indices = new int[bbs.size()];
        int count = 0;
        for (WrappedAxisAlignedBB bb : bbs) {
          indices[count++] = indexOf9(bb);
        }
        boolean hasState = false;
        for (int index : indices) {
          if (index > 0) {
            hasState = true;
            break;
          }
        }
        if (hasState) {
          List<WrappedAxisAlignedBB> list = new ArrayList<>();
          for (int value : indices) {
            if (value > 0) {
              list.add(STATES_8[value + 1]);
            }
          }
          return list;
        } else {
          return EMPTY_STATE_8;
        }
      }
    } else {
      if (user.meta().clientData().combatUpdate()) {
        // update 1.8 to 1.9
        int[] indices = new int[bbs.size()];
        int count = 0;
        for (WrappedAxisAlignedBB bb : bbs) {
          indices[count++] = indexOf8(bb);
        }
        boolean hasState = true;
        for (int index : indices) {
          if (index <= 1) {
            hasState = false;
            break;
          }
        }
        if (hasState) {
          List<WrappedAxisAlignedBB> list = new ArrayList<>();
          for (int operand : indices) {
            list.add(STATES_9[operand - 1]);
          }
          return list;
        } else {
          return EMPTY_STATE_9;
        }
      }
    }

    return super.patch(world, player, type, blockState, bbs);
  }

  private int indexOf8(WrappedAxisAlignedBB axisAlignedBB) {
    for (int i = 0, length = STATES_8.length; i < length; i++) {
      WrappedAxisAlignedBB wrappedAxisAlignedBB = STATES_8[i];
      if (wrappedAxisAlignedBB.equals(axisAlignedBB)) {
        return i;
      }
    }
    return -1;
  }

  private int indexOf9(WrappedAxisAlignedBB axisAlignedBB) {
    for (int i = 0, length = STATES_9.length; i < length; i++) {
      WrappedAxisAlignedBB wrappedAxisAlignedBB = STATES_9[i];
      if (wrappedAxisAlignedBB.equals(axisAlignedBB)) {
        return i;
      }
    }
    return -1;
  }
}