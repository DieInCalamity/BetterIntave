package de.jpx3.intave.module.tracker.entity;

public final class DestroyedWrappedEntity extends WrappedEntity {
  public DestroyedWrappedEntity() {
    super(0, null, false);
  }

  @Override
  void onLivingUpdate() {
  }
}
