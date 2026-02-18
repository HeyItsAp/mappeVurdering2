package ntnu.gruppe21.transaction;

import ntnu.gruppe21.Player;
import ntnu.gruppe21.Share;
import ntnu.gruppe21.transaction.calculators.SaleCalculator;

/** Class representing a sale. */
public class Sale extends Transaction {
  /**
   * Creates a new sale.
   *
   * @param share share to be sold.
   * @param week week of sale.
   */
  public Sale(Share share, int week) {
    var calculator = new SaleCalculator(share);

    super(share, week, calculator);
  }

  public void commit(Player player) throws TransactionException {
    if (isCommitted()) {
      throw new TransactionException("Transaction is already committed.");
    }

    player.getPortfolio().removeShare(getShare());

    player.addMoney(getCalculator().calculateTotal());

    player.getTransactionArchive().add(this);

    markCommitted();
  }
}
