package ntnu.gruppe21.gameEngine;

import java.math.BigDecimal;
import ntnu.gruppe21.filehandler.HighScoreManager;
import ntnu.gruppe21.gameEngine.challanges.Challenge;

/**
 * The enum dictates the difficulty and has allocated attributes that effect gameplay and final
 * score. Follows a classic enum structure with a constructor.
 */
public enum Difficulty {
  EASY(BigDecimal.ONE),
  MEDIUM(BigDecimal.TWO),
  HARD(BigDecimal.TWO.multiply(BigDecimal.TWO)),
  REALISTIC(BigDecimal.TEN);

  /**
   * DifficultyMultiplier, Higher difficulty yields a better finalScore and little more difficult
   * challanges This attribute reflects that by giving a better multiplier, used in {@link
   * HighScoreManager} and effects challanges in {@link Challenge}
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

  /* Getters */
  public BigDecimal getDifficultyMultiplier() {
    return DifficultyMultiplier;
  }
}
