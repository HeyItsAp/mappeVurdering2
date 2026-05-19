package ntnu.gruppe21.gameEngine.challanges;

/**
 * So far we have two different challanges: - {@code UNIQUE_SHARE_REQUIREMENT}, You need to have a
 * set amount of diffierent stock in your portfolio - {@code BALANCE_REQUIREMENT}, You need to have
 * a certain amount of CURRENT MONEY (not Net Worth).
 *
 * <p>Currently, has a one attribute, the title.
 */
public enum ChallengeType {
  UNIQUE_SHARE_REQUIREMENT("Own different stocks:"),
  BALANCE_REQUIREMENT("Must have amount of balance:");

  private final String challengeTitle;

  ChallengeType(String challengeTitle) {
    this.challengeTitle = challengeTitle;
  }

  public String getChallengeTitle() {
    return challengeTitle;
  }
}
