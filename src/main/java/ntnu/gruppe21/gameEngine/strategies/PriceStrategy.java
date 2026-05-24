package ntnu.gruppe21.gameEngine.strategies;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import ntnu.gruppe21.Stock;
import ntnu.gruppe21.gameEngine.Difficulty;

/**
 * TODO: Implement handling of different strategies to FilehandlerExchange. PriceStrategy interface
 * is used to calculate a new stock price based on different algorithms and/or special effects.
 *
 * <p>Most Price Strategies will incorporate Difficulty and the Grace-mechanic in their
 * calculations. Strategies will always return a new price PriceStrategy follow the principles of
 * {@code Strategy Behavioral Pattern}.
 *
 * <p>
 *     Different Algorithms as separate classes but still interchangeable with some predefined
 * structure: Each Strategy needs a way correctly parse/serialize and format changes when saving or
 * loading.
 *     This also makes Junit testing impractical as values are everchanging.
 * </p>
 */
public interface PriceStrategy {
  /** All Strategies need a unique identifier. So saving can be done. */
  String getStrategyId();

  /**
   * All strategies most calculate a new by taking a list of the stock and updating it.
   *
   * @param stocks
   */
  public void calculateNewPrice(List<Stock> stocks);

  /*

  /** Setter for strategies difficulty. Must be implemented */
  public void setDifficulty(Difficulty difficulty);

  /**
   * Optional: Different strategy might need different stock attributes. Override this if this is
   * true.
   */
  default Stock createStock(String symbol, String company, ArrayList<BigDecimal> priceHistory) {
    return new Stock(symbol, company, priceHistory);
  }

  /** Serializes any extras, in this case Stocks @Override if necessary */
  default String saveStockExtras(Stock stock) {
    return ""; // default: nothing extra to save
  }

  /** Reconstructs extras, in this case stocks and extras. @Override if necessary */
  default void deserializeStockExtras(Stock stock, String extras) {
    // default: nothing to restore
  }

  /** Copy any strategy-specific state from source to target after creation */
  default void copyStockState(Stock source, Stock target) {
    // default: nothing to copy for plain stocks
  }
}
