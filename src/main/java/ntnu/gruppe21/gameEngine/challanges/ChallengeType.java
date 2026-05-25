package ntnu.gruppe21.gameEngine.challanges;

/**
 * So far we have two different challanges: - {@code UNIQUE_SHARE_REQUIREMENT}, You need to have a
 * set amount of different stock in your portfolio - {@code BALANCE_REQUIREMENT}, You need to have
 * a certain amount of CURRENT MONEY (not Net Worth).
 *
 * <p>Currently, has a one attribute, the title.
 */
public enum ChallengeType {
  /**
   * Unique_share_requirement: Players must have of certain number of distinct stock in portfolio {@link ntnu.gruppe21.Player}
   */
  UNIQUE_SHARE_REQUIREMENT("Own different stocks:"),
  /**
   * Balance_requirement: Players must have a certain ammount of current money {@link ntnu.gruppe21.Player}
   */
  BALANCE_REQUIREMENT("Must have amount of balance:");

  private final String challengeTitle;

  ChallengeType(String challengeTitle) {
    this.challengeTitle = challengeTitle;
  }

  /**
   * Returns the allocated title to each ChallengeType
   * @return allocated title
   */
  public String getChallengeTitle() {
    return challengeTitle;
  }
}
