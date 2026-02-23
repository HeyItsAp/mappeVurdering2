package ntnu.gruppe21;

/* UNFINISHED */

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import ntnu.gruppe21.transaction.Purchase;
import ntnu.gruppe21.transaction.Sale;
import ntnu.gruppe21.transaction.Transaction;

public class Exchange {
  /* Name of exchange */
  private final String name;

  /* The current week of the game */
  private int week;

  /* A map of stock symbols to Stock objects, representing the stocks available on the exchange. */
  private final Map<String, Stock> stockMap;

  /* A random number generator, used for simulating stock price changes??? */
  private final Random random;

  /**
   * Creates a new Exchange with the specified name, week, stock map, and random number generator.
   *
   * @param name the name of the exchange.
   * @param stocks list of stocks to be traded at this exchange.
   */
  public Exchange(String name, List<Stock> stocks) {
    this.name = name;
    this.week = 1;
    this.stockMap =
        stocks.stream().collect(Collectors.toMap(Stock::getSymbol, Function.identity()));

    this.random = new Random();
  }

  /**
   * Returns the name of the exchange.
   *
   * @return the name in question.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the current week of the game.
   *
   * @return the week in question.
   */
  public int getWeek() {
    return week;
  }

  /**
   * Returns the true or false based on if desired stock is contained in the exchange.
   *
   * @param symbol The symbol of the desired stock.
   * @return True, if contains the stock, false if not.
   */
  public boolean hasStock(String symbol) {
    return stockMap.containsKey(symbol);
  }

  /**
   * Returns the Stock object associated with the given symbol.
   *
   * @param symbol the stock symbol in question.
   * @return the Stock object associated with the given symbol.
   * @throws IllegalArgumentException if the stock symbol is not found in the exchange.
   */
  public Stock getStock(String symbol) {
    Stock stock = stockMap.get(symbol);
    if (stock == null) {
      throw new IllegalArgumentException("Stock symbol not found: " + symbol);
    }
    return stock;
  }

  /**
   * Returns a list of searched for stocks based on the search term. Based on symbol
   *
   * @param searchTerm The term you wish to search for.
   * @return A list of all stocks matching the search.
   */
  public List<Stock> findStock(String searchTerm) {
    return stockMap.values().stream()
        .filter(
            s ->
                s.getSymbol().toLowerCase().contains(searchTerm.toLowerCase())
                    || s.getCompany().toLowerCase().contains(searchTerm.toLowerCase()))
        .toList();
  }

  /**
   * Creates a purchase for a wanted stock.
   *
   * @param symbol The symbol of the stock to be purchased.
   * @param quantity Amount of stocks to be purchased.
   * @param player The player doing the purchase.
   * @return The transaction.
   */
  public Transaction buy(String symbol, BigDecimal quantity, Player player) {
    Stock stock = getStock(symbol);
    Share share = new Share(stock, quantity, stock.getSalesPrice());
    return new Purchase(share, week);
  }

  /**
   * Creates a sale for a wanted stock.
   *
   * @param symbol The symbol of the stock to be sold.
   * @param quantity Amount of stocks to be sold.
   * @param player The player doing the sale.
   * @return The transaction.
   */
  public Transaction sell(String symbol, BigDecimal quantity, Player player) {
    Stock stock = getStock(symbol);
    Share share = new Share(stock, quantity, stock.getSalesPrice());
    return new Sale(share, week);
  }

  /**
   * Advances the exchange by one week, updating the stock prices.
   */
  public void advance() {
    for (Stock stock : stockMap.values()) {
      BigDecimal currentPrice = stock.getSalesPrice();
      double changePercent =
          (random.nextDouble() - 0.5) * 0.1; // Simulate a price change between -5% and +5%
      BigDecimal newPrice = currentPrice.multiply(BigDecimal.valueOf(1 + changePercent));
      stock.addNewSalesPrice(newPrice);
    }
    week++;
  }
}
