package ntnu.gruppe21.calculators;

import java.math.BigDecimal;
import java.math.RoundingMode;
import ntnu.gruppe21.Share;

/** Calculator class for sales. */
public class SaleCalculator implements TransactionCalculator {
  private final BigDecimal purchasePrice;
  private final BigDecimal salesPrice;
  private final BigDecimal quantity;

  /**
   * Creates a new sale calculator.
   *
   * @param share The share linked to the transaction
   */
  public SaleCalculator(Share share) {
    purchasePrice = share.getPurchasePrice();
    quantity = share.getQuantity();
    salesPrice = share.getStock().getSalesPrice();
  }

  public BigDecimal calculateGross() {
    return salesPrice.multiply(quantity);
  }

  public BigDecimal calculateCommission() {
    return calculateGross().divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
  }

  public BigDecimal calculateTax() {
    BigDecimal costs = purchasePrice.multiply(quantity);
    BigDecimal gain = calculateGross().subtract(calculateCommission()).subtract(costs);
    return gain.multiply(BigDecimal.valueOf(0.3));
  }

  public BigDecimal calculateTotal() {
    return calculateGross().subtract(calculateCommission()).subtract(calculateTax());
  }
}
