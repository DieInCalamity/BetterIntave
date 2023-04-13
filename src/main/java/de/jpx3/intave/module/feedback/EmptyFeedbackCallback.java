package de.jpx3.intave.module.feedback;

import org.bukkit.entity.Player;

/**
 * Class generated using IntelliJ IDEA
 * Created by Richard Strunk 2023
 */

public interface EmptyFeedbackCallback extends FeedbackCallback<Object> {
  @Override
  default void success(Player player, Object target) {
    success();
  }

  void success();
}
