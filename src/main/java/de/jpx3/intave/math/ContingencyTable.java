package de.jpx3.intave.math;

public final class ContingencyTable {
  private final Matrix data;

  public ContingencyTable(int rows, int columns) {
    this.data = new Matrix(rows, columns);
  }

  public void increment(int row, int column) {
    this.data.set(row, column, this.data.get(row, column) + 1);
  }

  public long get(int row, int column) {
    return (long) this.data.get(row, column);
  }

  // P(A)
  public double probabilityOf(int event) {
    // event sum divided by total sum
    return (double) rowSum(event) / this.data.sum();
  }

  // P(A|B)
  public double conditionalProbabilityOf(int event, int condition) {
    // event sum divided by condition sum
    return (double) get(event, condition) / columSum(condition);
  }

  public long rowSum(int row) {
    long sum = 0L;
    for (int i = 0; i < this.data.columns(); i++) {
      sum += get(row, i);
    }
    return sum;
  }

  public long columSum(int column) {
    long sum = 0L;
    for (int i = 0; i < this.data.rows(); i++) {
      sum += get(i, column);
    }
    return sum;
  }

  public double chi2() {
    double chi2 = 0.0;
    long total = 0L;
    for (int i = 0; i < this.data.rows(); i++) {
      for (int j = 0; j < this.data.columns(); j++) {
        total += get(i, j);
      }
    }
    for (int i = 0; i < this.data.rows(); i++) {
      for (int j = 0; j < this.data.columns(); j++) {
        double expected = (double) rowSum(i) * columSum(j) / total;
        chi2 += Math.pow(get(i, j) - expected, 2) / expected;
      }
    }
    return chi2;
  }

  public double chi2pValue() {
    return Gamma.inverseRegularizedIncompleteGamma(((double)this.data.rows() - 1) * 0.5, chi2() * 0.5) / 100d;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < this.data.rows(); i++) {
      for (int j = 0; j < this.data.columns(); j++) {
        builder.append(get(i, j)).append(" ");
      }
      builder.append("\n");
    }
    return builder.toString();
  }
}
