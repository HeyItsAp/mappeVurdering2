package ntnu.gruppe21;

import ntnu.gruppe21.calculators.PurchaseCalculator;

public class Purchase extends Transaction {
  public Purchase(Share share, int week) {
    var calculator = new PurchaseCalculator(share);

    super(share, week, calculator);
  }

  public void commit(Player player) throws TransactionException{
    if (isCommitted()) {
      throw new TransactionException("Transaction is already committed.");
    }

    player.withdrawMoney(getCalculator().calculateTotal());

    player.getPortfolio().addShare(getShare());

    player.getTransactionArchive().add(this);

    markCommitted();
  }
}
