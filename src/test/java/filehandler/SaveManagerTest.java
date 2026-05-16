package filehandler;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import ntnu.gruppe21.Exchange;
import ntnu.gruppe21.Player;
import ntnu.gruppe21.Portfolio;
import ntnu.gruppe21.TransactionArchive;
import ntnu.gruppe21.filehandler.SaveManager;
import ntnu.gruppe21.gameEngine.Difficulty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for SaveManager. Not well branched and developed as previous FilehandlerTest, as this uses
 * the same methods. Only unique logic is making a folder. Coordinator logic is tested with
 * in-memory fakes (no filesystem). Filesystem behavior is tested with @TempDir.
 */
public class SaveManagerTest {
  // ── Helpers ──────────────────────────────────────────────────────────────

  private Player dummyPlayer() {
    return new Player(
        "TestPlayer",
        new BigDecimal("5000"),
        new BigDecimal("3000"),
        new Portfolio(),
        new TransactionArchive(),
        Difficulty.EASY);
  }

  private Exchange dummyExchange() {
    return new Exchange("TestExchange", List.of());
  }

  /* save() should create the save folder if it doesn't exist yet */
  @Test
  public void save_createsFolderIfNotExists(@TempDir Path tempDir) throws Exception {

    Path saveSlot = tempDir.resolve("slot_01");
    SaveManager manager = new SaveManager(saveSlot.toString(), true);

    manager.save(dummyPlayer(), dummyExchange());

    assertTrue(saveSlot.toFile().exists());
  }

  /* Calling save() twice should not throw — folder already exists on second call */
  @Test
  public void save_doesNotThrow_whenFolderAlreadyExists(@TempDir Path tempDir) throws Exception {
    Path saveSlot = tempDir.resolve("slot_01");
    SaveManager manager = new SaveManager(saveSlot.toString(), true);

    manager.save(dummyPlayer(), dummyExchange());

    assertDoesNotThrow(() -> manager.save(dummyPlayer(), dummyExchange()));
  }
}
