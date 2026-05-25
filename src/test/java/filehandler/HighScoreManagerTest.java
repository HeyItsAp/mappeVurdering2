package filehandler;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import ntnu.gruppe21.Exchange;
import ntnu.gruppe21.Player;
import ntnu.gruppe21.Stock;
import ntnu.gruppe21.filehandler.HighScoreManager;
import ntnu.gruppe21.gameEngine.Difficulty;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

class HighScoreManagerTest {

  @TempDir Path tempDir;

  // A minimal stock list — Exchange requires at least one stock to construct
  private static final List<Stock> DUMMY_STOCKS =
      List.of(new Stock("DUMMY", "Dummy Corp", new BigDecimal("100")));

  // ── Helpers ──────────────────────────────────────────────────────────
  /** Returns an Exchange at week 1 (default after construction). */
  private Exchange exchangeAtWeek(int targetWeek) {
    Exchange exchange = new Exchange.Builder("TextExchange").stockMap(DUMMY_STOCKS).build();
    // advance() increments week, so call it (targetWeek - 1) times
    for (int i = 1; i < targetWeek; i++) {
      exchange.advance();
    }
    return exchange;
  }

  /**
   * Returns a Player whose currentMoney + portfolio networth equals {@code fortune}. We keep
   * portfolio empty (worth 0) and put everything in currentMoney.
   */
  private Player playerWithFortune(String name, BigDecimal startingMoney, BigDecimal fortune) {
    Player player = new Player.Builder(name, startingMoney, Difficulty.EASY).build();
    // currentMoney starts at startingMoney; adjust it to reach the desired fortune
    BigDecimal delta = fortune.subtract(startingMoney);
    if (delta.compareTo(BigDecimal.ZERO) >= 0) {
      player.addMoney(delta);
    } else {
      player.withdrawMoney(delta.abs());
    }
    return player;
  }


  // ── calculateFinalScore ───────────────────────────────────────────────────────────

  @Nested
  class calculateFinalScore {
    @Test
    void calculateFinalScore_knownInputs_returnsCorrectValue() {
      Exchange exchange = exchangeAtWeek(10);
      exchange.setDifficulty(Difficulty.HARD);
      // startingMoney=1000, fortune=2000 → profitScore=(2000/1000)*10=20
      // finalScore = 5 * 10 * 20 = 200*5
      Player player = playerWithFortune("Alice", new BigDecimal("1000"), new BigDecimal("2000"));

      BigDecimal result = HighScoreManager.calculateFinalScore(exchange, player);

      assertEquals(0, new BigDecimal("2000").compareTo(result.stripTrailingZeros()));
    }

    @Test
    void calculateFinalScore_doubledWeek_doublesScore() {
      Player player = playerWithFortune("Bob", new BigDecimal("1000"), new BigDecimal("2000"));

      BigDecimal scoreWeek5 = HighScoreManager.calculateFinalScore(exchangeAtWeek(5), player);
      BigDecimal scoreWeek10 = HighScoreManager.calculateFinalScore(exchangeAtWeek(10), player);

      assertEquals(
              0,
              scoreWeek5.multiply(new BigDecimal("2")).compareTo(scoreWeek10.stripTrailingZeros()),
              "Score should double when week doubles");
    }

    @Test
    void calculateFinalScore_negativeNumber_throwsIllegalArgumentException() {
      Exchange exchange = exchangeAtWeek(1);
      // fortune=1, startingMoney=100000 → profitScore tiny → finalScore << 1
      Player player = playerWithFortune("Carol", new BigDecimal("100000"), new BigDecimal("-1"));

      assertThrows(
              IllegalArgumentException.class,
              () -> HighScoreManager.calculateFinalScore(exchange, player));
    }

    @Test
    void calculateFinalScore_scoreExactlyOne_doesNotThrow() {
      // We need: 1 * week * (fortune/starting)*10 = 1
      // week=1, fortune/starting = 0.1 → fortune = starting * 0.1
      // Use starting=10, fortune=1 → profitScore=(1/10)*10=1 → finalScore=1*1*1=1
      Exchange exchange = exchangeAtWeek(1);
      Player player = playerWithFortune("Dave", new BigDecimal("10"), new BigDecimal("1"));

      assertDoesNotThrow(() -> HighScoreManager.calculateFinalScore(exchange, player));
    }
  }

  // ── addFinalScoreToCsv ───────────────────────────────────────────────────────────
  @Nested
  class addFinalScoreToCsv {
    @Test
    void addFinalScoreToCsv_invalidScore_returnsFalse() {
      Exchange exchange = exchangeAtWeek(1);
      Player player = playerWithFortune("Grace", new BigDecimal("100000"), new BigDecimal("-1"));

      boolean result = HighScoreManager.addFinalScoreToCsv(exchange, player);

      assertFalse(result);
    }
  }
}
