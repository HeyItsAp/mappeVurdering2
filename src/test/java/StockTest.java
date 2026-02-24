import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import ntnu.gruppe21.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Stock class.
 * 
 * The tests currently focus on basic functionality and expected behavior of the Stock class.
 * @author Adrian Balunan
 */

public class StockTest {
  private Stock stock;

  /* Method to set up a Stock instance before each test case is executed, ensuring a consistent test environment. */
  @BeforeEach
  public void setup() {
    stock = new Stock("Bit", "ExampleCompany", new BigDecimal(20000));
  }

  /* Method to verify that the getSymbol() method returns the correct symbol of the stock. */
  @Test
  public void gettingSymbolCorrect() {
    assertEquals("Bit", stock.getSymbol());
  }

  /* Method to verify that the getCompany() method returns the correct company name of the stock. */
  @Test
  public void gettingCompanyCorrect() {
    assertEquals("ExampleCompany", stock.getCompany());
  }

  /* Method to verify that the getSalesPrice() method returns the correct sales price of the stock. */
  @Test
  public void gettingPricesListCorrect() {
    assertEquals(new BigDecimal(20000), stock.getSalesPrice());
  }

  /* Method to verify that the addNewSalesPrice() method correctly updates the sales price of the stock. */
  @Test
  public void addingNewSalesCorrect() {
    stock.addNewSalesPrice(new BigDecimal(12345));
    assertEquals(new BigDecimal(12345), stock.getSalesPrice());
  }
}
