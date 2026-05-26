import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import ntnu.gruppe21.model.Exchange;
import ntnu.gruppe21.model.Player;
import ntnu.gruppe21.model.Share;
import ntnu.gruppe21.model.Stock;
import ntnu.gruppe21.model.gameEngine.Difficulty;
import ntnu.gruppe21.model.transaction.Purchase;
import ntnu.gruppe21.model.transaction.Sale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Exchange class.
 *
 * <p>The tests currently focus on basic functionality and expected behavior of the Exchange class.
 *
 * @author Adrian Balunan
 */
public class ExchangeTest {
  private Exchange exchange;
  private Stock stock1;

  /**
   * Sets up the test environment before each test case is executed. This includes creating a new
   * Exchange instance with a predefined list of stocks.
   */
  @BeforeEach
  public void setup() {
    stock1 = new Stock("Bit", "Company1", new BigDecimal(1000));
    Stock stock2 = new Stock("DogeCoin", "Company2", new BigDecimal(10));
    Stock stock3 = new Stock("DopeCoin", "Company3", new BigDecimal(500));
    List<Stock> stocks = new ArrayList<>();
    stocks.add(stock1);
    stocks.add(stock2);
    stocks.add(stock3);
    exchange = new Exchange.Builder("Oslo Børs").stockMap(stocks).build();
  }

  /**
   * The following test cases verify the functionality of the Exchange class, including: - Getter
   * methods for name and week.
   */
  @Test
  public void getterNameWorks() {
    assertEquals("Oslo Børs", exchange.getName());
  }

  /* Getter method for week, ensuring it returns the correct initial value. */
  @Test
  public void getterWeekWorks() {
    assertEquals(1, exchange.getWeek());
  }

  /* Method to retrieve a stock by its symbol, ensuring it returns the correct Stock object. */
  @Test
  public void getStockWithSymbolStockReturns() {
    assertEquals(stock1.getSymbol(), exchange.getStock("Bit").getSymbol());
  }

  /* Method to check if a stock exists by its symbol, ensuring it returns true for existing stocks. */
  @Test
  public void hasStockWorks() {
    assertTrue(exchange.hasStock("Bit"));
  }

  /* Method to throw an exception if a stock is not found by its symbol. */
  @Test
  public void getStockWithSymbolThrowsExceptionIfNotFound() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          exchange.getStock("SymbolThatDoesNotExist");
        });
  }

  /* Method to find stocks by a partial symbol name, ensuring it returns the correct list of Stock objects. */
  @Test
  public void findStockWorks() {
    List<Stock> expectedResult = new ArrayList<>();
    expectedResult.add(stock1);
    assertEquals(stock1.getCompany(), exchange.findStock("Bi").getFirst().getCompany());
  }

  /* Method to return an empty list if no stocks match the partial symbol name. */
  @Test
  public void findStockReturnsEmptyInFailure() {
    List<Stock> expectedResult = new ArrayList<>();
    assertEquals(expectedResult, exchange.findStock("StockDoesNotExist"));
  }

  /* Method to return a Purchase object when a player buys a stock. */
  @Test
  public void buyingReturnsCorrectType() {
    Player player = new Player.Builder("Name1", new BigDecimal(10000000), Difficulty.EASY).build();
    assertInstanceOf(Purchase.class, exchange.buy("Bit", new BigDecimal(10), player));
  }

  /* Method to throw an exception if a player tries to buy a stock without having enough money. */
  @Test
  public void buyingThrowsExceptionIfNotEnoughMoney() {
    Share share = new Share(stock1, new BigDecimal(10), stock1.getSalesPrice());
    Purchase purchase = new Purchase(share, 1);
    Player player = new Player.Builder("Name1", new BigDecimal(1), Difficulty.EASY).build();

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          exchange.buy("Bit", new BigDecimal(10), player);
        });
  }

  /* Method to return a Sale object when a player sells a stock. */
  @Test
  public void saleReturnsCorrectType() {
    Player player = new Player.Builder("Name1", new BigDecimal(10000000), Difficulty.EASY).build();
    assertInstanceOf(Sale.class, exchange.sell("Bit", new BigDecimal(10), player));
  }

  /* Method to throw an exception if a player tries to sell a stock they do not own. */
  @Test
  public void advanceWorks() {
    java.math.BigDecimal oldPrice = exchange.getStock("Bit").getSalesPrice();
    exchange.advance();
    assertNotEquals(oldPrice, exchange.getStock("Bit").getSalesPrice());
    assertEquals(2, exchange.getWeek());
  }

  /* Method to see if it returns correct amount of stocks according to limit.*/
  @Test
  public void getGainersReturnsCorrectNumberOfStocks() {
    exchange.advance();
    List<Stock> gainers = exchange.getGainers(2);
    assertEquals(2, gainers.size());
  }

  /* Method that sees if getGainers sorts correctly.*/
  @Test
  public void getGainersReturnsSortedDescending() {
    exchange.advance();
    List<Stock> gainers = exchange.getGainers(3);
    for (int i = 0; i < gainers.size() - 1; i++) {
      assertTrue(
          gainers.get(i).getLatestPriceChange().compareTo(gainers.get(i + 1).getLatestPriceChange())
              >= 0);
    }
  }

  /* Method to throw an exception if limit exceeds amount of total stocks. */
  @Test
  public void getGainersThrowsExceptionIfLimitExceedsStockCount() {
    assertThrows(IllegalArgumentException.class, () -> exchange.getGainers(100));
  }

  /* Method to see if it returns correct amount of stocks according to limit.*/
  @Test
  public void getLosersReturnsCorrectNumberOfStocks() {
    exchange.advance();
    List<Stock> losers = exchange.getLosers(2);
    assertEquals(2, losers.size());
  }

  /* Method that sees if getLosers sorts correctly.*/
  @Test
  public void getLosersReturnsSortedAscending() {
    exchange.advance();
    List<Stock> losers = exchange.getLosers(3);
    for (int i = 0; i < losers.size() - 1; i++) {
      assertTrue(
          losers.get(i).getLatestPriceChange().compareTo(losers.get(i + 1).getLatestPriceChange())
              <= 0);
    }
  }

  /* Method to throw an exception if limit exceeds amount of total stocks. */
  @Test
  public void getLosersThrowsExceptionIfLimitExceedsStockCount() {
    assertThrows(IllegalArgumentException.class, () -> exchange.getLosers(100));
  }
}
