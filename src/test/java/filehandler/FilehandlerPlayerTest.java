package filehandler;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import ntnu.gruppe21.*;
import ntnu.gruppe21.filehandler.FilehandlerExchange;
import ntnu.gruppe21.filehandler.FilehandlerPlayer;
import ntnu.gruppe21.gameEngine.Difficulty;
import ntnu.gruppe21.transaction.Purchase;
import ntnu.gruppe21.transaction.Sale;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Filehandler class.
 *
 * <p>Covers loading exchange data from CSV, loading saved exchange data, and the save/reload
 * round-trip.
 *
 * @author Adrian Balunan
 */
public class FilehandlerPlayerTest {
  // First root is subject to change
  private final String test_saves_root = "src/main/resources/saves/testsetsaveslot";
  // Second root will have static data
  private final String test_get_saves_root = "src/main/resources/saves/testgetsaveslot";

  // ── getPlayerSavedData ──────────────────────────────────────────────────────────

  /* Loading a known save file should return a non-null Player. */
  @Test
  public void getSaveDataReturnsNonNull() {
    assertNotNull(FilehandlerPlayer.getPlayerSavedData(test_get_saves_root));
  }

  /* The save file has 3 stocks; the returned Exchange should contain exactly 3. */
  @Test
  public void getSaveDataReturnsCorrectPortfolioAndTransactions() {
    Player savedPlayer = FilehandlerPlayer.getPlayerSavedData(test_get_saves_root);
    assertEquals(3, savedPlayer.getPortfolio().getShares().size());
    assertEquals(3, savedPlayer.getTransactionArchive().getPurchases().size());
    assertEquals(1, savedPlayer.getTransactionArchive().getSales().size());
  }

  /* The Player attributes should be the same as spesified in file */
  @Test
  public void getSaveDataExchangeNameContainsFilename() {
    Player savedPlayer = FilehandlerPlayer.getPlayerSavedData(test_get_saves_root);
    assertEquals("Adrian", savedPlayer.getName());
    assertEquals(new BigDecimal("20000.0"), savedPlayer.getStartingMoney());
    assertEquals(new BigDecimal("8267.46719"), savedPlayer.getCurrentMoney());
  }

  // ── savePlayerData ───────────────────────────────────────────────────────────

  /* Saving a player should return true on a valid path */
  @Test
  public void savePlayerDataReturnsTrueOnValidPath() {
    Player player =
        new Player.Builder("TestPlayer", new BigDecimal("5000"), Difficulty.EASY)
            .currentMoney(new BigDecimal("3000"))
            .build();

    boolean success = FilehandlerPlayer.savePlayerData(player, test_saves_root);
    assertTrue(success);
  }

  /* Saving a player to an invalid path should return false */
  @Test
  public void savePlayerDataReturnsFalseOnInvalidPath() {
    Player player =
        new Player.Builder("TestPlayer", new BigDecimal("5000"), Difficulty.EASY)
            .currentMoney(new BigDecimal("3000"))
            .build();

    boolean success = FilehandlerPlayer.savePlayerData(player, "not/valid/path");
    assertFalse(success);
  }

  /* Saving and reloading should preserve player name, money and difficulty */
  @Test
  public void saveAndReloadPreservesMetadata() {
    Player original =
        new Player.Builder("RoundTrip", new BigDecimal("5000"), Difficulty.EASY)
            .currentMoney(new BigDecimal("3000"))
            .build();

    FilehandlerPlayer.savePlayerData(original, test_saves_root);
    Player loaded = FilehandlerPlayer.getPlayerSavedData(test_saves_root);

    assertEquals("RoundTrip", loaded.getName());
    assertEquals(0, loaded.getStartingMoney().compareTo(new BigDecimal("5000")));
    assertEquals(0, loaded.getCurrentMoney().compareTo(new BigDecimal("3000")));
    assertEquals(Difficulty.EASY, loaded.getDifficulty());
  }

  /* Saving and reloading should preserve portfolio shares */
  @Test
  public void saveAndReloadPreservesPortfolio() {
    Portfolio portfolio = new Portfolio();
    Stock stock = new Stock("TST", "TestCo", new BigDecimal("100"));
    portfolio.addShare(new Share(stock, new BigDecimal("5"), new BigDecimal("100")));

    Player original =
        new Player.Builder("PortfolioTest", new BigDecimal("5000"), Difficulty.EASY)
            .currentMoney(new BigDecimal("3000"))
            .portfolio(portfolio)
            .build();
    FilehandlerPlayer.savePlayerData(original, test_saves_root);
    Player loaded = FilehandlerPlayer.getPlayerSavedData(test_saves_root);

    assertEquals(1, loaded.getPortfolio().getShares().size());
  }

  /* Saving and reloading should preserve purchase transaction count */
  @Test
  public void saveAndReloadPreservesPurchases() {
    TransactionArchive archive = new TransactionArchive();
    Stock stock = new Stock("TST", "TestCo", new BigDecimal("100"));
    Share share = new Share(stock, new BigDecimal("2"), new BigDecimal("100"));
    archive.add(new Purchase(share, 1));

    Player original =
        new Player.Builder("PurchaseTest", new BigDecimal("5000"), Difficulty.EASY)
            .currentMoney(new BigDecimal("3000"))
            .transactionArchive(archive)
            .build();

    FilehandlerPlayer.savePlayerData(original, test_saves_root);
    Player loaded = FilehandlerPlayer.getPlayerSavedData(test_saves_root);

    assertEquals(1, loaded.getTransactionArchive().getPurchases().size());
  }

  /* Saving and reloading should preserve sale transaction count */
  @Test
  public void saveAndReloadPreservesSales() {
    TransactionArchive archive = new TransactionArchive();
    Stock stock = new Stock("TST", "TestCo", new BigDecimal("100"));
    Share share = new Share(stock, new BigDecimal("2"), new BigDecimal("100"));
    archive.add(new Sale(share, 3));

    Player original =
        new Player.Builder("SaleTest", new BigDecimal("5000"), Difficulty.EASY)
            .currentMoney(new BigDecimal("3000"))
            .transactionArchive(archive)
            .build();
    FilehandlerPlayer.savePlayerData(original, test_saves_root);
    Player loaded = FilehandlerPlayer.getPlayerSavedData(test_saves_root);

    assertEquals(1, loaded.getTransactionArchive().getSales().size());
  }

  /* Loading from an invalid path should return null (or handle gracefully) */
  @Test
  public void getPlayerSavedDataReturnsNullOnInvalidPath() {
    assertThrows(
        (RuntimeException.class),
        () -> {
          Player result = FilehandlerPlayer.getPlayerSavedData("not/valid/path");
        });
  }

  @Test
  void gettingPlayerSaveOptionsShouldBeCorrect() {
    assertEquals(2, FilehandlerPlayer.getPlayerSaveOptions().size());
    assertTrue(FilehandlerExchange.getExchangeDatasetOptions().contains("testgetsaveslot"));
    assertTrue(FilehandlerExchange.getExchangeDatasetOptions().contains("testsetsaveslot"));
  }
}
