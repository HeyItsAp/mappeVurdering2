/**
 * An archive of transactions, which can be used to store and retrieve transactions based on the
 * week they were made.
 *
 * @author Adrian
 */
package ntnu.gruppe21;

import java.util.ArrayList;
import java.util.List;

public class TransactionArchive {
  /** An list of transactions, which can contain both purchases and sales. */
  private List<Transaction> transactions;

  /** Creates a new TransactionArchive with an empty list of transactions. */
  public TransactionArchive() {
    this.transactions = new ArrayList<>();
  }

  /**
   * Adds a transaction to the archive.
   *
   * @param transaction The transaction to be added to the archive.
   * @return true if the transaction was added successfully.
   */
  public boolean add(Transaction transaction) {
    transactions.add(transaction);
    return true;
  }

  /**
   * Checks if the archive is empty.
   *
   * @return true/false based on whether the archive is empty or not.
   */
  public boolean isEmpty() {
    return transactions.isEmpty();
  }

  /**
   * Returns a list of transactions that were made in the specified week.
   *
   * @param week The week in question
   * @return A list of transactions
   */
  public List<Transaction> getTransactionsInWeek(int week) {
    List<Transaction> filteredTransactions =
        transactions.stream().filter(t -> t.getWeek() == week).toList();
    return filteredTransactions;
  }

  /**
   * Returns a list of purchases, that were made in the specified week.
   *
   * @param week The week in question
   * @return Thelist of purchases
   */
  public List<Purchase> getPurchases(int week) {
    List<Purchase> filteredTransactions =
        transactions.stream().map(t -> (Purchase) t).filter(t -> t.getWeek() == week).toList();
    return filteredTransactions;
  }

  /**
   * Returns a list of sales, that were made in the specified week.
   *
   * @param week The week in question
   * @return Thelist of sales
   */
  public List<Sale> getSales(int week) {
    List<Sale> filteredTransactions =
        transactions.stream().map(t -> (Sale) t).filter(t -> t.getWeek() == week).toList();
    return filteredTransactions;
  }

  /**
   * Counts the number of distinct weeks in which transactions were made.
   *
   * @return the length of the list of distinct list, which is the number of distinct weeks.
   */
  public int countDistinctWeeks() {
    List<Integer> weeks = transactions.stream().map(t -> t.getWeek()).distinct().toList();
    return weeks.size();
  }
}
