package ntnu.gruppe21;

import java.math.BigDecimal;
import java.util.Objects;

public class Share {
  private final Stock stock;
  private final BigDecimal quantity;
  private final BigDecimal purchasePrice;

  public Share(Stock stock, BigDecimal quantity, BigDecimal purchasePrice) {
    this.stock = Objects.requireNonNull(stock, "Stock cannot be null");
    this.quantity = Objects.requireNonNull(quantity, "Quantity cannot be null");
    this.purchasePrice = Objects.requireNonNull(purchasePrice, "Purchase price cannot be null");
  }

  public Stock getStock() {
    return stock;
  }

  public BigDecimal getQuantity() {
    return quantity;
  }

  public BigDecimal getPurchasePrice() {
    return purchasePrice;
  }
}
