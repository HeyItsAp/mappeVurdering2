package ntnu.gruppe21.model.transaction;

import ntnu.gruppe21.model.Player;
import ntnu.gruppe21.model.Share;
import ntnu.gruppe21.model.transaction.calculators.TransactionCalculator;

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
   * Checks if the transaction is committed.
   *
   * @return True if committed, false if not
   */
  public boolean isCommitted() {
    return committed;
  }

  protected void markCommitted() {
    committed = true;
  }

  /**
   * Returns a short human-readable label for the transaction type, e.g. {@code "Buy"} or {@code
   * "Sell"}. Used by views to display the type without needing an {@code instanceof} check.
   *
   * @return the type label
   */
  public abstract String getTypeName();

  /**
   * When making a Transaction, commit is the final check before money is withdrawn/added.
   *
   * @param player Player object contain current money and etc.
   * @throws TransactionException Unique Exception
   */
  public abstract void commit(Player player) throws TransactionException;
}
