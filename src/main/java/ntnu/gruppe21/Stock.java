package ntnu.gruppe21;

import java.math.BigDecimal;
import java.util.*;

/** Class representing a stock. */
public class Stock {
  private final String symbol;
  private final String company;
  private final List<BigDecimal> prices;

  /**
   * Creates a stock with initial sales price
   * @param symbol Symbol, represented as shorted string of the Company
   * @param company Company name. For example 'Microsoft'
   * @param salesPrice Initial Sales price
   */
  public Stock(String symbol, String company, BigDecimal salesPrice) {
    this.symbol = Objects.requireNonNull(symbol, "symbol must not be null");
    this.company = Objects.requireNonNull(company, "company must not be null");
    this.prices = new ArrayList<>();
    prices.add(Objects.requireNonNull(salesPrice, "salesPrice must not be null"));
  }

  /**
   * Creates a stock with premade history list. Usually used in {@link ntnu.gruppe21.filehandler.FilehandlerExchange}
   * and {@link ntnu.gruppe21.filehandler.FilehandlerPlayer} to easily and correctly make stock with history.
   * @param symbol Symbol, represented as shorted string of the Company
   * @param company Company name. For example 'Microsoft'
   * @param priceHistory List of prices over time
   */
  public Stock(String symbol, String company, ArrayList<BigDecimal> priceHistory) {
    this.symbol = Objects.requireNonNull(symbol, "symbol must not be null");
    this.company = Objects.requireNonNull(company, "company must not be null");
    this.prices = Objects.requireNonNull(priceHistory, "priceHistory must not be null");
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
