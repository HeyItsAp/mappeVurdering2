package ntnu.gruppe21;

import java.math.BigDecimal;

public interface TransactionCalculator {
  public BigDecimal calculateGross();

  public BigDecimal calculateCommission();

  public BigDecimal calculateTax();

  public BigDecimal calculateTotal();
}
