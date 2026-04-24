import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import ntnu.gruppe21.Player;
import ntnu.gruppe21.Share;
import ntnu.gruppe21.Stock;
import ntnu.gruppe21.transaction.Purchase;
import ntnu.gruppe21.transaction.Transaction;
import ntnu.gruppe21.transaction.TransactionException;
import ntnu.gruppe21.transaction.calculators.PurchaseCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Purchase class.
 *
 * <p>The tests currently focus on basic functionality and expected behavior of the Purchase class.
 *
 * @author Adrian Balunan
 */
public class TransactionBuyTest {
  private Transaction purchase;
  private Share share;
  private Player player1;

  /* Method to set up a Purchase instance before each test case is executed, ensuring a consistent test environment. */
  @BeforeEach
  public void setup() {
    Stock stock1 = new Stock("Bit", "Company1", new BigDecimal(1000));
    share = new Share(stock1, new BigDecimal(10), stock1.getSalesPrice());
    player1 = new Player("Name", new BigDecimal(100000));
    purchase = new Purchase(share, 1);
  }

  /* Method to verify that the getShare() method returns the correct Share object associated with the Purchase transaction. */
  @Test
  public void getterForShareWorks() {
    assertEquals(share, purchase.getShare());
  }

  /* Method to verify that the getWeek() method returns the correct week number associated with the Purchase transaction. */
  @Test
  public void getterForWeekWorks() {
    assertEquals(1, purchase.getWeek());
  }

  /* Method to verify that the getCalculator() method returns an instance of the PurchaseCalculator class, which is responsible for calculating the cost of the purchase transaction. */
  @Test
  public void getterForCalculatorReturnCorrectType() {
    assertInstanceOf(PurchaseCalculator.class, purchase.getCalculator());
  }

  /* Method to verify that the isCommitted() method returns false for a newly created Purchase transaction, indicating that it has not yet been committed. */
  @Test
  public void IsCommitedWorks() {
    assertFalse(purchase.isCommitted());
  }

  /* Method to verify that the commit() method correctly commits a purchase transaction and that it cannot be committed more than once. */
  @Test
  public void commitingWorks() throws TransactionException {
    purchase.commit(player1);
    assertTrue(purchase.isCommitted());
    assertThrows(
        TransactionException.class,
        () -> {
          purchase.commit(player1);
        });
  }
}
