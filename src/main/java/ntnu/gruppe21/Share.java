/**
 * Represents a share of a stock, including the stock itself, the quantity of shares owned, and the purchase price.
 * 
 * @author Adrian
 */

package ntnu.gruppe21;

import java.math.BigDecimal;
import java.util.Objects;

public class Share {
  /* The stock that this share represents */
  private final Stock stock;

  /* The quantity of shares owned */
  private BigDecimal quantity;

  /* The purchase price of the shares */
  private BigDecimal purchasePrice;

  /**
   * Creates a new Share with the specified stock, quantity, and purchase price.
   * 
   * @param stock
   * @param quantity
   * @param purchasePrice
   */
  public Share(Stock stock, BigDecimal quantity, BigDecimal purchasePrice) {
    this.stock = Objects.requireNonNull(stock, "Stock cannot be null");
    this.quantity = Objects.requireNonNull(quantity, "Quantity cannot be null");
    this.purchasePrice = Objects.requireNonNull(purchasePrice, "Purchase price cannot be null");
  }

  /**
   * Returns the stock that this share represents.
    *
   * @return the stock in question.
   */
  public Stock getStock() {
    return stock;
  }

  /**
   * Returns the quantity of shares owned.
   * 
   * @return the quantity in question.
   */
  public BigDecimal getQuantity() {
    return quantity;
  }

	/**
	 * Returns the purchase price of the shares.
	 * 
	 * @return the purchase price in question.
	 */
  public BigDecimal getPurchasePrice() {
    return purchasePrice;
  }
}
