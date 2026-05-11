import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import ntnu.gruppe21.Exchange;
import ntnu.gruppe21.Stock;
import ntnu.gruppe21.filehandler.FilehandlerExchange;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Filehandler class.
 *
 * <p>Covers loading exchange data from CSV, loading saved exchange data, and the save/reload
 * round-trip.
 *
 * @author Adrian Balunan
 */
public class FilehandlerTest {

  // ── getExchangeData ──────────────────────────────────────────────────────

  /* Loading the default exchange file should return a non-null Exchange. */
  @Test
  public void getExchangeDataReturnsNonNull() {
    assertNotNull(FilehandlerExchange.getExchangeData());
  }

  /* The exchange file has 3 data rows; the returned Exchange should contain exactly 3 stocks. */
  @Test
  public void getExchangeDataReturnsCorrectNumberOfStocks() {
    assertEquals(3, FilehandlerExchange.getExchangeData().getStockMap().size());
  }

  /* The exchange should be named "ExchangeFromFile" as defined in the method. */
  @Test
  public void getExchangeDataHasCorrectName() {
    assertEquals("ExchangeFromFile", FilehandlerExchange.getExchangeData().getName());
  }

  /* A known stock from the file should exist with the correct price. */
  @Test
  public void getExchangeDataContainsNvidiaWithCorrectPrice() {
    Exchange exchange = FilehandlerExchange.getExchangeData();
    assertTrue(exchange.hasStock("NVDA"));
    assertEquals(0, exchange.getStock("NVDA").getSalesPrice().compareTo(new BigDecimal("191.27")));
  }

  /* Comment lines (starting with '#') must not be parsed as stocks. */
  @Test
  public void getExchangeDataIgnoresCommentLines() {
    Exchange exchange = FilehandlerExchange.getExchangeData();
    assertFalse(exchange.hasStock("#"));
  }

  // ── getSaveData ──────────────────────────────────────────────────────────

  /* Loading a known save file should return a non-null Exchange. */
  @Test
  public void getSaveDataReturnsNonNull() {
    assertNotNull(FilehandlerExchange.getSaveData("saveDataExhangeFromFile1"));
  }

  /* The save file has 3 stocks; the returned Exchange should contain exactly 3. */
  @Test
  public void getSaveDataReturnsCorrectNumberOfStocks() {
    assertEquals(3, FilehandlerExchange.getSaveData("saveDataExhangeFromFile1").getStockMap().size());
  }

  /* MSFT in the save file has two prices (404.68;312.12), so price history size should be 2. */
  @Test
  public void getSaveDataReconstructsFullPriceHistory() {
    Exchange exchange = FilehandlerExchange.getSaveData("saveDataExhangeFromFile1");
    assertEquals(2, exchange.getStock("MSFT").getPriceHistory().size());
  }

  /* The current sales price should be the last price in the saved history, not the first. */
  @Test
  public void getSaveDataUsesLatestPriceAsSalesPrice() {
    Exchange exchange = FilehandlerExchange.getSaveData("saveDataExhangeFromFile1");
    // MSFT: 404.68;312.12 — latest (current) price is 312.12
    assertEquals(0, exchange.getStock("MSFT").getSalesPrice().compareTo(new BigDecimal("312.12")));
  }

  /* The exchange name should contain the filename so save files are identifiable. */
  @Test
  public void getSaveDataExchangeNameContainsFilename() {
    Exchange exchange = FilehandlerExchange.getSaveData("saveDataExhangeFromFile1");
    assertTrue(exchange.getName().contains("saveDataExhangeFromFile1"));
  }

  // ── saveExchangeData ─────────────────────────────────────────────────────

  /* The returned filename should follow the pattern saveData{name}{week}. */
  @Test
  public void saveExchangeDataReturnsCorrectFilename() {
    List<Stock> stocks = new ArrayList<>();
    stocks.add(new Stock("TST", "TestCo", new BigDecimal("100")));
    Exchange exchange = new Exchange("SaveTest", stocks);

    String filename = FilehandlerExchange.saveExchangeData(exchange);
    assertEquals("saveDataSaveTest1", filename);

    new File("src/main/resources/saves/" + filename + ".csv").delete();
  }

  /* Saving an exchange and reloading it should preserve all stocks. */
  @Test
  public void saveAndReloadPreservesStocks() {
    List<Stock> stocks = new ArrayList<>();
    stocks.add(new Stock("RRT", "RoundTripCo", new BigDecimal("75.00")));
    Exchange exchange = new Exchange("RoundTrip", stocks);

    String filename = FilehandlerExchange.saveExchangeData(exchange);
    Exchange loaded = FilehandlerExchange.getSaveData(filename);

    assertTrue(loaded.hasStock("RRT"));
    assertEquals(0, loaded.getStock("RRT").getSalesPrice().compareTo(new BigDecimal("75.00")));

    new File("src/main/resources/saves/" + filename + ".csv").delete();
  }

  /* Saving an exchange and reloading it should reconstruct the full price history. */
  @Test
  public void saveAndReloadPreservesPriceHistory() {
    Stock stock = new Stock("HST", "HistoryCo", new BigDecimal("100"));
    stock.addNewSalesPrice(new BigDecimal("110"));
    stock.addNewSalesPrice(new BigDecimal("120"));
    List<Stock> stocks = new ArrayList<>();
    stocks.add(stock);
    Exchange exchange = new Exchange("HistoryTest", stocks);

    String filename = FilehandlerExchange.saveExchangeData(exchange);
    Exchange loaded = FilehandlerExchange.getSaveData(filename);

    assertEquals(3, loaded.getStock("HST").getPriceHistory().size());
    assertEquals(0, loaded.getStock("HST").getSalesPrice().compareTo(new BigDecimal("120")));

    new File("src/main/resources/saves/" + filename + ".csv").delete();
  }
}
