package de.jpx3.intave.access;

import de.jpx3.intave.IntaveControl;

/**
 * This exception is not used
 */
public final class UnsupportedFallbackOperationException extends IntaveInternalException {
  public static final UnsupportedFallbackOperationException INSTANCE = new UnsupportedFallbackOperationException("Can't locate player here");

  private UnsupportedFallbackOperationException() {
    super();
  }

  private UnsupportedFallbackOperationException(String message) {
    super(message);
  }

  @Override
  public void setStackTrace(StackTraceElement[] stackTrace) {
    super.setStackTrace(stackTrace);
  }

  @Override
  public synchronized Throwable fillInStackTrace() {
    if (!IntaveControl.FILL_UFOE_STACKTRACE) {
      this.setStackTrace(new StackTraceElement[0]);
      return this;
    } else {
      return super.fillInStackTrace();
    }
  }
}
