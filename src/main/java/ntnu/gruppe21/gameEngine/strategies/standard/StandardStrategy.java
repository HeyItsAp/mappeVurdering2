package ntnu.gruppe21.gameEngine.strategies.standard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;
import ntnu.gruppe21.Stock;
import ntnu.gruppe21.gameEngine.Difficulty;
import ntnu.gruppe21.gameEngine.strategies.PriceStrategy;

/**
 * This strategy uses the designated algorithm from the document to use random to calculate the
 * price but also incorporates difficulty and a random boost. Details covered in associated Javadoc.
 */
public class StandardStrategy implements PriceStrategy {
  /** Max amount of percentagewise change. More details on {@link #setDifficulty(Difficulty)} */
  private double changeRate;

  /**
   * boostStat: A value that can between a maximum of 0.0 - 0.80. correlates to 0% - 80%. Adds
   * random percent-wise increase in value. A true gamble. Higher difficulties give stronger
   * increases. Refer to {@link #setDifficulty(Difficulty)}
   */
  private double boostStat;

  // Random API to generate random percentages
  private final Random random = new Random();

  @Override
  public String getStrategyId() {
    return "STANDARD";
  }

  /**
   * Algorithm that calculates a new stock price based on {@code changeRate} and {@code boostStat}.
   *
   * <p>First random boost calculated based on {@code boostStat}. Random produces a number between
   * -1.0 and 1.0 which then multiplied by max possible percentage change, then boost is added
   * changePercent = changeRate(5%, 10%, 15%, 25%) + boost
   *
   * @param stocks contains stock prices to be looped around
   */
  @Override
  public void calculateNewPrice(List<Stock> stocks) {
    double randomBoost = random.nextDouble(0, boostStat + 0.01);
    double changePercent = (random.nextDouble() * 2 - 1) * changeRate;
    double finalChange = changePercent + randomBoost;
    for (Stock stock : stocks) {
      stock
          .getSalesPrice()
          .multiply(BigDecimal.valueOf(1 + finalChange))
          .setScale(2, RoundingMode.HALF_UP);
    }
  }

  /**
   * Difficulty effect changeRate (in procent, 0.2 = 2%). Which means that the stock can be
   * increase/decrease by a maximum of the changeRate.
   *
   * <p>EASY -> 0.05 (5%) MEDIUM -> 0.10 (10%) HARD -> 0.15 (15%) REALISTIC -> 0.25 (25%) Difficulty
   * change max BoostStat. Higher difficulties will yield better profit changes:
   *
   * <p>EASY -> 0.10 (10%) MEDIUM -> 0.20 (20%) HARD -> 0.40 (40%) REALISTIC -> 1.00 (80%)
   *
   * @param difficulty {@link Difficulty}
   */
  @Override
  public void setDifficulty(Difficulty difficulty) {
    this.changeRate =
        switch (difficulty) {
          case EASY -> 0.02;
          case MEDIUM -> 0.07;
          case HARD -> 0.15;
          case REALISTIC -> 0.25;
        };
    this.boostStat =
        switch (difficulty) {
          case EASY -> 0.1;
          case MEDIUM -> 0.2;
          case HARD -> 0.40;
          case REALISTIC -> 0.80;
        };
  }
}
