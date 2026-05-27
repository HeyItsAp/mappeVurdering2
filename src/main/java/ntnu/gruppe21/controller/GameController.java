package ntnu.gruppe21.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import ntnu.gruppe21.filehandler.HighScoreManager;
import ntnu.gruppe21.filehandler.SaveManager;
import ntnu.gruppe21.model.Exchange;
import ntnu.gruppe21.model.Player;
import ntnu.gruppe21.model.Share;
import ntnu.gruppe21.model.Stock;
import ntnu.gruppe21.model.transaction.Transaction;
import ntnu.gruppe21.model.transaction.TransactionException;

/**
 * Mediates between the model layer and the view layer during an active game session. Holds the
 * current {@link Player} and {@link Exchange}, and exposes all game actions that views may trigger.
 *
 * <p>Read-only data that requires any aggregation or computation is exposed through dedicated query
 * methods (returning plain records) so views never need to traverse the model graph themselves.
 */
public class GameController {
  /**
   * A pre-aggregated snapshot of one portfolio holding, intended for rendering in a table row.
   *
   * @param symbol the stock ticker symbol
   * @param company the company name
   * @param quantity total shares held across all lots
   * @param currentValue total market value at the current price
   * @param stock the underlying {@link Stock} (for navigation)
   */
  public record HoldingSummary(
      String symbol, String company, int quantity, BigDecimal currentValue, Stock stock) {}

  /**
   * A single row in the transaction history, ready for display.
   *
   * @param week the game week in which the transaction occurred
   * @param type human-readable type label, e.g. {@code "Buy"} or {@code "Sell"}
   * @param symbol the stock ticker symbol
   * @param quantity number of shares
   * @param total the gross monetary amount of the transaction
   */
  public record TransactionSummary(
      int week, String type, String symbol, int quantity, BigDecimal total) {}

  /**
   * One line in the sell-all confirmation popup.
   *
   * @param symbol the stock ticker symbol
   * @param quantity number of shares being sold
   * @param value market value of the lot
   */
  public record HoldingLine(String symbol, int quantity, BigDecimal value) {}

  /**
   * Pre-computed data for the "sell all and quit" confirmation popup.
   *
   * @param holdings individual lots to be sold
   * @param cash the player's current cash before the sale
   * @param proceeds total proceeds from selling all lots
   */
  public record SellAllSummary(List<HoldingLine> holdings, BigDecimal cash, BigDecimal proceeds) {
    /** Returns the sum of current cash and sale proceeds. */
    public BigDecimal total() {
      return cash.add(proceeds);
    }
  }

  private final Player player;
  private final Exchange exchange;
  private final String saveSlot;
  private BigDecimal lastWeeklyChange = BigDecimal.ZERO;

  /**
   * Creates a GameController for an active game session.
   *
   * @param player the player for this session
   * @param exchange the exchange for this session
   * @param saveSlot the save-folder name used when persisting the game
   */
  public GameController(Player player, Exchange exchange, String saveSlot) {
    this.player = player;
    this.exchange = exchange;
    this.saveSlot = saveSlot;
  }

  /** Returns the current player. */
  public Player getPlayer() {
    return player;
  }

  /** Returns the current exchange. */
  public Exchange getExchange() {
    return exchange;
  }

  /** Returns the save-slot name for this session. */
  public String getSaveSlot() {
    return saveSlot;
  }

  /**
   * Buys the given quantity of a stock and commits the transaction against the player.
   *
   * @param symbol the stock symbol to buy
   * @param quantity the number of shares to buy
   * @throws IllegalArgumentException if the symbol is unknown or funds are insufficient
   * @throws TransactionException if the transaction cannot be committed
   */
  public void buyStock(String symbol, int quantity) throws TransactionException {
    Transaction purchase = exchange.buy(symbol, quantity, player);
    purchase.commit(player);
  }

  /**
   * Sells the given quantity of a stock and commits the transaction against the player.
   *
   * @param symbol the stock symbol to sell
   * @param quantity the number of shares to sell
   * @throws IllegalArgumentException if the symbol is unknown
   * @throws TransactionException if the transaction cannot be committed
   */
  public void sellStock(String symbol, int quantity) throws TransactionException {
    Transaction sale = exchange.sell(symbol, quantity, player);
    sale.commit(player);
  }

  /** Advances the exchange by one week, updating all stock prices. */
  public void advanceWeek() {
    exchange.advance();
    lastWeeklyChange =
        player.getPortfolio().getShares().stream()
            .map(
                s ->
                    BigDecimal.valueOf(s.getQuantity())
                        .multiply(s.getStock().getLatestPriceChange()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    player.getChallengeManager().evaluateChallenges(player);
  }

  /** Returns the portfolio value change from the most recent week advance. */
  public BigDecimal getLastWeeklyChange() {
    return lastWeeklyChange;
  }

  /**
   * Saves the current game state to the save slot.
   *
   * @throws Exception if saving fails
   */
  public void saveGame() throws Exception {
    new SaveManager(saveSlot, false).save(player, exchange);
  }

  /**
   * Records the current score to the global high-score file. Should be called once at the end of a
   * session, after all shares have been sold.
   */
  public void recordHighScore() {
    HighScoreManager.addFinalScoreToCsv(exchange, player);
  }

  /**
   * Returns the live score for the current session without recording it.
   *
   * @return the calculated score, or {@link BigDecimal#ZERO} if calculation fails
   */
  public BigDecimal getCurrentScore() {
    try {
      return HighScoreManager.calculateFinalScore(exchange, player);
    } catch (Exception e) {
      return BigDecimal.ZERO;
    }
  }

  /**
   * Sells every share currently held in the player's portfolio. Intended for the "sell all and
   * quit" flow.
   *
   * @throws TransactionException if any individual sale cannot be committed
   */
  public void sellAll() throws TransactionException {
    for (Share share : new ArrayList<>(player.getPortfolio().getShares())) {
      Transaction sale = exchange.sell(share.getStock().getSymbol(), share.getQuantity(), player);
      sale.commit(player);
    }
  }

  /**
   * Returns the total number of shares the player holds for the given stock symbol.
   *
   * @param symbol the stock ticker symbol
   * @return quantity owned, or {@code 0} if none
   */
  public int getOwnedShares(String symbol) {
    return player.getPortfolio().getShares().stream()
        .filter(s -> s.getStock().getSymbol().equals(symbol))
        .mapToInt(Share::getQuantity)
        .sum();
  }

  /**
   * Returns the weighted average purchase cost per share for the given stock symbol.
   *
   * @param symbol the stock ticker symbol
   * @return average cost, or {@link BigDecimal#ZERO} if none held
   */
  public BigDecimal getAvgCost(String symbol) {
    List<Share> matching =
        player.getPortfolio().getShares().stream()
            .filter(s -> s.getStock().getSymbol().equals(symbol))
            .toList();
    if (matching.isEmpty()) return BigDecimal.ZERO;
    BigDecimal totalCost =
        matching.stream()
            .map(s -> s.getPurchasePrice().multiply(BigDecimal.valueOf(s.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    int totalQty = matching.stream().mapToInt(Share::getQuantity).sum();
    return totalQty == 0
        ? BigDecimal.ZERO
        : totalCost.divide(BigDecimal.valueOf(totalQty), 2, RoundingMode.HALF_UP);
  }

  /**
   * Returns an aggregated list of portfolio holdings, one entry per distinct stock symbol, suitable
   * for rendering directly in a table.
   *
   * @return holdings in insertion order
   */
  public List<HoldingSummary> getHoldings() {
    List<HoldingSummary> result = new ArrayList<>();
    player.getPortfolio().getShares().stream()
        .collect(
            Collectors.groupingBy(
                s -> s.getStock().getSymbol(), LinkedHashMap::new, Collectors.toList()))
        .forEach(
            (symbol, shares) -> {
              Stock stock = shares.getFirst().getStock();
              int totalQty = shares.stream().mapToInt(Share::getQuantity).sum();
              BigDecimal currentValue =
                  BigDecimal.valueOf(totalQty).multiply(stock.getSalesPrice());
              result.add(
                  new HoldingSummary(symbol, stock.getCompany(), totalQty, currentValue, stock));
            });
    return result;
  }

  /**
   * Returns the full transaction history, newest first, as a flat list of display-ready records.
   *
   * @return transaction summaries in reverse-chronological order
   */
  public List<TransactionSummary> getTransactionHistory() {
    List<Transaction> txs = new ArrayList<>(player.getTransactionArchive().getAll());
    Collections.reverse(txs);
    return txs.stream()
        .map(
            tx ->
                new TransactionSummary(
                    tx.getWeek(),
                    tx.getTypeName(),
                    tx.getShare().getStock().getSymbol(),
                    tx.getShare().getQuantity(),
                    tx.getCalculator().calculateTotal()))
        .toList();
  }

  /**
   * Returns the distinct set of stocks currently held in the player's portfolio, in the order they
   * were first purchased. Intended for the holdings panel on the Today screen.
   *
   * @return list of distinct {@link Stock} objects held
   */
  public List<Stock> getPortfolioStocks() {
    return player.getPortfolio().getShares().stream().map(Share::getStock).distinct().toList();
  }

  /**
   * Returns the best-performing stock held in the player's portfolio this week, or empty if no
   * shares are held.
   *
   * @return the {@link Stock} with the highest latest price change, if any
   */
  public Optional<Stock> getBestPortfolioStock() {
    return player.getPortfolio().getShares().stream()
        .map(Share::getStock)
        .distinct()
        .max(Comparator.comparing(Stock::getLatestPriceChange));
  }

  /**
   * Returns a pre-computed summary of all current holdings for use in the "sell all and quit"
   * confirmation popup. Does not perform any actual sales.
   *
   * @return the sell-all summary
   */
  public SellAllSummary getSellAllSummary() {
    List<HoldingLine> lines = new ArrayList<>();
    BigDecimal proceeds = BigDecimal.ZERO;
    for (Share share : player.getPortfolio().getShares()) {
      BigDecimal value =
          share.getStock().getSalesPrice().multiply(BigDecimal.valueOf(share.getQuantity()));
      proceeds = proceeds.add(value);
      lines.add(new HoldingLine(share.getStock().getSymbol(), share.getQuantity(), value));
    }
    return new SellAllSummary(lines, player.getCurrentMoney(), proceeds);
  }
}
