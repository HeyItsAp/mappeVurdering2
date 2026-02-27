import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import ntnu.gruppe21.Share;
import ntnu.gruppe21.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ShareTest {
  private Stock stock;
  private Share shareOfStock;

  @BeforeEach
  public void setUp() {
    stock = new Stock("Bit", "ExampleCompany", new BigDecimal(20000));
    shareOfStock = new Share(stock, new BigDecimal(1000), new BigDecimal(2531));
  }

  @Test
  public void gettingStockPrintsCorrect() {
    assertEquals(stock, shareOfStock.getStock());
  }

  @Test
  public void gettingQuantityCorrect() {
    assertEquals(new BigDecimal(1000), shareOfStock.getQuantity());
  }

  @Test
  public void gettingPurchasePriceCorrect() {
    assertEquals(new BigDecimal(2531), shareOfStock.getPurchasePrice());
  }

  @Test
  public void constructorNullGivenHandled() {
    assertThrows(
        NullPointerException.class,
        () -> {
          Share share1OfStock = new Share(null, new BigDecimal(1000), new BigDecimal(2531));
        });
  }
}
