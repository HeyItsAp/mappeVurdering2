import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import ntnu.gruppe21.Player;
import ntnu.gruppe21.Share;
import ntnu.gruppe21.Stock;
import ntnu.gruppe21.transaction.Sale;
import ntnu.gruppe21.transaction.Transaction;
import ntnu.gruppe21.transaction.TransactionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Sale class.
 *
 * <p>The tests currently focus on basic functionality and expected behavior of the Sale class.
 *
 * @author Adrian Balunan
 */
public class SaleTest {
  private Transaction sale;
  private Player player1;

  /* Method to set up a Sale instance before each test case is executed, ensuring a consistent test environment. */
  @BeforeEach
  public void setup() {
    Stock stock1 = new Stock("Bit", "Company1", new BigDecimal(1000));
    Share share = new Share(stock1, new BigDecimal(10), stock1.getSalesPrice());
    player1 = new Player("Name", new BigDecimal(100000));
    sale = new Sale(share, 1);
  }

  /* Method to verify that the commit() method correctly commits a sale transaction and that it cannot be committed more than once. */
  @Test
  public void commitingWorks() throws TransactionException {
    sale.commit(player1);
    assertTrue(sale.isCommitted());
    assertThrows(
        TransactionException.class,
        () -> {
          sale.commit(player1);
        });
  }
}
