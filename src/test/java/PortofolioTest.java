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

public class PortofolioTest {
  private Portfolio protofoilo;
  private Stock stock;

  @BeforeEach
  public void setup() {
    stock = new Stock("Bit", "ExampleCompany", new BigDecimal(20000));
    // Share shareOfStock = new Share(stock, new BigDecimal(1000), new BigDecimal(2531));
    // Share share2OfStock = new Share(stock, new BigDecimal(2000), new BigDecimal(5062));
    protofoilo = new Portfolio();
  }

  @Test
  public void gettingSharesShouldWork() {
    assertEquals(new ArrayList<>(), protofoilo.getShares());
  }

  @Test
  public void addingSharesShouldWork() {
    Share shareOfStock = new Share(stock, new BigDecimal(1000), new BigDecimal(2531));
    protofoilo.addShare(shareOfStock);
    assertEquals(new ArrayList<>(List.of(shareOfStock)), protofoilo.getShares());
  }

  @Test
  public void removiingSharesShouldWork() {
    Share shareOfStock = new Share(stock, new BigDecimal(1000), new BigDecimal(2531));
    Share share2OfStock = new Share(stock, new BigDecimal(2000), new BigDecimal(5062));
    protofoilo.addShare(shareOfStock);
    protofoilo.addShare(share2OfStock);
    protofoilo.removeShare(shareOfStock);
    assertEquals(new ArrayList<>(List.of(share2OfStock)), protofoilo.getShares());
  }
  @Test
  public void containSearchShouldWork() {
    Share shareOfStock = new Share(stock, new BigDecimal(1000), new BigDecimal(2531));
    protofoilo.addShare(shareOfStock);
    assertTrue(protofoilo.containsShare(shareOfStock));
  }

}
