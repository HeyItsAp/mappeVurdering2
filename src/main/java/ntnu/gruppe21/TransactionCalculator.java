package ntnu.gruppe21;

import java.math.BigDecimal;

/**
 * The interface represents a calculator for transactions.
 *
 * <p>It should only be used in transaction calculators like purchases and sales.
 */
public interface TransactionCalculator {
  /**
   * Calculates the gross value of the share.
   *
   * @return The gross value
   */
  public BigDecimal calculateGross();

  /**
   * Calculates the commission fee of the transaction.
   *
   * @return The amount that need to be paid of commission
   */
  public BigDecimal calculateCommission();

  /**
   * Calculates the tax that need to be paid to the government.
   *
   * @return The tax
   */
  public BigDecimal calculateTax();

  /**
   * Calculates the total price of the transaction, including commision and tax.
   *
   * @return The amount to be paid
   */
  public BigDecimal calculateTotal();
}
