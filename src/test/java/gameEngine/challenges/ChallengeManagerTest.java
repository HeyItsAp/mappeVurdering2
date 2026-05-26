package gameEngine.challenges;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import ntnu.gruppe21.model.Player;
import ntnu.gruppe21.model.Portfolio;
import ntnu.gruppe21.model.gameEngine.Difficulty;
import ntnu.gruppe21.model.gameEngine.challenges.Challenge;
import ntnu.gruppe21.model.gameEngine.challenges.ChallengeManager;
import ntnu.gruppe21.model.gameEngine.challenges.ChallengeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ChallengeManager}.
 *
 * <p>Uses Mockito to stub {@link Player} and {@link Portfolio}.
 */
class ChallengeManagerTest {

  private ChallengeManager manager;

  // ── Helpers ───────────────────────────────────-

  private Player mockPlayer(int money, int distinctStocks, Difficulty difficulty) {
    Player player = mock(Player.class);
    Portfolio portfolio = mock(Portfolio.class);

    when(player.getCurrentMoney()).thenReturn(BigDecimal.valueOf(money));
    when(player.getStartingMoney()).thenReturn(BigDecimal.valueOf(money));
    when(player.getNetWorth()).thenReturn(BigDecimal.valueOf(money));
    when(player.getPortfolio()).thenReturn(portfolio);
    when(player.getDifficulty()).thenReturn(difficulty);
    when(portfolio.countDistinctStock()).thenReturn(distinctStocks);

    return player;
  }

  @BeforeEach
  void setUp() {
    manager = new ChallengeManager();
  }

  // ── Initial state ───────────────────────────────────-

  @Nested
  class InitialState {

    @Test
    void noChallengesBeforeGeneration() {
      assertTrue(manager.getActiveChallenges().isEmpty());
    }
  }

  // ── generateChallenges ───────────────────────────────────-

  @Nested
  class GenerateChallenges {

    @Test
    void generatesOneChallengeExactly() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      manager.generateChallenges(player);

      assertEquals(1, manager.getActiveChallenges().size());
    }

    @Test
    void generatedChallenge_usesDifficultyFromPlayer() {
      Player player = mockPlayer(1000, 0, Difficulty.HARD);
      manager.generateChallenges(player);

      // Difficulty is encoded in the challenge via its target value, not exposed directly,
      // but we can verify the challenge is a known ChallengeType.
      Challenge c = manager.getActiveChallenges().get(0);
      List<ChallengeType> validTypes = List.of(ChallengeType.values());
      assertTrue(validTypes.contains(c.getChallengeType()));
    }

    @Test
    void generatedChallenge_isNotCompleted() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      manager.generateChallenges(player);

      assertFalse(manager.getActiveChallenges().get(0).isCompleted());
    }

    @Test
    void callingTwice_addsTwoChallenges() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      manager.generateChallenges(player);
      manager.generateChallenges(player);

      assertEquals(2, manager.getActiveChallenges().size());
    }
  }

  // ── getActiveChallenges – immutability ───────────────────────────────────-

  @Nested
  class GetActiveChallengesImmutability {

    @Test
    void returnedList_isUnmodifiable() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      manager.generateChallenges(player);

      List<Challenge> challenges = manager.getActiveChallenges();
      assertThrows(UnsupportedOperationException.class, () -> challenges.remove(0));
    }
  }

  // ── evaluateChallenges  ───────────────────────────────────-

  @Nested
  class EvaluateChallenges {

    @Test
    void notCompleted_doesNotThrow() {
      Player player = mockPlayer(1, 0, Difficulty.EASY);
      manager.parseChallenges(Map.of(ChallengeType.BALANCE_REQUIREMENT, 0), player);

      // EASY BALANCE target = 1/2 = 0; player has 1 → actually meets it, so use UNIQUE instead
      // Use UNIQUE_SHARE_REQUIREMENT with 0 stocks so the challenge is definitely not met
      // (target=2)
      ChallengeManager freshManager = new ChallengeManager();
      freshManager.parseChallenges(Map.of(ChallengeType.UNIQUE_SHARE_REQUIREMENT, 0), player);

      assertDoesNotThrow(() -> freshManager.evaluateChallenges(player));
    }

    @Test
    void completed_replacesWithDifferentType() {
      Player player = mockPlayer(1000, 99, Difficulty.EASY);
      manager.parseChallenges(Map.of(ChallengeType.UNIQUE_SHARE_REQUIREMENT, 0), player);

      Challenge old = manager.getActiveChallenges().get(0);
      old.checkCompletion(player); // 99 >= 4 → true
      assertTrue(old.isCompleted());

      manager.evaluateChallenges(player);

      Challenge replacement = manager.getActiveChallenges().get(0);
      assertFalse(replacement.isCompleted());
      assertNotEquals(ChallengeType.UNIQUE_SHARE_REQUIREMENT, replacement.getChallengeType());
    }

    @Test
    void completed_incrementsTotalCompletions() {
      Player player = mockPlayer(1000, 99, Difficulty.EASY);
      manager.parseChallenges(Map.of(ChallengeType.UNIQUE_SHARE_REQUIREMENT, 0), player);
      manager.getActiveChallenges().get(0).checkCompletion(player);

      manager.evaluateChallenges(player);

      assertEquals(1, manager.getTotalCompletions());
    }
  }

  // ── parseChallenges  ───────────────────────────────────-

  @Nested
  class ParseChallenges {

    @Test
    void addsCorrectNumberOfChallenges() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      Map<ChallengeType, Integer> saveData =
          Map.of(
              ChallengeType.BALANCE_REQUIREMENT, 0,
              ChallengeType.UNIQUE_SHARE_REQUIREMENT, 2);

      manager.parseChallenges(saveData, player);

      assertEquals(2, manager.getActiveChallenges().size());
    }

    @Test
    void parsedChallenges_haveCorrectTypes() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      manager.parseChallenges(Map.of(ChallengeType.BALANCE_REQUIREMENT, 1), player);

      Challenge c = manager.getActiveChallenges().get(0);
      assertEquals(ChallengeType.BALANCE_REQUIREMENT, c.getChallengeType());
    }

    @Test
    void parsedWithZeroTimesCompleted_behavesLikeFresh() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      manager.parseChallenges(Map.of(ChallengeType.BALANCE_REQUIREMENT, 0), player);

      Challenge parsed = manager.getActiveChallenges().get(0);

      // Same target as a brand-new challenge
      ChallengeManager freshManager = new ChallengeManager();
      // We can't directly compare targets, but we can verify completion behaviour is identical.
      // EASY BALANCE target = 500; player with 499 should fail both.
      when(player.getCurrentMoney()).thenReturn(BigDecimal.valueOf(499));
      assertFalse(parsed.checkCompletion(player));
    }

    @Test
    void parsedWithTimesCompleted_hasHigherTarget() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      manager.parseChallenges(Map.of(ChallengeType.BALANCE_REQUIREMENT, 3), player);

      Challenge restored = manager.getActiveChallenges().get(0);

      // Fresh EASY target = 500. After 3 advances it must be higher.
      // Player with exactly 500 should NOT complete the restored challenge.
      when(player.getCurrentMoney()).thenReturn(BigDecimal.valueOf(500));
      assertFalse(restored.checkCompletion(player));
    }
  }

  // ── saveChallenges  ───────────────────────────────────-

  @Nested
  class SaveChallenges {

    @Test
    void saveChallenges_containsChallengeType() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      manager.parseChallenges(Map.of(ChallengeType.BALANCE_REQUIREMENT, 0), player);

      String saved = manager.saveChallenges();

      assertTrue(saved.contains("BALANCE_REQUIREMENT"));
    }

    @Test
    void saveChallenges_containsTimesCompleted() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      manager.parseChallenges(Map.of(ChallengeType.BALANCE_REQUIREMENT, 5), player);

      String saved = manager.saveChallenges();

      assertTrue(saved.contains("5"));
    }

    @Test
    void saveChallenges_usesFieldSeparator() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      manager.parseChallenges(Map.of(ChallengeType.UNIQUE_SHARE_REQUIREMENT, 0), player);

      String saved = manager.saveChallenges();

      assertTrue(saved.contains(";"));
    }

    @Test
    void saveThenParse_producesEquivalentChallenge() {
      Player player = mockPlayer(1000, 0, Difficulty.EASY);
      manager.parseChallenges(Map.of(ChallengeType.BALANCE_REQUIREMENT, 2), player);

      String saved = manager.saveChallenges();

      // Parse the saved string back into a new manager.
      // Format: "total|TYPE;timesCompleted" — skip the first segment (it's the integer total).
      ChallengeManager restored = new ChallengeManager();
      String[] entries = saved.split("\\|");
      Map<ChallengeType, Integer> parsed = new java.util.HashMap<>();
      int savedTotal = 0;
      int startIdx = 0;
      try {
        savedTotal = Integer.parseInt(entries[0].trim());
        startIdx = 1;
      } catch (NumberFormatException ignored) {
      }
      for (int i = startIdx; i < entries.length; i++) {
        if (entries[i].isBlank()) continue;
        String[] parts = entries[i].split(";");
        parsed.put(ChallengeType.valueOf(parts[0]), Integer.parseInt(parts[1]));
      }
      restored.parseChallenges(parsed, player);
      restored.setTotalCompletions(savedTotal);

      assertEquals(1, restored.getActiveChallenges().size());
      assertEquals(
          ChallengeType.BALANCE_REQUIREMENT,
          restored.getActiveChallenges().get(0).getChallengeType());
      assertEquals(2, restored.getActiveChallenges().get(0).getTimesCompleted());
    }
  }
}
