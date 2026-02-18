package ntnu.gruppe21;

import ntnu.gruppe21.calculators.PurchaseCalculator;

public class Purchase extends Transaction {
  public Purchase(Share share, int week) {
    var calculator = new PurchaseCalculator(share);

    super(share, week, calculator);
  }
}
