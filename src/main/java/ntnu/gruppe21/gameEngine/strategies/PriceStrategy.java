package ntnu.gruppe21.gameEngine.strategies;

import java.math.BigDecimal;

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
     * All strategies most calculate a new price for the stock, when advancing the week.
     *
     * @param currentPrice current price of a stock
     * @return calculated new price for the
     */
    BigDecimal calculateNewPrice(BigDecimal currentPrice);
}
