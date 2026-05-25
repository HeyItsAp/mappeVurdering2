package ntnu.gruppe21.gameEngine.challanges;

import java.util.*;
import ntnu.gruppe21.Player;
import ntnu.gruppe21.filehandler.FilehandlerPlayer;

/**
 * TODO: Add as attribute in Player.java, get FilehandlerPlayer to handle said challanges and Junit
 * test Challenge Manager manges challenges by checking for if each challange is completed and
 * creates new challange.
 *
 * <p>To keep it simple, players will JUST HAVE ONE CHALLENGE, but this class definitely opens the
 * possibility for multiple challenges. Thigh coupling with player as certain challanges highly
 * depend on players actions, for example starting money.
 *
 * <p>At the start, creaetes an empty set of challanges. Challenges need to be set after player
 * creation: 1. use {@link #generateChallenges(Player)} to create new random fresh challenges 2.
 * when loading from a save, {@link #parseChallenges(Map, Player)} is used in {@link
 * FilehandlerPlayer}
 */
public class ChallengeManager {
  /** List of {@link Challenge} */
  private final List<Challenge> challenges;

  // Random to produce random {@link ChallengeType}
  private final Random random = new Random();

  /**
   * Creates a new ChallengeManager with empty challenges list;
   */
  public ChallengeManager() {
    this.challenges = new ArrayList<>();
  }

  /**
   * Currently makes one {@link Challenge} with a random {@link ChallengeType}, but changing it to
   * multiple is no problem.
   */
  public void generateChallenges(Player player) {
    List<ChallengeType> challengeTypesList = List.of(ChallengeType.values());
    int size = challengeTypesList.size();
    ChallengeType randomChallengeType = challengeTypesList.get(random.nextInt(size));

    challenges.add(new Challenge(randomChallengeType, player.getDifficulty(), player));
  }

  /**
   * Checks if all challanges are complete for {@link Player}. If it is, advance the challanges.
   *
   * @param player Player object to compare player stats and targetValue
   */
  public void evaluateChallenges(Player player) {
    boolean completedAll = true;
    for (Challenge c : challenges) {
      c.refreshDescription(player, c.getChallengeType());
      if (!c.isCompleted() && !c.checkCompletion(player)) {
        completedAll = false;
        throw new IllegalStateException("All challenges are not completed");
      }
    }

    for (Challenge c : challenges) {
      c.advanceChallenge(0, player);
    }
  }

  /**
   * Getter for challanges
   *
   * @return list of challenges
   */
  public List<Challenge> getActiveChallenges() {
    return Collections.unmodifiableList(challenges);
  }

  /**
   * Returns a String with all challenges type with | as seperator.
   *
   * @return String with containing each Challenge object as ChallengeType;timesCompleted then separated by "|";
   */
  public String saveChallenges() {
    StringBuilder stringBuilder = new StringBuilder(100);
    for (Challenge c : challenges) {
      stringBuilder
          .append(c.getChallengeType())
          .append(";")
          .append(c.getTimesCompleted())
          .append("|");
    }
    stringBuilder.deleteCharAt(stringBuilder.length() - 1); // Remove last |
    return stringBuilder.toString();
  }

  /**
   * Saved challanges are just challengeTypes, so when loading a save it just creates overwrites the
   * first challenges then creates new challanges with same types.
   *
   * @param challengeMetaDataMap Map containing unique challengeType with how many times it has been
   *     completed
   * @param player contain coupled metadata.
   */
  public void parseChallenges(Map<ChallengeType, Integer> challengeMetaDataMap, Player player) {
    challengeMetaDataMap.forEach(
        (key, value) -> {
          challenges.add(new Challenge(key, player.getDifficulty(), player, value));
        });
  }
}
