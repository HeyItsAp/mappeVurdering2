package filehandler;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import ntnu.gruppe21.Exchange;
import ntnu.gruppe21.Stock;
import ntnu.gruppe21.filehandler.FilehandlerExchange;
import ntnu.gruppe21.gameEngine.Difficulty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for the FilehandlerExchangeClass.
 *
 * <p>Covers loading exchange data from CSV, loading saved exchange data, and the save/reload
 * round-trip.
 *
 * @author Adrian Balunan
 */
public class FilehandlerExchangeTest {
  // First root is subject to change
  private final String test_saves_root = "src/main/resources/saves/testsetsaveslot";
  // Second root will have static data
  private final String test_get_saves_root = "src/main/resources/saves/testgetsaveslot";
  private final String defaultDatasetName = "exchangeDataSet1";

  // ── getExchangeData ──────────────────────────────────────────────────────

  /* Loading the default exchange file should return a non-null Exchange. */
  @Test
  public void getExchangeDataReturnsNonNull() {
    assertNotNull(FilehandlerExchange.getExchangeDataset(defaultDatasetName));
  }

  /* The exchange file has 3 data rows; the returned Exchange should contain exactly 3 stocks. */
  @Test
  public void getExchangeDataReturnsCorrectNumberOfStocks() {
    assertEquals(5, FilehandlerExchange.getExchangeDataset(defaultDatasetName).getStockMap().size());
  }

  /* The exchange should be named "ExchangeFromFile" as defined in the method. */
  @Test
  public void getExchangeDataHasCorrectName() {
    assertEquals("ExchangeFromFile", FilehandlerExchange.getExchangeDataset(defaultDatasetName).getName());
  }

  /* A known stock from the file should exist with the correct price. */
  @Test
  public void getExchangeDataContainsNvidiaWithCorrectPrice() {
    Exchange exchange = FilehandlerExchange.getExchangeDataset(defaultDatasetName);
    assertTrue(exchange.hasStock("NVDA"));
    assertEquals(0, exchange.getStock("NVDA").getSalesPrice().compareTo(new BigDecimal("191.27")));
  }

  /* Comment lines (starting with '#') must not be parsed as stocks. */
  @Test
  public void getExchangeDataIgnoresCommentLines() {
    Exchange exchange = FilehandlerExchange.getExchangeDataset(defaultDatasetName);
    assertFalse(exchange.hasStock("#"));
  }

  // ── getSaveData ──────────────────────────────────────────────────────────

  /* Loading a known save file should return a non-null Exchange. */
  @Test
  public void getSaveDataReturnsNonNull() {
    assertNotNull(FilehandlerExchange.getSaveData(test_get_saves_root));
  }

  /* The save file has 3 stocks; the returned Exchange should contain exactly 3. */
  @Test
  public void getSaveDataReturnsCorrectNumberOfStocks() {
    assertEquals(3, FilehandlerExchange.getSaveData(test_get_saves_root).getStockMap().size());
  }

  /* MSFT in the save file has two prices (404.68;312.12), so price history size should be 2. */
  @Test
  public void getSaveDataReconstructsFullPriceHistory() {
    Exchange exchange = FilehandlerExchange.getSaveData(test_get_saves_root);
    assertEquals(2, exchange.getStock("MSFT").getPriceHistory().size());
  }

  /* The current sales price should be the last price in the saved history, not the first. */
  @Test
  public void getSaveDataUsesLatestPriceAsSalesPrice() {
    Exchange exchange = FilehandlerExchange.getSaveData(test_get_saves_root);
    // MSFT: 404.68;312.12 — latest (current) price is 312.12
    assertEquals(0, exchange.getStock("MSFT").getSalesPrice().compareTo(new BigDecimal("312.12")));
  }

  /* The exchange metadata is preserved */
  @Test
  public void getSaveDataExchangeNameContainsFilenameAndDifficulty() {
    Exchange exchange = FilehandlerExchange.getSaveData(test_get_saves_root);
    assertEquals("HistoryTest", exchange.getName());
    assertEquals(Difficulty.EASY, exchange.getDifficulty());
  }

  // ── saveExchangeData ─────────────────────────────────────────────────────

  /* If it succseeds it will be true*/
  @Test
  public void saveExchangeDataReturnsCorrect() {
    List<Stock> stocks = new ArrayList<>();
    stocks.add(new Stock("TST", "TestCo", new BigDecimal("100")));
    Exchange exchange = new Exchange.Builder("SaveTest")
            .stockMap(stocks)
            .difficulty(Difficulty.EASY)
            .build();

    Boolean success = FilehandlerExchange.saveExchangeData(exchange, test_saves_root);
    assertTrue(success);
  }

  /* If it fails, returns false*/
  @Test
  public void saveExchangeDataReturnsFalseWhenNot() {

    Exchange exchange = new Exchange.Builder("SaveTest")
            .stockMap(List.of())
            .build();

    Boolean success = FilehandlerExchange.saveExchangeData(exchange, "not/valid/path");
    assertFalse(success);
  }

  /* Saving an exchange and reloading it should preserve all stocks. */
  @Test
  public void saveAndReloadPreservesStocks() {
    List<Stock> stocks = new ArrayList<>();
    stocks.add(new Stock("RRT", "RoundTripCo", new BigDecimal("75.00")));
    Exchange exchange = new Exchange.Builder("RoundTrip")
            .stockMap(stocks)
            .difficulty(Difficulty.HARD)
            .build();

    boolean success = FilehandlerExchange.saveExchangeData(exchange, test_saves_root);
    Exchange loaded = FilehandlerExchange.getSaveData(test_saves_root);

    assertTrue(success);
    assertTrue(loaded.hasStock("RRT"));
    assertEquals(0, loaded.getStock("RRT").getSalesPrice().compareTo(new BigDecimal("75.00")));
  }

  /* Saving an exchange and reloading it should reconstruct the full price history. */
  @Test
  public void saveAndReloadPreservesPriceHistory() {
    Stock stock = new Stock("HST", "HistoryCo", new BigDecimal("100"));
    stock.addNewSalesPrice(new BigDecimal("110"));
    stock.addNewSalesPrice(new BigDecimal("120"));
    List<Stock> stocks = new ArrayList<>();
    stocks.add(stock);
    Exchange exchange = new Exchange.Builder("HistoryTest")
            .stockMap(stocks)
            .difficulty(Difficulty.MEDIUM)
            .build();

    Boolean success = FilehandlerExchange.saveExchangeData(exchange, test_saves_root);
    Exchange loaded = FilehandlerExchange.getSaveData(test_saves_root);

    assertEquals(3, loaded.getStock("HST").getPriceHistory().size());
    assertEquals(0, loaded.getStock("HST").getSalesPrice().compareTo(new BigDecimal("120")));
  }

  // ── validFormat ─────────────────────────────────────────────────────

  @Test
  void validFormat_returnsFalse_whenPriceIsNotANumber(@TempDir Path tempDir) throws IOException {
    Path csv = tempDir.resolve("bad.csv");
    Files.write(
        csv,
        List.of(
            "# comment",
            "First,TestCo,100.00",
            "TST,TestCo,21.0",
            "TST,TestCo,21.0",
            "TST,TestCo,21.0",
            "TST,TestCo,notanumber"));

    assertFalse(FilehandlerExchange.validFormat(csv));
  }

  @Test
  void validFormat_returnsFalse_whenTooFewColumnsOrTooMany(@TempDir Path tempDir)
      throws IOException {
    Path csv = tempDir.resolve("bad.csv");
    Files.write(
        csv,
        List.of(
            "# comment",
            "First,TestCo,100.00",
            "TST,TestCo",
            "TST,TestCo,21.0",
            "TST,TestCo,21.0",
            "TST,TestCo,21.0"));

    assertFalse(FilehandlerExchange.validFormat(csv));

    Path csv2 = tempDir.resolve("bad2.csv");
    Files.write(
        csv,
        List.of(
            "# comment",
            "First,TestCo,100.00,week1",
            "TST,TestCo,21.0",
            "TST,TestCo,21.0",
            "TST,TestCo,21.0",
            "TST,TestCo,21.0"));

    assertFalse(FilehandlerExchange.validFormat(csv));
  }

  @Test
  void validFormat_returnsFalse_whenTooFewRows(@TempDir Path tempDir) throws IOException {
    Path csv = tempDir.resolve("bad.csv");
    Files.write(csv, List.of("# comment", "TST,TestCo,21.0", "TST,TestCo,21.0", "TST,TestCo,21.0"));
    assertFalse(FilehandlerExchange.validFormat(csv));
  }

  @Test
  void validFormat_returnsTrue_whenFileIsValid(@TempDir Path tempDir) throws IOException {
    Path csv = tempDir.resolve("good.csv");
    Files.write(
        csv,
        List.of(
            "# comment",
            "First,TestCo,100.00",
            "TST,TestCo,21.0",
            "TST,TestCo,21.0",
            "TST,TestCo,21.0",
            "TST,TestCo,21.0"));

    assertTrue(FilehandlerExchange.validFormat(csv));
  }
}
