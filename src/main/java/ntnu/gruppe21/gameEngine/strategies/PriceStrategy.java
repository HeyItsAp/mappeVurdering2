package ntnu.gruppe21.gameEngine.strategies;

import ntnu.gruppe21.Stock;

import java.math.BigDecimal;
import java.util.List;

/**
 * PriceStrategy interface is used to calculate a new stock price based on different
 * algorithms and/or special effects.
 * <p>
 *  Most Price Strategies will incorporate Difficulty and the Grace-mechanic in their
 *      calculations.
 *  Strategies will always return a new price
 *  PriceStrategy follow the principles of {@code Strategy Behavioral Pattern}.
 *      Different Algorithms as separate classes but still interchangeable.
 * </p>
 *
 */
public interface PriceStrategy {
    /**
     * All strategies most calculate a new by taking a list of the stock and updating it.
     *
     * @param stocks
     */
    public void calculateNewPrice(List<Stock> stocks);
}