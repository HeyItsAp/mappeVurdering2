import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import ntnu.gruppe21.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StockTest {
  private Stock stock;

  @BeforeEach
  public void setup() {
    stock = new Stock("Bit", "ExampleCompany", new BigDecimal(20000));
  }

  @Test
  public void gettingSymbolCorrect() {
    assertEquals("Bit", stock.getSymbol());
  }

  @Test
  public void gettingCompanyCorrect() {
    assertEquals("ExampleCompany", stock.getCompany());
  }

  @Test
  public void gettingPricesListCorrect() {
    assertEquals(new BigDecimal(20000), stock.getSalesPrice());
  }


  @Test
  public void addingNewSalesCorrect() {
    stock.addNewSalesPrice(new BigDecimal(12345));
    assertEquals(new BigDecimal(12345), stock.getSalesPrice());
  }
}
