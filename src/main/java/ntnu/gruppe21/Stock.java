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

  public BigDecimal getHighestPrice(){
    Optional<BigDecimal> maxOptional = prices.stream().max(Comparator.naturalOrder());
    return maxOptional.isPresent() ? maxOptional.get() : new BigDecimal(0);
  }
  public BigDecimal getLowestPrice(){
    Optional<BigDecimal> maxOptional = prices.stream().min(Comparator.naturalOrder());
    return maxOptional.isPresent() ? maxOptional.get() : new BigDecimal(0);
  }
  public BigDecimal getLatestPriceChange(){
    if (prices.size() < 2){
      System.out.println("This Stock has not gone through any changes");
      return BigDecimal.ZERO;
    }

    BigDecimal latest = prices.get(prices.size() - 1);
    BigDecimal previous = prices.get(prices.size() - 2);

    return latest.subtract(previous);
  }
}
