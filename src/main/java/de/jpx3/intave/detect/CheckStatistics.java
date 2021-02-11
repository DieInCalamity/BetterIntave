package de.jpx3.intave.detect;

public final class CheckStatistics {
  private long totalViolations;
  private long executedCommands;

  private long passed;
  private long failed;

  public long totalViolations() {
    return totalViolations;
  }

  public void setTotalViolations(long totalViolations) {
    this.totalViolations = totalViolations;
  }

  public long executedCommands() {
    return executedCommands;
  }

  public void setExecutedCommands(long executedCommands) {
    this.executedCommands = executedCommands;
  }

  public long passed() {
    return passed;
  }

  public void setPassed(long passed) {
    this.passed = passed;
  }

  public long failed() {
    return failed;
  }

  public void setFailed(long failed) {
    this.failed = failed;
  }
}
