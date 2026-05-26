package ntnu.gruppe21.model;

/* UNFINISHED */

import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import ntnu.gruppe21.model.gameEngine.Difficulty;
import ntnu.gruppe21.model.gameEngine.strategies.PriceStrategy;
import ntnu.gruppe21.model.gameEngine.strategies.marketsimulator.MarketSimulator;
import ntnu.gruppe21.model.transaction.Transaction;
import ntnu.gruppe21.model.transaction.TransactionFactory;

/**
 * Represents a realistic exchange with list of stock and the ability to make a transaction.
 * Incorporates specific game mechanic, advancing. Advancing updates the week counter and updates
 * prices of each stock.
 */
public class Exchange {
  /* Name of exchange */
  private final String name;

  /* The current week of the game */
  private int week;

  /* A map of stock symbols to Stock objects, representing the stocks available on the exchange. */
  private final Map<String, Stock> stockMap;

  /* Factory to produce correct Transactions, purchase or sell */
  private final TransactionFactory transactionFactory;

  /* Difficulty enum, difficulty chosen by player at start */
  private Difficulty difficulty;

  /**
   * Drives per-week price advancement for all stocks Strategies can be interchanged in the
   * constructor.
   */
  private final PriceStrategy strategy;

  /**
   * Creates a new Exchange with a builder. Builder is defined at the bottom of this file.
   *
   * @param builder builder, follows the principles of Builder Creational Design
   */
  public Exchange(Builder builder) {
    this.name = builder.name;
    this.week = builder.week;
    this.transactionFactory = builder.transactionFactory;
    this.difficulty = builder.difficulty;
    this.strategy = builder.strategy;
    this.stockMap = builder.stockMap;
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
   * Returns the StockMap
   *
   * @return StockMap in question
   */
  public Map<String, Stock> getStockMap() {
    return stockMap;
  }

  /**
   * Getter for the player choses difficulty
   *
   * @return Difficulty {@link Difficulty}
   */
  public Difficulty getDifficulty() {
    return difficulty;
  }

  /**
   * TransactionFactory used for making transactions
   *
   * @return TransactionFactory {@link TransactionFactory}
   */
  public TransactionFactory getTransactionFactory() {
    return transactionFactory;
  }

  /**
   * Getter for a {@link PriceStrategy} object
   *
   * @return {@link PriceStrategy} object
   */
  public PriceStrategy getStrategy() {
    return strategy;
  }

  /**
   * Setting difficulty will be done after choosing exchange. This method reflects that.
   *
   * @param difficulty {@link Difficulty} contains the FinalScoreMultiplier.
   */
  public void setDifficulty(Difficulty difficulty) {
    this.difficulty = difficulty;
    strategy.setDifficulty(difficulty);
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
  public Transaction buy(String symbol, int quantity, Player player) {
    Stock stock = getStock(symbol);
    Share share = new Share(stock, quantity, stock.getSalesPrice());
    Transaction purchase = transactionFactory.createPurchase(share, week);

    if (player.getCurrentMoney().compareTo(purchase.getCalculator().calculateTotal()) <= 0) {
      throw new IllegalArgumentException("Insufficient Funds");
    }
    return purchase;
  }

  /**
   * Creates a sale for a wanted stock.
   *
   * @param symbol The symbol of the stock to be sold.
   * @param quantity Amount of stocks to be sold.
   * @param player The player doing the sale.
   * @return The transaction.
   */
  public Transaction sell(String symbol, int quantity, Player player) {
    Stock stock = getStock(symbol);
    Share share = new Share(stock, quantity, stock.getSalesPrice());
    return transactionFactory.createSale(share, week);
  }

  /** Advances the exchange by one week, updating all stock prices via the market simulator. */
  public void advance() {
    week++;
    strategy.calculateNewPrice(List.copyOf(stockMap.values()));
  }

  /**
   * Returns a list of the top-performing stocks based on their most recent price change.
   *
   * <p>Stocks are sorted in descending order by their latest price change, meaning the stocks with
   * the highest positive change appear first.
   *
   * <p>The number of stocks returned is limited by the {@code limit} parameter.
   *
   * @param limit the maximum number of top gainers to return
   * @return a list of stocks with the highest recent price increases
   * @throws IllegalArgumentException if {@code limit} is greater than the number of available
   *     stocks
   */
  public List<Stock> getGainers(int limit) {
    if (limit > stockMap.size()) {
      throw new IllegalArgumentException("Limit can be bigger than number of stock");
    }
    if (limit <= 0) {
      throw new IllegalArgumentException("Limit cannot be below 0");
    }

    List<Stock> winners =
        stockMap.values().stream()
            .sorted(Comparator.comparing(Stock::getLatestPriceChange).reversed())
            .toList();
    return winners.stream().limit(limit).collect(toList());
  }

  /**
   * Returns a list of the worst-performing stocks based on their most recent price change.
   *
   * <p>Stocks are sorted in ascending order by their latest price change, meaning the stocks with
   * the largest negative change appear first.
   *
   * <p>The number of stocks returned is limited by the {@code limit} parameter.
   *
   * @param limit the maximum number of top losers to return
   * @return a list of stocks with the largest recent price decreases
   * @throws IllegalArgumentException if {@code limit} is greater than the number of available
   *     stocks
   */
  public List<Stock> getLosers(int limit) {
    if (limit > stockMap.size()) {
      throw new IllegalArgumentException("Limit can be bigger than number of stock");
    }
    if (limit <= 0) {
      throw new IllegalArgumentException("Limit cannot be below 0");
    }

    List<Stock> losers =
        stockMap.values().stream()
            .sorted(Comparator.comparing(Stock::getLatestPriceChange))
            .toList();
    return losers.stream().limit(limit).collect(toList());
  }

  /**
   * Standard builder following the standard builder creational pattern. Name and TransactionFactory
   * does not have a default value, everything else does but can be subject to change when building.
   */
  public static class Builder {
    private final String name;
    private final TransactionFactory transactionFactory;

    private PriceStrategy strategy = new MarketSimulator();
    private Difficulty difficulty = Difficulty.EASY;
    private int week = 1;
    private Map<String, Stock> stockMap = Map.of();

    /**
     * Starts a builder object with the minimum for an Exchange, the name
     *
     * @param name Name for the Exchange
     */
    public Builder(String name) {
      this.name = name;
      this.transactionFactory = new TransactionFactory();
    }

    /**
     * Set-method through builder.
     *
     * @param strategy Strategy to be changed
     * @return Builder with updated strategy
     */
    public Builder strategy(PriceStrategy strategy) {
      this.strategy = strategy;
      return this;
    }

    /**
     * Set-method through builder. Stock go through strategy stock creation factory
     *
     * @param stocks Strategy to be changed
     * @return Builder with updated strategy
     */
    public Builder stockMap(List<Stock> stocks) {

      List<Stock> strategySpecificStock =
          stocks.stream()
              .map(
                  stock -> {
                    Stock created =
                        strategy.createStock(
                            stock.getSymbol(),
                            stock.getCompany(),
                            new ArrayList<>((stock.getPriceHistory())));
                    strategy.copyStockState(stock, created); // delegates to strategy
                    return created;
                  })
              .toList();

      this.stockMap =
          strategySpecificStock.stream()
              .collect(Collectors.toMap(Stock::getSymbol, Function.identity()));
      return this;
    }

    /**
     * Set-method through builder. Stock go through strategy stock creation factory
     *
     * @param difficulty difficulty to be changed
     * @return Builder with updated difficulty
     */
    public Builder difficulty(Difficulty difficulty) {
      this.difficulty = difficulty;
      return this;
    }

    /**
     * Set-method through builder.
     *
     * @param week week to be changed
     * @return Builder with updated week
     */
    public Builder week(int week) {
      this.week = week;
      return this;
    }

    /**
     * Build-method, finalizes all changes and returns a complete Exchange Object
     *
     * @return complete Exchange Object
     */
    public Exchange build() {
      return new Exchange(this);
    }
  }
}
