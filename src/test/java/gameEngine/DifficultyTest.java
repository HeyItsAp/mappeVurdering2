package gameEngine;

import static org.junit.jupiter.api.Assertions.*;

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
  void easy_shouldHaveCorrectVolatility() {
    assertEquals(0.03, Difficulty.EASY.getChangeRate(), 1e-9);
  }

  @Test
  void easy_shouldHaveCorrectGracePeriod() {
    assertEquals(10, Difficulty.EASY.getGracePeriodWeeks());
  }

  @Test
  void medium_shouldHaveCorrectVolatility() {
    assertEquals(0.07, Difficulty.MEDIUM.getChangeRate(), 1e-9);
  }

  @Test
  void medium_shouldHaveCorrectGracePeriod() {
    assertEquals(5, Difficulty.MEDIUM.getGracePeriodWeeks());
  }

  @Test
  void hard_shouldHaveCorrectVolatility() {
    assertEquals(0.15, Difficulty.HARD.getChangeRate(), 1e-9);
  }

  @Test
  void hard_shouldHaveCorrectGracePeriod() {
    assertEquals(2, Difficulty.HARD.getGracePeriodWeeks());
  }

  @Test
  void insane_shouldHaveCorrectVolatility() {
    assertEquals(0.25, Difficulty.REALISTIC.getChangeRate(), 1e-9);
  }

  @Test
  void insane_shouldHaveZeroGracePeriod() {
    assertEquals(0, Difficulty.REALISTIC.getGracePeriodWeeks());
  }

  // ----------------------------------------------------------------
  // Negative / boundary tests
  // ----------------------------------------------------------------

  @Test
  void volatility_shouldNeverBeNegative() {
    for (Difficulty d : Difficulty.values()) {
      assertTrue(d.getChangeRate() > 0, "Volatility must be positive for " + d);
    }
  }

  @Test
  void gracePeriod_shouldNeverBeNegative() {
    for (Difficulty d : Difficulty.values()) {
      assertTrue(d.getGracePeriodWeeks() >= 0, "Grace period must be >= 0 for " + d);
    }
  }

  @Test
  void harderDifficulty_shouldHaveHigherOrEqualVolatility() {
    // Volatility should increase (or stay equal) as difficulty rises
    assertTrue(Difficulty.EASY.getChangeRate() <= Difficulty.MEDIUM.getChangeRate());
    assertTrue(Difficulty.MEDIUM.getChangeRate() <= Difficulty.HARD.getChangeRate());
    assertTrue(Difficulty.HARD.getChangeRate() <= Difficulty.REALISTIC.getChangeRate());
  }

  @Test
  void harderDifficulty_shouldHaveShorterOrEqualGracePeriod() {
    // Grace period should decrease (or stay equal) as difficulty rises
    assertTrue(Difficulty.EASY.getGracePeriodWeeks() >= Difficulty.MEDIUM.getGracePeriodWeeks());
    assertTrue(Difficulty.MEDIUM.getGracePeriodWeeks() >= Difficulty.HARD.getGracePeriodWeeks());
    assertTrue(Difficulty.HARD.getGracePeriodWeeks() >= Difficulty.REALISTIC.getGracePeriodWeeks());
  }
}
