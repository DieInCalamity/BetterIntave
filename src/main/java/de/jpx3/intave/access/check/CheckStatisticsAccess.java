package de.jpx3.intave.access.check;

public interface CheckStatisticsAccess {
  long totalProcesses();
  long totalPasses();
  long totalViolations();
  long totalFails();
}
