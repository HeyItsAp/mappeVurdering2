package gameEngine;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import ntnu.gruppe21.gameEngine.Difficulty;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Difficulty enum.
 *
 * <p>These tests verify that each difficulty level exposes the correct volatility and grace-period
 * values, since downstream calculations (PriceStrategy, status thresholds) depend on them being
 * exact.
 */
public class DifficultyTest {

  // ----------------------------------------------------------------
  // Positive tests – correct values per difficulty level
  // ----------------------------------------------------------------

  @Test
  void easy_shouldHaveCorrectScoreMultiplier() {
    assertEquals(BigDecimal.ONE, Difficulty.EASY.getDifficultyMultiplier());
  }

  @Test
  void medium_shouldHaveCorrectVolatility() {
    assertEquals(BigDecimal.TWO, Difficulty.MEDIUM.getDifficultyMultiplier());
  }

  @Test
  void hard_shouldHaveCorrectVolatility() {
    assertEquals(
        BigDecimal.TWO.multiply(BigDecimal.TWO), Difficulty.HARD.getDifficultyMultiplier());
  }

  @Test
  void insane_shouldHaveScoreMultiplier() {
    assertEquals(BigDecimal.TEN, Difficulty.REALISTIC.getDifficultyMultiplier());
  }

  // ----------------------------------------------------------------
  // Negative / boundary tests
  // ----------------------------------------------------------------

  @Test
  void harderDifficulty_shouldHaveHigherScoreMultiplier() {
    // Volatility should increase (or stay equal) as difficulty rises
    assertTrue(
        Difficulty.EASY
                .getDifficultyMultiplier()
                .compareTo(Difficulty.MEDIUM.getDifficultyMultiplier())
            < 0);
    assertTrue(
        Difficulty.MEDIUM
                .getDifficultyMultiplier()
                .compareTo(Difficulty.HARD.getDifficultyMultiplier())
            < 0);
    assertTrue(
        Difficulty.HARD
                .getDifficultyMultiplier()
                .compareTo(Difficulty.REALISTIC.getDifficultyMultiplier())
            < 0);
  }
}
