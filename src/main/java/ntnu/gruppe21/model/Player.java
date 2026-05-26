package ntnu.gruppe21.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import ntnu.gruppe21.model.gameEngine.Difficulty;
import ntnu.gruppe21.model.gameEngine.challenges.ChallengeManager;
import ntnu.gruppe21.model.transaction.TransactionArchive;

/**
 * Player object that contains all relevant data to play the game. Name, current money, {@link
 * Portfolio}, {@link TransactionArchive}, {@link ChallengeManager} to get challenges, and {@link
 * Difficulty}.
 */
public class Player {
  /** Players name */
  private final String name;

  /** Starting money for the player */
  private final BigDecimal startingMoney;

  /** Current money for the player */
  private BigDecimal currentMoney;

  /** Players purchase portfolio */
  private final Portfolio portfolio;

  /** Players transaction archive */
  private final TransactionArchive transactionArchive;

  /** Players chosen difficulty, used for GUI */
  private final Difficulty difficulty;

  /**
   * ChallengeManager, track challenges {@link ChallengeManager} Challenges need to be created after
   * player creation, as challenges and player have necessarily tight coupling.
   */
  private final ChallengeManager challengeManager;

  /**
   * Creates a new Player with through a builder. Builder is at the bottom
   *
   * @param builder Builder, follows Builder Creational Pattern.
   */
  public Player(Builder builder) {
    this.name = builder.name;
    this.startingMoney = builder.startingMoney;
    this.currentMoney = builder.currentMoney;
    this.portfolio = builder.portfolio;
    this.transactionArchive = builder.transactionArchive;
    this.difficulty = builder.difficulty;
    this.challengeManager = builder.challengeManager;
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
   * Returns challengeManager
   *
   * @return Manager class contain challenges
   */
  public ChallengeManager getChallengeManager() {
    return challengeManager;
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
   * Calculates and returns the experience level of the player. Higher difficulties get a little
   * bonus.
   *
   * @return A number representing the experience level. 1 = Novice, 2 = Investor, 3 = Speculator.
   */
  public int getStatus() {
    int weeks = transactionArchive.countDistinctWeeks();
    BigDecimal bonusGrowthFactor =
        difficulty
            .getDifficultyMultiplier()
            .divide(BigDecimal.valueOf(100), 5, RoundingMode.HALF_UP)
            .add(BigDecimal.ONE);
    BigDecimal gain =
        getNetWorth()
            .subtract(startingMoney)
            .divide(startingMoney, 8, RoundingMode.HALF_UP)
            .multiply(bonusGrowthFactor);

    if (weeks >= 20 && gain.compareTo(BigDecimal.ONE) >= 0) {
      return 3;
    } else if (weeks >= 10 && gain.compareTo(BigDecimal.valueOf(0.2)) >= 0) {
      return 2;
    } else {
      return 1;
    }
  }

  /**
   * Standard builder that have base values that are necessary to make a new player. If necessary
   * attributes can be changed. Challenges need to be created after player creation, as challenges
   * and player have necessarily tight coupling.
   */
  public static class Builder {
    private final String name;
    private final BigDecimal startingMoney;
    private BigDecimal currentMoney;
    private Portfolio portfolio;
    private TransactionArchive transactionArchive;
    private Difficulty difficulty;
    private ChallengeManager challengeManager;

    /**
     * Starts a builder object with the minimum attributes for a Player.
     *
     * @param name Name of the Player
     * @param startingMoney Player chosen starting amount
     * @param difficulty Difficulty.enum
     */
    public Builder(String name, BigDecimal startingMoney, Difficulty difficulty) {
      this.name = name;
      this.startingMoney = startingMoney;
      this.currentMoney = startingMoney;
      this.portfolio = new Portfolio();
      this.transactionArchive = new TransactionArchive();
      this.difficulty = difficulty;
      this.challengeManager = new ChallengeManager();
    }

    /**
     * Set-method through builder.
     *
     * @param currentMoney currentMoney to be changed
     * @return Builder with updated currentMoney
     */
    public Builder currentMoney(BigDecimal currentMoney) {
      this.currentMoney = currentMoney;
      return this;
    }

    /**
     * Set-method through builder.
     *
     * @param portfolio portfolio to be changed
     * @return Builder with updated portfolio
     */
    public Builder portfolio(Portfolio portfolio) {
      this.portfolio = portfolio;
      return this;
    }

    /**
     * Set-method through builder.
     *
     * @param transactionArchive transactionArchive to be changed
     * @return Builder with updated transactionArchive
     */
    public Builder transactionArchive(TransactionArchive transactionArchive) {
      this.transactionArchive = transactionArchive;
      return this;
    }

    /**
     * Set-method through builder.
     *
     * @param difficulty difficulty to be changed
     * @return Builder with updated difficulty
     */
    public Builder difficulty(Difficulty difficulty) {
      this.difficulty = difficulty;
      return this;
    }

    /**
     * Set-method through builder.
     *
     * @param challengeManager challengeManager to be changed
     * @return Builder with updated challengeManager
     */
    public Builder challengeManager(ChallengeManager challengeManager) {
      this.challengeManager = challengeManager;
      return this;
    }

    /**
     * Build-method, finalizes all changes and returns a complete Player Object
     *
     * @return complete Player Object
     */
    public Player build() {
      return new Player(this);
    }
  }
}
