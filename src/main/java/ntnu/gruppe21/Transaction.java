package ntnu.gruppe21;

import ntnu.gruppe21.calculators.TransactionCalculator;

/** Abstract class representing a single transaction. */
public abstract class Transaction {
  private final Share share;
  private final int week;
  private final TransactionCalculator calculator;
  private boolean committed = false;

  protected Transaction(Share share, int week, TransactionCalculator calculator) {
    this.share = share;
    this.week = week;
    this.calculator = calculator;
  }

  public Share getShare() {
    return share;
  }

  public int getWeek() {
    return week;
  }

  public TransactionCalculator getCalculator() {
    return calculator;
  }

  /**
   * Checks if the transaction is comitted.
   *
   * @return True if committed, false if not
   */
  public boolean isCommitted() {
    return committed;
  }

  public abstract void commit(Player player);
}
