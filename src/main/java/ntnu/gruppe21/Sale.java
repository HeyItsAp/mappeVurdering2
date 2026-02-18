package ntnu.gruppe21;

import ntnu.gruppe21.calculators.SaleCalculator;

public class Sale extends Transaction {
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
