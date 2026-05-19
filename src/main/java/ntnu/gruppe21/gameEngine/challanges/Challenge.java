package ntnu.gruppe21.gameEngine.challanges;

import java.math.BigDecimal;
import java.math.RoundingMode;
import ntnu.gruppe21.Player;
import ntnu.gruppe21.gameEngine.Difficulty;

/**
 * Wraps the {@link ChallengeType}, gives it a visual description and has functions to check if
 * player has completed the challange, and get advance into a new challange.
 *
 * <p>Challenges needs to be created after player creation, as players chosen starting money effects
 * certain challanges. Difficulty effects challenges by making it more difficult or not.
 */
public class Challenge {
  /** {@link ChallengeType} */
  private final ChallengeType challengeType;

  // Difficulty effects challenges by making it more difficult or not.
  private final Difficulty difficulty;

  /**
   * Description, more or less a progressbar. Displays your progress and challange description from
   * {@link ChallengeType} *
   */
  private String description;

  /**
   * Depends on the {@link ChallengeType} - {@code UNIQUE_SHARE_REQUIREMENT}. Will be low integer
   * that keeps rising - {@code BALANCE_REQUIREMENT}. Based on starting money
   */
  private int targetValue;

  // Standard boolean to mark if challenge is completed or not
  private boolean completed;

  /**
   * Creates a uncompleted challenge of {@link ChallengeType} and {@link Difficulty}.
   *
   * @param challengeType Right now, can be of two types.
   * @param difficulty effect how difficult the challenge will be by updating targetValue
   * @param player certain {@link ChallengeType} depend on starting stats of player, so its required
   */
  public Challenge(ChallengeType challengeType, Difficulty difficulty, Player player) {
    this.challengeType = challengeType;
    this.difficulty = difficulty;
    this.completed = false;
    this.targetValue = setInitialValue(challengeType, difficulty, player);
    this.description = challengeType.getChallengeTitle() + ": 0 / " + targetValue;
  }

  /**
   * Updates the visual/progress/description of the challenge.
   *
   * @param player certain {@link ChallengeType} depend on starting stats of player, so its
   *     requires.
   * @param challengeType Can be of two different type and have different functions.
   */
  public void refreshDescription(Player player, ChallengeType challengeType) {
    description =
        switch (challengeType) {
          case UNIQUE_SHARE_REQUIREMENT ->
              challengeType.getChallengeTitle()
                  + ": "
                  + player.getPortfolio().countDistinctStock()
                  + " / "
                  + targetValue;
          case BALANCE_REQUIREMENT ->
              challengeType.getChallengeTitle()
                  + ": "
                  + player.getCurrentMoney()
                  + " / "
                  + targetValue;
        };
  }

  /**
   * Calculates the INITIAL goal requirements based on difficulty and challenge. Method contains
   * base values for each type that is then modified by {@link Difficulty} to produce the final
   * requirement to complete the challenge.
   *
   * @param challengeType Right now, can be of two types. Different types use different base values
   *     and multipliers
   * @param difficulty effects base values to create more demanding challenges
   * @param player certain {@link ChallengeType} depend on starting stats of player, so its required
   * @return new Initial requirement
   */
  private int setInitialValue(ChallengeType challengeType, Difficulty difficulty, Player player) {
    int baseBalanceRequirement = 0;
    int baseUniqueStock = 2;
    try {
      baseBalanceRequirement =
          player.getCurrentMoney().divide(BigDecimal.TWO, RoundingMode.HALF_UP).intValue();
    } catch (NumberFormatException e) {
      throw new RuntimeException("Starting ammount is invalid for challenge: " + e);
    }

    return switch (challengeType) {
      case UNIQUE_SHARE_REQUIREMENT ->
          switch (difficulty) {
            case EASY -> baseUniqueStock;
            case MEDIUM -> baseUniqueStock + 2;
            case HARD -> baseUniqueStock + 4;
            case REALISTIC -> baseUniqueStock + 8;
          };
      case BALANCE_REQUIREMENT ->
          switch (difficulty) {
            case EASY -> baseBalanceRequirement;
            case MEDIUM -> baseBalanceRequirement + (baseBalanceRequirement / 100 * 15); //
            case HARD, REALISTIC -> baseBalanceRequirement + (baseBalanceRequirement / 100 * 25);
              // Hard Enough as it is because of change rate among stocks
          };
    };
  }

  /**
   * Checks if challenge is completed by player by going through values according to {@link
   * ChallengeType}
   *
   * @param player
   * @return {@code True} or {@code False} based on if it fills the requirement
   */
  public boolean checkCompletion(Player player) {
    if (completed) return true;

    boolean done =
        switch (challengeType) {
          case BALANCE_REQUIREMENT ->
              player.getCurrentMoney().compareTo(BigDecimal.valueOf(targetValue)) >= 0;
          case UNIQUE_SHARE_REQUIREMENT ->
              player.getPortfolio().countDistinctStock() >= targetValue;
        };
    if (done) completed = true;
    return done;
  }

  /**
   * Calculates the A NEW goal requirements based on initial goal. Right now does a simple
   * arithmetic addition to calculate new goal. One of params is {@code newWeek} (which this method
   * doesn't use) but opens for interesting ways to calculate new goals.
   *
   * @param player certain {@link ChallengeType} depend on starting stats of player, so its required
   * @param newWeek Unnecessary parameter, but opens for development.
   * @return new Initial requirement
   */
  public void advanceChallenge(int newWeek, Player player) {
    if (!completed) throw new IllegalStateException("Challenge is not completed");
    targetValue =
        switch (challengeType) {
          case UNIQUE_SHARE_REQUIREMENT ->
              switch (difficulty) {
                case EASY, MEDIUM, HARD, REALISTIC -> targetValue + 1;
              };
          case BALANCE_REQUIREMENT ->
              switch (difficulty) {
                case EASY, MEDIUM, HARD, REALISTIC -> targetValue + targetValue / 100;
              };
        };
    completed = false;
    refreshDescription(player, challengeType);
  }

  /** Simple getters */
  public boolean isCompleted() {
    return completed;
  }

  public String getDescription() {
    return description;
  }

  public ChallengeType getChallengeType() {
    return challengeType;
  }
}
