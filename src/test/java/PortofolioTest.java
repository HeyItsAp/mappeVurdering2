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
public class PortofolioTest {
  private Portfolio protofoilo;
  private Stock stock;

  /* Method to set up a Portfolio instance before each test case is executed, ensuring a consistent test environment. */
  @BeforeEach
  public void setup() {
    stock = new Stock("Bit", "ExampleCompany", new BigDecimal(20000));
    // Share shareOfStock = new Share(stock, new BigDecimal(1000), new BigDecimal(2531));
    // Share share2OfStock = new Share(stock, new BigDecimal(2000), new BigDecimal(5062));
    protofoilo = new Portfolio();
  }

  /* Method to verify that the getShares() method returns an empty list when no shares are added to the portfolio. */
  @Test
  public void gettingSharesShouldWork() {
    assertEquals(new ArrayList<>(), protofoilo.getShares());
  }

  /* Method to verify that the addShare() method correctly adds a share to the portfolio and that the getShares() method returns the correct list of shares. */
  @Test
  public void addingSharesShouldWork() {
    Share shareOfStock = new Share(stock, new BigDecimal(1000), new BigDecimal(2531));
    protofoilo.addShare(shareOfStock);
    assertEquals(new ArrayList<>(List.of(shareOfStock)), protofoilo.getShares());
  }

  /* Method to verify that the removeShare() method correctly removes a share from the portfolio and that the getShares() method returns the correct list of shares. */
  @Test
  public void removiingSharesShouldWork() {
    Share shareOfStock = new Share(stock, new BigDecimal(1000), new BigDecimal(2531));
    Share share2OfStock = new Share(stock, new BigDecimal(2000), new BigDecimal(5062));
    protofoilo.addShare(shareOfStock);
    protofoilo.addShare(share2OfStock);
    protofoilo.removeShare(shareOfStock);
    assertEquals(new ArrayList<>(List.of(share2OfStock)), protofoilo.getShares());
  }

  /* Method to verify that the containsShare() method correctly identifies whether a share is present in the portfolio. */
  @Test
  public void containSearchShouldWork() {
    Share shareOfStock = new Share(stock, new BigDecimal(1000), new BigDecimal(2531));
    protofoilo.addShare(shareOfStock);
    assertTrue(protofoilo.containsShare(shareOfStock));
  }
}
