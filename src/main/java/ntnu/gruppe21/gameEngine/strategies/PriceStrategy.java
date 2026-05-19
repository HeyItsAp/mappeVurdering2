package ntnu.gruppe21.gameEngine.strategies;

import ntnu.gruppe21.Stock;
import ntnu.gruppe21.gameEngine.Difficulty;

import java.math.BigDecimal;
import java.util.List;

/**
 * TODO: Implement handling of different strategies to FilehandlerExchange.
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

    /**
     * Setter for strategies difficulty. Must be implemented
     */
    public void setDifficulty(Difficulty difficulty);

    /**
     * Optional: Different strategy might need different stock attributes. Override this if this is true.
     */
    default Stock createStock(String symbol, String company, BigDecimal price) {
        return new Stock(symbol, company, price);
    }
}