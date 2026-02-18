package ntnu.gruppe21;

import ntnu.gruppe21.calculators.PurchaseCalculator;

/** Class representing a purchase. */
public class Purchase extends Transaction {
  /**
   * Creates a new purchase.
   *
   * @param share share to be bought.
   * @param week week of purchase.
   */
  public Purchase(Share share, int week) {
    var calculator = new PurchaseCalculator(share);

    super(share, week, calculator);
  }

  public void commit(Player player) throws TransactionException {
    if (isCommitted()) {
      throw new TransactionException("Transaction is already committed.");
    }

    player.withdrawMoney(getCalculator().calculateTotal());

    player.getPortfolio().addShare(getShare());

    player.getTransactionArchive().add(this);

    markCommitted();
  }
}
