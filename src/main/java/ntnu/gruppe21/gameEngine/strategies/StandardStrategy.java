package ntnu.gruppe21.gameEngine.strategies;

import ntnu.gruppe21.gameEngine.Difficulty;
import ntnu.gruppe21.Exchange;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * This strategy uses the designated algorithm from the document to use
 * random to calculate the price but also incorporates difficulty and grace.
 *      Details covered in associated Javadoc.
 *
 */
public class StandardStrategy implements PriceStrategy{
    /** Max amount of percentagewise change form {@link Difficulty} */
    private final double changeRate;

    /**
     * GraceFactor:
     *   A value between 0.0 - 1.0 which where approaching 0 is maximum amount of grace.
     *   When calculating the final price
     */
    private final double graceFactor;

    // Random API to generate random percentages
    private final Random random;

    /**
     * Standard Constructor. Usually invoked at the construction or invoking {@code advance()}
     * in {@link Exchange}.
     *
     * @param difficulty Difficulty enum {@link Difficulty}
     * @param currentweek Current of the {@link Exchange}
     */
    public StandardStrategy(Difficulty difficulty, int currentweek){
        this.changeRate = difficulty.getChangeRate();
        this.graceFactor = calculateGraceFactor(difficulty, currentweek);
        this.random = new Random();
    }

    /**
     * Grace factor has a linear decline as formula is
     *      GraceFactor = currentweek/gracePeriod.
     *      Example, (Easy, 10) and current week is 2:
     *      GraceFactor = 2/10 = 1/5
     *          NextWeek:
     *          GraceFactor = 3/10
     *
     * @param difficulty Difficulty enum {@link Difficulty}
     * @param currentWeek Current of the {@link Exchange}
     * @return
     */
    private double calculateGraceFactor(Difficulty difficulty, int currentWeek) {
        int gracePeriod = difficulty.getGracePeriodWeeks();
        if (gracePeriod == 0 || currentWeek > gracePeriod) return 1.0;
        return (double) currentWeek / gracePeriod;
    }


    /**
     * Algorithm that calculates a new stock price based on difficulty and the current week
     * (relevant for grace).
     * <p>
     *     Random produces a number between -1.0 and 1.0 which then multiplied
     *     by max possible percentage change.
     *          Max Effect = changeRate(3%, 7%, 15%, 25%) * graceFactor(0.0 -> 1.0)
     *     Grace factor is based on current week and difficult and calculated on
     *     {@link #calculateGraceFactor(Difficulty, int)}
     * </p>
     *
     * @param currentPrice current price of a stock
     * @return New calculated price.
     */
    @Override
    public BigDecimal calculateNewPrice(BigDecimal currentPrice) {
        double maxEffect = changeRate * graceFactor;
        double changePercent = (random.nextDouble() * 2 - 1) * maxEffect;
        return currentPrice.multiply(BigDecimal.valueOf(1 + changePercent)).setScale(2, RoundingMode.HALF_UP);
    }

}


