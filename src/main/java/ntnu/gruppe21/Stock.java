package ntnu.gruppe21;

import java.math.BigDecimal;
import java.util.*;

/** Class representing a stock. */
public class Stock {
  private final String symbol;
  private final String company;
  private final List<BigDecimal> prices = new ArrayList<>();

  public Stock(String symbol, String company, BigDecimal salesPrice) {
    this.symbol = Objects.requireNonNull(symbol, "symbol must not be null");
    this.company = Objects.requireNonNull(company, "company must not be null");
    prices.add(Objects.requireNonNull(salesPrice, "salesPrice must not be null"));
  }

  public String getSymbol() {
    return symbol;
  }

  public String getCompany() {
    return company;
  }

  public BigDecimal getSalesPrice() {
    return prices.getLast();
  }

  public void addNewSalesPrice(BigDecimal salesPrice) {
    prices.add(Objects.requireNonNull(salesPrice, "salesPrice must not be null"));
  }

  /**
   * Returns the full History of this stock chronologically.
   *
   * @return a list of {@link BigDecimal} values.
   */
  public List<BigDecimal> getPriceHistory() {
    return prices;
  }

  /**
   * Returns the highest recorded price of this stock. If the price history is empty, {@link
   * BigDecimal#ZERO} is returned.
   *
   * @return the maximum price in the price history, or {@code BigDecimal.ZERO} if no prices exist
   */
  public BigDecimal getHighestPrice() {
    Optional<BigDecimal> maxOptional = prices.stream().max(Comparator.naturalOrder());
    return maxOptional.orElse(BigDecimal.ZERO);
  }

  /**
   * Returns the lowest recorded price of this stock. If the price history is empty, {@link
   * BigDecimal#ZERO} is returned.
   *
   * @return the minimum price in the price history, or {@code BigDecimal.ZERO} if no prices exist
   */
  public BigDecimal getLowestPrice() {
    Optional<BigDecimal> minOptional = prices.stream().min(Comparator.naturalOrder());
    return minOptional.orElse(BigDecimal.ZERO);
  }

  /**
   * Calculates the most recent price change for this stock.
   *
   * <p>The value is computed as the difference between the latest price and the previous price in
   * the history: latest - previous
   *
   * <p>If fewer than two prices exist, no change can be calculated and {@link BigDecimal#ZERO} is
   * returned.
   *
   * @return the latest price change, or {@code BigDecimal.ZERO} if insufficient data
   */
  public BigDecimal getLatestPriceChange() {
    if (prices.size() < 2) {
      System.out.println("This Stock has not gone through any changes");
      return BigDecimal.ZERO;
    }

    BigDecimal latest = prices.getLast();
    BigDecimal previous = prices.get(prices.size() - 2);

    return latest.subtract(previous);
  }
}
