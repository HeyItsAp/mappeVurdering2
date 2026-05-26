package gameEngine.challenges;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import ntnu.gruppe21.model.Player;
import ntnu.gruppe21.model.Portfolio;
import ntnu.gruppe21.model.gameEngine.Difficulty;
import ntnu.gruppe21.model.gameEngine.challenges.Challenge;
import ntnu.gruppe21.model.gameEngine.challenges.ChallengeType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Unit tests for {@link Challenge}.
 *
 * <p>Uses Mockito to stub {@link Player} and {@link Portfolio} so tests are isolated from unrelated
 * game-engine logic.
 */
class ChallengeTest {

  // ── Shared helpres ────────────────────────────────────

  /** Returns a stubbed Player with the given money and distinct-stock count. */
  private Player mockPlayer(int money, int distinctStocks, Difficulty difficulty) {
    Player player = mock(Player.class);
    Portfolio portfolio = mock(Portfolio.class);

    when(player.getCurrentMoney()).thenReturn(BigDecimal.valueOf(money));
    when(player.getPortfolio()).thenReturn(portfolio);
    when(player.getDifficulty()).thenReturn(difficulty);
    when(portfolio.countDistinctStock()).thenReturn(distinctStocks);

    return player;
  }

  // ── Construction ────────────────────────────────────

  @Nested
  class ConstructorFresh {

    @Test
    void freshChallenge_isNotCompleted() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.BALANCE_REQUIREMENT, Difficulty.EASY, player);

      assertFalse(challenge.isCompleted());
    }

    @Test
    void freshChallenge_timesCompletedIsZero() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.BALANCE_REQUIREMENT, Difficulty.EASY, player);

      assertEquals(0, challenge.getTimesCompleted());
    }

    @Test
    void freshChallenge_descriptionIsNotNull() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.UNIQUE_SHARE_REQUIREMENT, Difficulty.EASY, player);

      assertNotNull(challenge.getDescription());
      assertFalse(challenge.getDescription().isBlank());
    }

    @Test
    void freshChallenge_challengeTypePreserved() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.BALANCE_REQUIREMENT, Difficulty.MEDIUM, player);

      assertEquals(ChallengeType.BALANCE_REQUIREMENT, challenge.getChallengeType());
    }
  }

  // ── Construction – restored (previously completed N times) ────────────────────────────────────

  @Nested
  class ConstructorRestored {

    @Test
    void restoredChallenge_isNotCompleted() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.BALANCE_REQUIREMENT, Difficulty.EASY, player, 3);

      assertFalse(challenge.isCompleted());
    }

    @Test
    void restoredChallenge_timesCompletedMatchesArgument() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.BALANCE_REQUIREMENT, Difficulty.EASY, player, 3);

      assertEquals(3, challenge.getTimesCompleted());
    }

    @Test
    void restoredChallenge_targetValueHigherThanFresh() {
      // We infer target via checkCompletion: fresh challenge at EASY starts at money/2 = 500.
      // After 3 advances the target should be > 500, so a player with 600 will pass.
      Player startingPlayer = mockPlayer(1000, 0, Difficulty.EASY);
      Player playerAfterSomeAdvances = mockPlayer(520, 0, Difficulty.EASY);

      Challenge fresh =
          new Challenge(ChallengeType.BALANCE_REQUIREMENT, Difficulty.EASY, startingPlayer);
      Challenge restored =
          new Challenge(ChallengeType.BALANCE_REQUIREMENT, Difficulty.EASY, startingPlayer, 5);

      // Fresh challenge: 520 >= 500 → true. Restored: target has been raised 3 times.
      assertTrue(fresh.checkCompletion(playerAfterSomeAdvances));
      // With only 520 and a raised target the restored challenge should NOT be completable.
      assertFalse(restored.checkCompletion(playerAfterSomeAdvances));
    }
  }

  // ── checkCompletion – BALANCE_REQUIREMENT ────────────────────────────────────

  @Nested
  class CheckCompletionBalance {

    @Test
    void belowTarget_returnsFalse() {
      // EASY: target = 1000 / 2 = 500
      Player newplayer = mockPlayer(1000, 0, Difficulty.EASY);
      Player playerWithGameTime = mockPlayer(499, 0, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.BALANCE_REQUIREMENT, Difficulty.EASY, newplayer);

      assertFalse(challenge.checkCompletion(playerWithGameTime));
    }

    @Test
    void atTarget_returnsTrue() {
      // EASY: target = 1000 / 2 = 500
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.BALANCE_REQUIREMENT, Difficulty.EASY, player);

      // Now simulate player with exactly 500
      when(player.getCurrentMoney()).thenReturn(BigDecimal.valueOf(500));
      assertTrue(challenge.checkCompletion(player));
    }

    @Test
    void aboveTarget_returnsTrue() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.BALANCE_REQUIREMENT, Difficulty.EASY, player);

      when(player.getCurrentMoney()).thenReturn(BigDecimal.valueOf(999));
      assertTrue(challenge.checkCompletion(player));
    }

    @Test
    void afterCompletion_isCompletedIsTrue() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.BALANCE_REQUIREMENT, Difficulty.EASY, player);

      when(player.getCurrentMoney()).thenReturn(BigDecimal.valueOf(999));
      challenge.checkCompletion(player);

      assertTrue(challenge.isCompleted());
    }

    @Test
    void failedCheck_isCompletedRemainseFalse() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.BALANCE_REQUIREMENT, Difficulty.EASY, player);

      when(player.getCurrentMoney()).thenReturn(BigDecimal.valueOf(1));
      challenge.checkCompletion(player);

      assertFalse(challenge.isCompleted());
    }

    @ParameterizedTest
    @EnumSource(Difficulty.class)
    void harderDifficulty_hasHigherOrEqualTarget(Difficulty difficulty) {
      // Compare each difficulty's starting target against EASY.
      // EASY: target = startingMoney / 2. Others add a percentage on top.
      Player easyPlayer = mockPlayer(10_000, 0, Difficulty.EASY);
      Player testPlayer = mockPlayer(10_000, 0, difficulty);

      Challenge easy =
          new Challenge(ChallengeType.BALANCE_REQUIREMENT, Difficulty.EASY, easyPlayer);
      Challenge test = new Challenge(ChallengeType.BALANCE_REQUIREMENT, difficulty, testPlayer);

      // Derive targets by checking at EASY's target (5000); it should pass for EASY.
      when(easyPlayer.getCurrentMoney()).thenReturn(BigDecimal.valueOf(5000));
      assertTrue(easy.checkCompletion(easyPlayer));

      // For difficulties other than EASY, the same amount should NOT be enough.
      if (difficulty != Difficulty.EASY) {
        when(testPlayer.getCurrentMoney()).thenReturn(BigDecimal.valueOf(5000));
        assertFalse(test.checkCompletion(testPlayer));
      }
    }
  }

  // ── checkCompletion – UNIQUE_SHARE_REQUIREMENT ────────────────────────────────────
  @Nested
  class CheckCompletionUniqueShare {

    @Test
    void belowTarget_returnsFalse() {
      // EASY base = 2; player has 1 stock
      Player player = mockPlayer(1000, 1, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.UNIQUE_SHARE_REQUIREMENT, Difficulty.EASY, player);

      assertFalse(challenge.checkCompletion(player));
    }

    @Test
    void atTarget_returnsTrue() {
      // EASY base = 2; player has 2 stocks
      Player player = mockPlayer(1000, 2, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.UNIQUE_SHARE_REQUIREMENT, Difficulty.EASY, player);

      assertTrue(challenge.checkCompletion(player));
    }

    @Test
    void hard_requiresMoreUniqueStocksThanEasy() {
      // EASY target = 2, HARD target = 6
      Player player = mockPlayer(1000, 5, Difficulty.HARD);
      Challenge challenge =
          new Challenge(ChallengeType.UNIQUE_SHARE_REQUIREMENT, Difficulty.HARD, player);

      assertFalse(challenge.checkCompletion(player));
    }
  }

  // ── advanceChallenge ───────────────────────────────────-

  @Nested
  class AdvanceChallenge {

    @Test
    void notCompleted_throwsIllegalStateException() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.BALANCE_REQUIREMENT, Difficulty.EASY, player);

      assertThrows(IllegalStateException.class, () -> challenge.advanceChallenge(1, player));
    }

    @Test
    void afterAdvance_isNotCompleted() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.BALANCE_REQUIREMENT, Difficulty.EASY, player);

      when(player.getCurrentMoney()).thenReturn(BigDecimal.valueOf(999));
      challenge.checkCompletion(player); // complete it

      challenge.advanceChallenge(1, player);

      assertFalse(challenge.isCompleted());
    }

    @Test
    void uniqueShare_targetIncreasedByOne() {
      // EASY target = 2. Complete with 2 stocks, advance, then 2 stocks should no longer suffice.
      Player player = mockPlayer(1000, 2, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.UNIQUE_SHARE_REQUIREMENT, Difficulty.EASY, player);

      challenge.checkCompletion(player); // complete (2 >= 2)
      challenge.advanceChallenge(1, player);

      // Target is now 3; player still has 2 → should fail
      assertFalse(challenge.checkCompletion(player));
    }

    @Test
    void balance_targetIncreasedAfterAdvance() {
      // EASY: target starts at 500 (1000/2). Complete with 500, advance, 500 should no longer
      // suffice.
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.BALANCE_REQUIREMENT, Difficulty.EASY, player);

      when(player.getCurrentMoney()).thenReturn(BigDecimal.valueOf(500));
      challenge.checkCompletion(player); // complete (500 >= 500)
      challenge.advanceChallenge(1, player);

      // New target = 500 + 500/100 = 505
      when(player.getCurrentMoney()).thenReturn(BigDecimal.valueOf(500));
      assertFalse(challenge.checkCompletion(player));
    }
  }

  // ── refreshDescription ───────────────────────────────────-

  @Nested
  class RefreshDescription {

    @Test
    void balance_descriptionContainsMoney() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.BALANCE_REQUIREMENT, Difficulty.EASY, player);

      when(player.getCurrentMoney()).thenReturn(BigDecimal.valueOf(750));
      challenge.refreshDescription(player, ChallengeType.BALANCE_REQUIREMENT);

      assertTrue(challenge.getDescription().contains("750"));
    }

    @Test
    void uniqueShare_descriptionContainsStockCount() {
      Player player = mockPlayer(1000, 3, Difficulty.EASY);
      Challenge challenge =
          new Challenge(ChallengeType.UNIQUE_SHARE_REQUIREMENT, Difficulty.EASY, player);

      challenge.refreshDescription(player, ChallengeType.UNIQUE_SHARE_REQUIREMENT);

      assertTrue(challenge.getDescription().contains("3"));
    }
  }
}
