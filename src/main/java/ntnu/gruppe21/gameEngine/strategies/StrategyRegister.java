package ntnu.gruppe21.gameEngine.strategies;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import ntnu.gruppe21.gameEngine.strategies.marketsimulator.MarketSimulator;
import ntnu.gruppe21.gameEngine.strategies.standard.StandardStrategy;

/**
 * Serializable class, just used to get read strategiIDs (as string) to return proper strategies.
 * Currently, two strategies, adding new just means adding a new key and value
 */
public class StrategyRegister {

  /**
   * String id, should be the same as when defining {@code #getStrategyId()} in {@link
   * PriceStrategy}'s
   */
  private static final Map<String, Supplier<PriceStrategy>> Registry =
      new HashMap<>(
          Map.of(
              "MARKET_SIMULATOR", MarketSimulator::new,
              "STANDARD", StandardStrategy::new));

  /**
   * Returns Strategy type of id by searching for the strategy in Registry and using supplier
   * interface to get a strategy that implements {@link PriceStrategy}
   *
   * @param id String the uniquely identifies each strategies.
   * @return {@link PriceStrategy}
   */
  public static PriceStrategy fromId(String id) {
    Supplier<PriceStrategy> supplier = Registry.get(id);
    if (supplier == null) {
      throw new IllegalArgumentException("Unknown strategy id: " + id);
    }
    return supplier.get();
  }
}
