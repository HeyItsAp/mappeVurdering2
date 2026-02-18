package ntnu.gruppe21;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
}
