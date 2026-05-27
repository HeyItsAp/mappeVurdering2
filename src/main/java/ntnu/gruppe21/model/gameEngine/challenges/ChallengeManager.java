package ntnu.gruppe21.model.gameEngine.challenges;

import java.util.*;
import ntnu.gruppe21.filehandler.FilehandlerPlayer;
import ntnu.gruppe21.model.Player;

/**
 * Challenge Manager contains and manages challenges. Is able to check if each challenge is
 * completed and creates new challenge, and handles saving and parsing of said challenges.
 *
 * <p>To keep it simple, players will JUST HAVE ONE CHALLENGE, but this class definitely opens the
 * possibility for multiple challenges. Tight coupling with player as certain challenges highly
 * depend on players actions, for example starting money.
 *
 * <p>At the start, creates an empty set of challenges. Challenges need to be set after player
 * creation: 1. use {@link #generateChallenges(Player)} to create new random fresh challenges 2.
 * when loading from a save, {@link #parseChallenges(Map, Player)} is used in {@link
 * FilehandlerPlayer}
 */
public class ChallengeManager {
  /** List of {@link Challenge} */
  private final List<Challenge> challenges;

  // Random to produce random {@link ChallengeType}
  private final Random random = new Random();

  /** Total challenge completions across all challenge instances (including replaced ones). */
  private int totalCompletions = 0;

  /** Creates a new ChallengeManager with empty challenges list; */
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
   * Checks each challenge and refreshes its description. Completed challenges give a cash reward
   * and are replaced with a fresh challenge of a different type.
   *
   * @param player Player object to compare player stats and targetValue
   */
  public void evaluateChallenges(Player player) {
    List<Integer> completedIndexes = new ArrayList<>();
    for (int i = 0; i < challenges.size(); i++) {
      Challenge c = challenges.get(i);
      c.refreshDescription(player, c.getChallengeType());
      if (!c.isCompleted()) {
        c.checkCompletion(player);
      }
      if (c.isCompleted()) {
        totalCompletions++;
        player.addMoney(c.calculateReward(player));
        completedIndexes.add(i);
      }
    }
    for (int idx : completedIndexes) {
      ChallengeType newType = pickDifferentType(challenges.get(idx).getChallengeType());
      challenges.set(idx, new Challenge(newType, player.getDifficulty(), player));
    }
  }

  private ChallengeType pickDifferentType(ChallengeType current) {
    List<ChallengeType> options =
        Arrays.stream(ChallengeType.values())
            .filter(t -> t != current)
            .collect(java.util.stream.Collectors.toList());
    return options.get(random.nextInt(options.size()));
  }

  public int getTotalCompletions() {
    return totalCompletions;
  }

  public void setTotalCompletions(int totalCompletions) {
    this.totalCompletions = totalCompletions;
  }

  /**
   * Getter for challenges
   *
   * @return list of challenges
   */
  public List<Challenge> getActiveChallenges() {
    return Collections.unmodifiableList(challenges);
  }

  /**
   * Returns a String representing challenge state. Format: {@code total|TYPE;timesCompleted|...}
   * where {@code total} is the lifetime completion count used for player status.
   *
   * @return serialised challenge state
   */
  public String saveChallenges() {
    StringBuilder stringBuilder = new StringBuilder(100);
    stringBuilder.append(totalCompletions);
    for (Challenge c : challenges) {
      stringBuilder
          .append("|")
          .append(c.getChallengeType())
          .append(";")
          .append(c.getTimesCompleted());
    }
    return stringBuilder.toString();
  }

  /**
   * Saved challenges are just challengeTypes, so when loading a save it just creates overwrites the
   * first challenges then creates new challenges with same types.
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
