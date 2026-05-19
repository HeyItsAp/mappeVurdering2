package ntnu.gruppe21.gameEngine;

import java.math.BigDecimal;
import ntnu.gruppe21.filehandler.HighScoreManager;
import ntnu.gruppe21.gameEngine.challanges.Challenge;

/**
 * The enum dictates the difficulty and has allocated attributes that effect gameplay
 * and final score. Follows a classic enum structure with a constructor.
 */
public enum Difficulty {
  EASY(0.03, 10, BigDecimal.ONE),
  MEDIUM(0.07, 5, BigDecimal.TWO),
  HARD(0.15, 2, BigDecimal.TWO.multiply(BigDecimal.TWO)),
  REALISTIC(0.25, 0, BigDecimal.TEN);

  /**
   * ChangeRate Effects how drastic the new stock prices can be. A maximum change percentage wise
   * Percentage based. Easy: 3%, Medium 7%, Hard 15%, Realistic 25%.
   */
  private final double changeRate;

  // GracePeriodWeeks. Grace reduces the changes to give players more time to develop their
  // portfolio
  //      This attribute determines how many weeks this period will last.
  private final int gracePeriodWeeks;

    /**
     *
     * DifficultyMultiplier, Higher difficulty yields a better finalScore and little more difficult challanges
     * This attribute reflects that by giving a better multiplier, used in {@link HighScoreManager} and
     * effects challanges in {@link Challenge}
     *
    */
    private final BigDecimal DifficultyMultiplier;


    /**
     * Attributes that effect gameplay:
     * @param changeRate Effects how drastic the new stock prices will be
     * @param gracePeriodWeeks Grace reduces the changes to give players more time to develop their portfolio
     * @param finalScoreMultiplier Higher difficulty yields a better finalScore.
     */
    Difficulty(double changeRate, int gracePeriodWeeks, BigDecimal finalScoreMultiplier){
        this.changeRate = changeRate;
        this.gracePeriodWeeks = gracePeriodWeeks;
        this.DifficultyMultiplier = finalScoreMultiplier;
    }

  /* Getters */
  public double getChangeRate() {
    return changeRate;
  }

  public int getGracePeriodWeeks() {
    return gracePeriodWeeks;
  }

    public BigDecimal getDifficultyMultiplier() {
        return DifficultyMultiplier;
    }
}
