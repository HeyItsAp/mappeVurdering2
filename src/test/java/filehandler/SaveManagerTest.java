package filehandler;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import ntnu.gruppe21.Exchange;
import ntnu.gruppe21.Player;
import ntnu.gruppe21.Stock;
import ntnu.gruppe21.filehandler.SaveManager;
import ntnu.gruppe21.gameEngine.Difficulty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for SaveManager – verifies that the wrapper correctly delegates to FilehandlerPlayer and
 * FilehandlerExchange.
 */
public class SaveManagerTest {

  private static final String TEST_SLOT = "testsetsaveslot";
  private static final String TEST_GET_SLOT = "testgetsaveslot";

  private Player testPlayer;
  private Exchange testExchange;

  @BeforeEach
  void setUp() {
    testPlayer =
        new Player.Builder("SaveManagerPlayer", new BigDecimal("5000"), Difficulty.EASY)
            .currentMoney(new BigDecimal("3000"))
            .build();
    testPlayer.getChallengeManager().generateChallenges(testPlayer);

    testExchange =
        new Exchange.Builder("SaveManagerExchange")
            .stockMap(List.of(new Stock("SMT", "SaveManagerCo", new BigDecimal("100"))))
            .difficulty(Difficulty.EASY)
            .build();
  }

  // ── constructor ─────────────────────────────────────────────────────────

  @Nested
  class constructor {
    /* Standard constructor should resolve to saves_root/slotName internally. */
    @Test
    void standardConstructorResolvesCorrectly() throws Exception {
      SaveManager sm = new SaveManager(TEST_SLOT);
      // If save works without throwing, the path was resolved correctly
      assertDoesNotThrow(() -> sm.save(testPlayer, testExchange));
    }

    /* fullPath constructor should use the path exactly as given. */
    @Test
    void fullPathConstructorUsesPathAsIs(@TempDir Path tempDir) throws Exception {
      SaveManager sm = new SaveManager(tempDir.toString(), true);
      assertDoesNotThrow(() -> sm.save(testPlayer, testExchange));
      assertTrue(Files.exists(tempDir.resolve("player.csv")));
      assertTrue(Files.exists(tempDir.resolve("exchangeData.csv")));
    }
  }

  // ── save ────────────────────────────────────────────────────────────────

  @Nested
  class save {
    /* save() should create the slot folder if it does not exist. */
    @Test
    void saveCreatesFolderIfMissing(@TempDir Path tempDir) throws Exception {
      Path newSlot = tempDir.resolve("brandNewSlot");
      SaveManager sm = new SaveManager(newSlot.toString(), true);

      sm.save(testPlayer, testExchange);

      assertTrue(Files.exists(newSlot));
    }

    /* save() should produce both player.csv and exchangeData.csv in the slot folder. */
    @Test
    void saveProdicesBothCsvFiles(@TempDir Path tempDir) throws Exception {
      SaveManager sm = new SaveManager(tempDir.toString(), true);

      sm.save(testPlayer, testExchange);

      assertTrue(Files.exists(tempDir.resolve("player.csv")), "player.csv missing");
      assertTrue(Files.exists(tempDir.resolve("exchangeData.csv")), "exchangeData.csv missing");
    }

    /* Calling save() twice should overwrite cleanly without throwing. */
    @Test
    void saveOverwriteDoesNotThrow(@TempDir Path tempDir) throws Exception {
      SaveManager sm = new SaveManager(tempDir.toString(), true);

      assertDoesNotThrow(
              () -> {
                sm.save(testPlayer, testExchange);
                sm.save(testPlayer, testExchange);
              });
    }
  }
  // ── loadPlayer ──────────────────────────────────────────────────────────

  @Nested
  class loadPlayer {
    /* loadPlayer() after save() should return a non-null Player. */
    @Test
    void loadPlayerReturnsNonNull() throws Exception {
      SaveManager sm = new SaveManager("testsetsaveslot", true);
      sm.save(testPlayer, testExchange);

      assertNotNull(sm.loadPlayer());
    }

    /* loadPlayer() should preserve name, money and difficulty after round-trip. */
    @Test
    void loadPlayerPreservesMetadata() throws Exception {
      SaveManager sm = new SaveManager("testsetsaveslot", true);
      sm.save(testPlayer, testExchange);

      Player loaded = sm.loadPlayer();

      assertEquals("SaveManagerPlayer", loaded.getName());
      assertEquals(0, loaded.getStartingMoney().compareTo(new BigDecimal("5000")));
      assertEquals(0, loaded.getCurrentMoney().compareTo(new BigDecimal("3000")));
      assertEquals(Difficulty.EASY, loaded.getDifficulty());
    }

    /* loadPlayer() on a slot with no player.csv should throw RuntimeException. */
    @Test
    void loadPlayerThrowsWhenNoFile(@TempDir Path tempDir) {
      SaveManager sm = new SaveManager(tempDir.toString(), true);
      // no save() called – file does not exist
      assertThrows(RuntimeException.class, sm::loadPlayer);
    }
  }
  // ── loadExchange ────────────────────────────────────────────────────────

  @Nested
  class loadExchange {
    /* loadExchange() after save() should return a non-null Exchange. */
    @Test
    void loadExchangeReturnsNonNull() throws Exception {
      SaveManager sm = new SaveManager("testsetsaveslot", true);
      sm.save(testPlayer, testExchange);

      assertNotNull(sm.loadExchange());
    }

    /* loadExchange() should preserve exchange name and difficulty after round-trip. */
    @Test
    void loadExchangePreservesMetadata() throws Exception {
      SaveManager sm = new SaveManager("testsetsaveslot", true);
      sm.save(testPlayer, testExchange);

      Exchange loaded = sm.loadExchange();

      assertEquals("SaveManagerExchange", loaded.getName());
      assertEquals(Difficulty.EASY, loaded.getDifficulty());
    }

    /* loadExchange() on a slot with no exchangeData.csv should return null or throw. */
    @Test
    void loadExchangeThrowsWhenNoFile(@TempDir Path tempDir) {
      SaveManager sm = new SaveManager(tempDir.toString(), true);
      assertThrows(RuntimeException.class, sm::loadExchange);
    }
  }

  // ── getSaveOptions / getDataSetOptions ───────────────────────────────────
  @Nested
  class getOptions {
    /* getSaveOptions() should return a non-null list. */
    @Test
    void getSaveOptionsReturnsNonNull() {
      SaveManager sm = new SaveManager(TEST_SLOT);
      assertNotNull(sm.getSaveOptions());
    }

    /* getSaveOptions() should contain the known static test slot. */
    @Test
    void getSaveOptionsContainsKnownSlot() {
      SaveManager sm = new SaveManager(TEST_SLOT);
      assertTrue(sm.getSaveOptions().contains(TEST_GET_SLOT));
    }

    /* getDataSetOptions() should return a non-null non-empty list. */
    @Test
    void getDataSetOptionsReturnsNonEmpty() {
      SaveManager sm = new SaveManager(TEST_SLOT);
      assertFalse(sm.getDataSetOptions().isEmpty());
    }
  }
}
