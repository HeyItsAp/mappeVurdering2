package ntnu.gruppe21.model.transaction;

import ntnu.gruppe21.model.Share;

public class TransactionFactory {

  public Transaction createPurchase(Share share, int week) {
    return new Purchase(share, week);
  }

  public Transaction createSale(Share share, int week) {
    return new Sale(share, week);
  }
}
