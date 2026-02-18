package ntnu.gruppe21;

import ntnu.gruppe21.calculators.SaleCalculator;

public class Sale extends Transaction {
  public Sale(Share share, int week) {
    var calculator = new SaleCalculator(share);

    super(share, week, calculator);
  }
}
