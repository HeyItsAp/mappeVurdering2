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

  public List<BigDecimal> getPriceHistory() {
    return prices;
  }

  public BigDecimal getHighestPrice() {
    return prices.stream().max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
  }

  public BigDecimal getLowestPrice() {
    return prices.stream().min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
  }

  public BigDecimal getLatestPriceChange() {
    if (prices.size() < 2) {
      return BigDecimal.ZERO;
    }
    return prices.getLast().subtract(prices.get(prices.size() - 2));
  }
}
