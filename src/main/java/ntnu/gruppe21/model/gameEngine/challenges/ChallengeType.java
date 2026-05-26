package ntnu.gruppe21.model.gameEngine.challenges;

import ntnu.gruppe21.model.Player;

/**
 * So far we have two different challenges: - {@code UNIQUE_SHARE_REQUIREMENT}, You need to have a
 * set amount of different stock in your portfolio - {@code BALANCE_REQUIREMENT}, You need to have a
 * certain amount of CURRENT MONEY (not Net Worth).
 *
 * <p>Currently, has a one attribute, the title.
 */
public enum ChallengeType {
  /**
   * Unique_share_requirement: Players must have of certain number of distinct stock in portfolio
   * {@link Player}
   */
  UNIQUE_SHARE_REQUIREMENT("Own different stocks:"),
  /** Balance_requirement: Players must have a certain amount of current money {@link Player} */
  BALANCE_REQUIREMENT("Must have amount of balance:");

  private final String challengeTitle;

  ChallengeType(String challengeTitle) {
    this.challengeTitle = challengeTitle;
  }

  /**
   * Returns the allocated title to each ChallengeType
   *
   * @return allocated title
   */
  public String getChallengeTitle() {
    return challengeTitle;
  }
}
