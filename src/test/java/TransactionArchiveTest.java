import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import ntnu.gruppe21.model.Share;
import ntnu.gruppe21.model.Stock;
import ntnu.gruppe21.model.transaction.Purchase;
import ntnu.gruppe21.model.transaction.Sale;
import ntnu.gruppe21.model.transaction.Transaction;
import ntnu.gruppe21.model.transaction.TransactionArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the TransactionArchive class.
 *
 * <p>The tests currently focus on basic functionality and expected behavior of the
 * TransactionArchive class.
 *
 * @author Adrian Balunan
 */
public class TransactionArchiveTest {
  private TransactionArchive transactionArchive;
  private TransactionArchive FilledtransactionArchive;
  private Transaction purchase1;

  /* Method to set up TransactionArchive instances before each test case is executed, ensuring a consistent test environment. */
  @BeforeEach
  public void setup() {
    transactionArchive = new TransactionArchive();
    FilledtransactionArchive = new TransactionArchive();
    Stock stock = new Stock("Bit", "ExampleCompany", new BigDecimal(20000));
    Share share = new Share(stock, 10, new BigDecimal(10));
    purchase1 = new Purchase(share, 1);
    Transaction purchase2 = new Purchase(share, 2);
    Transaction sale1 = new Sale(share, 1);
    Transaction sale2 = new Sale(share, 2);
    FilledtransactionArchive.add(purchase2);
    FilledtransactionArchive.add(purchase2);
    FilledtransactionArchive.add(purchase1);
    FilledtransactionArchive.add(purchase1);
    FilledtransactionArchive.add(purchase1);
    FilledtransactionArchive.add(sale2);
    FilledtransactionArchive.add(sale1);
    FilledtransactionArchive.add(sale1);
  }

  /* Method to verify that the isEmpty() method returns true for an empty TransactionArchive. */
  @Test
  public void emptyArrayShouldBeCorrect() {
    assertTrue(transactionArchive.isEmpty());
  }

  /* Method to verify that the add() method correctly adds a transaction to the TransactionArchive and that the isEmpty() method returns false after adding a transaction. */
  @Test
  public void addingWorks() {
    transactionArchive.add(purchase1);
    assertFalse(transactionArchive.isEmpty());
  }

  /* Method to verify that the getTransactionsInWeek() method returns the correct list of transactions for a given week. */
  @Test
  public void getterForTransactionsInWeekWorks() {
    assertEquals(5, FilledtransactionArchive.getTransactionsInWeek(1).size());
    assertEquals(3, FilledtransactionArchive.getTransactionsInWeek(2).size());
  }

  /* Method to verify that the getPurchases() method returns the correct list of purchase transactions for a given week. */
  @Test
  public void getterForPurchasesInWeekWorks() {
    assertEquals(3, FilledtransactionArchive.getPurchases(1).size());
    assertEquals(2, FilledtransactionArchive.getPurchases(2).size());
  }

  /* Method to verify that the getSales() method returns the correct list of sale transactions for a given week. */
  @Test
  public void getterForSalesInWeekWorks() {
    assertEquals(2, FilledtransactionArchive.getSales(1).size());
    assertEquals(1, FilledtransactionArchive.getSales(2).size());
  }

  /* Method to verify that the countDistinctWeeks() method returns the correct number of distinct weeks for which transactions are recorded in the TransactionArchive. */
  @Test
  public void countDistinctWeekShouldWork() {
    assertEquals(2, FilledtransactionArchive.countDistinctWeeks());
  }
}
