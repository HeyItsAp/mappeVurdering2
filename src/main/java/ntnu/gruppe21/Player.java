package ntnu.gruppe21;

import ntnu.gruppe21.gameEngine.Difficulty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Player {
  /* Players name */
  private final String name;

  /* Starting money for the player */
  private final BigDecimal startingMoney;

  /* Current money for the player */
  private BigDecimal currentMoney;

  /* Players purchase portfolio */
  private final Portfolio portfolio;

  /* Players transaction archive */
  private final TransactionArchive transactionArchive;

  /* Players chosen difficulty, used for GUI */
  private final Difficulty difficulty;

  /**
   * Creates a new Player with the specified name and starting money.
   *
   * @param name player name.
   * @param startingMoney money the player starts with.
   */
  public Player(String name, BigDecimal startingMoney, Difficulty difficulty) {
    this.name = name;
    this.startingMoney = startingMoney;
    this.currentMoney = startingMoney;
    this.portfolio = new Portfolio();
    this.transactionArchive = new TransactionArchive();
    this.difficulty = difficulty;
  }

  /**
   * Creates previous Player, usually from a csv load.
   *
   * @param name player name.
   * @param startingMoney money the player starts with.
   * @param currentMoney amount the player exited the game with
   * @param portfolio list of shares before exiting
   * @param transactionArchive list of transactions before exiting
   */
  public Player(
      String name,
      BigDecimal startingMoney,
      BigDecimal currentMoney,
      Portfolio portfolio,
      TransactionArchive transactionArchive,
      Difficulty difficulty) {
    this.name = name;
    this.startingMoney = startingMoney;
    this.currentMoney = currentMoney;
    this.portfolio = portfolio;
    this.transactionArchive = transactionArchive;
    this.difficulty = difficulty;
  }

  /**
   * Returns the name of the player.
   *
   * @return the name in question.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the current money of the player.
   *
   * @return the current money in question.
   */
  public BigDecimal getCurrentMoney() {
    return currentMoney;
  }

  /**
   * Returns the starting money of the player.
   *
   * @return the starting money in question.
   */
  public BigDecimal getStartingMoney() {
    return startingMoney;
  }

  /**
   * Adds money to the player's current money.
   *
   * @param amount The amount of money in question.
   */
  public void addMoney(BigDecimal amount) {
    this.currentMoney =
        this.currentMoney.add(Objects.requireNonNull(amount, "Amount cannot be null"));
  }

  /**
   * Subtracts/Withdraws money from the player's current money.
   *
   * @param amount The amount of money in question.
   */
  public void withdrawMoney(BigDecimal amount) {
    this.currentMoney =
        this.currentMoney.subtract(Objects.requireNonNull(amount, "Amount cannot be null"));
  }

  /**
   * Returns the portfolio of the player.
   *
   * @return portfolio of the player.
   */
  public Portfolio getPortfolio() {
    return portfolio;
  }

  /**
   * Returns the transaction archive of the player.
   *
   * @return Transaction archive/history of the player.
   */
  public TransactionArchive getTransactionArchive() {
    return transactionArchive;
  }

  /**
   * Returns chosen difficulty
   *
   * @return Difficulty of player
   */
  public Difficulty getDifficulty() {
    return difficulty;
  }

  /**
   * Calculates and returns the net worth of the player.
   *
   * @return Net worth of the player.
   */
  public BigDecimal getNetWorth() {
    return currentMoney.add(portfolio.getNetWorth());
  }

  /**
   * Calculates and returns the experience level of the player.
   *    Higher difficulties get a little bonus.
   *
   * @return A number representing the experience level. 1 = Novice, 2 = Investor, 3 = Speculator.
   */
  public int getStatus() {
    int weeks = transactionArchive.countDistinctWeeks();
    BigDecimal bonusGrowthFactor = difficulty.getDifficultyMultiplier().divide(BigDecimal.valueOf(100), 5, RoundingMode.HALF_UP).add(BigDecimal.ONE);
    BigDecimal gain =
        getNetWorth().subtract(startingMoney).divide(startingMoney, 8, RoundingMode.HALF_UP).multiply(bonusGrowthFactor);

    if (weeks >= 20 && gain.compareTo(BigDecimal.ONE) >= 0) {
      return 3;
    } else if (weeks >= 10 && gain.compareTo(BigDecimal.valueOf(0.2)) >= 0) {
      return 2;
    } else {
      return 1;
    }
  }
}
