import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import ntnu.gruppe21.Portfolio;
import ntnu.gruppe21.Share;
import ntnu.gruppe21.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Portfolio class.
 *
 * <p>The tests currently focus on basic functionality and expected behavior of the Portfolio class.
 *
 * @author Adrian Balunan
 */
public class PortfolioTest {
  private Portfolio portfolio;
  private Stock stock;

  /* Method to set up a Portfolio instance before each test case is executed, ensuring a consistent test environment. */
  @BeforeEach
  public void setup() {
    stock = new Stock("Bit", "ExampleCompany", new BigDecimal(20000));
    portfolio = new Portfolio();
  }

  /* Method to verify that the getShares() method returns an empty list when no shares are added to the portfolio. */
  @Test
  public void gettingSharesShouldWork() {
    assertEquals(new ArrayList<>(), portfolio.getShares());
  }

  /* Method to verify that the addShare() method correctly adds a share to the portfolio and that the getShares() method returns the correct list of shares. */
  @Test
  public void addingSharesShouldWork() {
    Share shareOfStock = new Share(stock, new BigDecimal(1000), new BigDecimal(2531));
    portfolio.addShare(shareOfStock);
    assertEquals(new ArrayList<>(List.of(shareOfStock)), portfolio.getShares());
  }

  /* Method to verify that the removeShare() method correctly removes a share from the portfolio and that the getShares() method returns the correct list of shares. */
  @Test
  public void removingSharesShouldWork() {
    Share shareOfStock = new Share(stock, new BigDecimal(1000), new BigDecimal(2531));
    Share share2OfStock = new Share(stock, new BigDecimal(2000), new BigDecimal(5062));
    portfolio.addShare(shareOfStock);
    portfolio.addShare(share2OfStock);
    portfolio.removeShare(shareOfStock);
    assertEquals(new ArrayList<>(List.of(share2OfStock)), portfolio.getShares());
  }

  /* Method to verify that the containsShare() method correctly identifies whether a share is present in the portfolio. */
  @Test
  public void containSearchShouldWork() {
    Share shareOfStock = new Share(stock, new BigDecimal(1000), new BigDecimal(2531));
    portfolio.addShare(shareOfStock);
    assertTrue(portfolio.containsShare(shareOfStock));
  }

  /* Empty portfolio should have a net worth of zero. */
  @Test
  public void getNetWorthIsZeroWhenEmpty() {
    assertEquals(0, portfolio.getNetWorth().compareTo(BigDecimal.ZERO));
  }

  /* Net worth should be positive for a share with a positive sales price. */
  @Test
  public void getNetWorthIsPositiveForProfitableShare() {
    // salesPrice=100, qty=10, purchasePrice=50
    Stock s = new Stock("A", "Co", new BigDecimal(100));
    portfolio.addShare(new Share(s, new BigDecimal(10), new BigDecimal(50)));
    assertTrue(portfolio.getNetWorth().compareTo(BigDecimal.ZERO) > 0);
  }

  /* Net worth for a single share should equal: gross - commission - tax.
   * salesPrice=100, qty=10, purchasePrice=50:
   *   gross      = 1000
   *   commission = 10        (1% of gross)
   *   costs      = 500       (purchasePrice * qty)
   *   gain       = 490       (gross - commission - costs)
   *   tax        = 147       (30% of gain)
   *   total      = 843
   */
  @Test
  public void getNetWorthIsCorrectForSingleShare() {
    Stock s = new Stock("A", "Co", new BigDecimal(100));
    portfolio.addShare(new Share(s, new BigDecimal(10), new BigDecimal(50)));
    assertEquals(0, portfolio.getNetWorth().compareTo(new BigDecimal(843)));
  }

  /* Net worth should sum across multiple shares. */
  @Test
  public void getNetWorthSumsAcrossMultipleShares() {
    // Both shares yield 843 individually (same stock, same params), so combined = 1686
    Stock s = new Stock("A", "Co", new BigDecimal(100));
    portfolio.addShare(new Share(s, new BigDecimal(10), new BigDecimal(50)));
    portfolio.addShare(new Share(s, new BigDecimal(10), new BigDecimal(50)));
    assertEquals(0, portfolio.getNetWorth().compareTo(new BigDecimal(1686)));
  }

  /* Adding a share must increase net worth. */
  @Test
  public void addingShareIncreasesNetWorth() {
    Stock s = new Stock("A", "Co", new BigDecimal(100));
    BigDecimal before = portfolio.getNetWorth();
    portfolio.addShare(new Share(s, new BigDecimal(10), new BigDecimal(50)));
    assertTrue(portfolio.getNetWorth().compareTo(before) > 0);
  }

  /* Removing a share must decrease net worth. */
  @Test
  public void removingShareDecreasesNetWorth() {
    Stock s = new Stock("A", "Co", new BigDecimal(100));
    Share share = new Share(s, new BigDecimal(10), new BigDecimal(50));
    portfolio.addShare(share);
    BigDecimal before = portfolio.getNetWorth();
    portfolio.removeShare(share);
    assertTrue(portfolio.getNetWorth().compareTo(before) < 0);
  }

  /* Net worth must use the stock's current (latest) sales price, not the purchase price.
   * After updating salesPrice from 100 to 200:
   *   gross      = 2000
   *   commission = 20
   *   costs      = 500
   *   gain       = 1480
   *   tax        = 444
   *   total      = 1536
   */
  @Test
  public void getNetWorthReflectsCurrentStockPrice() {
    Stock s = new Stock("A", "Co", new BigDecimal(100));
    portfolio.addShare(new Share(s, new BigDecimal(10), new BigDecimal(50)));
    s.addNewSalesPrice(new BigDecimal(200));
    assertEquals(0, portfolio.getNetWorth().compareTo(new BigDecimal(1536)));
  }
}
