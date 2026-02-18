package ntnu.gruppe21;

/** Thrown to indicate a transaction has failed. */
public class TransactionException extends Exception {

  /** Constructs a {@code TransactionException} with no detail message. */
  public TransactionException() {}

  /**
   * Constructs an {@code TransactionException} with the specified detail message.
   *
   * @param message the detail message.
   */
  public TransactionException(String message) {
    super(message);
  }
}
