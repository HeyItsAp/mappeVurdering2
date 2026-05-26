package ntnu.gruppe21.model.gameEngine;

import java.math.BigDecimal;
import ntnu.gruppe21.filehandler.HighScoreManager;
import ntnu.gruppe21.model.gameEngine.challenges.Challenge;

/**
 * The enum dictates the difficulty and has allocated attributes that effect gameplay and final
 * score. Follows a classic enum structure with a constructor.
 */
public enum Difficulty {
  /** EASY: Default value. 1 */
  EASY(BigDecimal.ONE),
  /** Medium: Little harder means better scores/harder challenges. Integer value: 2 */
  MEDIUM(BigDecimal.TWO),

  /** Hard: Harder means better scores/harder challenges. Integer value: 4 */
  HARD(BigDecimal.TWO.multiply(BigDecimal.TWO)),

  /** Realistic: Harder means better scores/harder challenges. Integer value: 4 */
  REALISTIC(BigDecimal.TEN);

  /**
   * DifficultyMultiplier, Higher difficulty yields a better finalScore and little more difficult
   * challenges This attribute reflects that by giving a better multiplier, used in {@link
   * HighScoreManager} and effects challenges in {@link Challenge}
   */
  private final BigDecimal DifficultyMultiplier;

  /**
   * Attributes that effect gameplay:'
   *
   * @param DifficultyMultiplier Higher difficulty yields a better finalScore.
   */
  Difficulty(BigDecimal DifficultyMultiplier) {
    this.DifficultyMultiplier = DifficultyMultiplier;
  }

  /**
   * Get-method for the DifficultyMultiplier
   *
   * @return DifficultMultiplier used in finalScore Calculations and challenges.
   */
  public BigDecimal getDifficultyMultiplier() {
    return DifficultyMultiplier;
  }
}
