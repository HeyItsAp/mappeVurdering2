package ntnu.gruppe21.gameEngine.challanges;

import ntnu.gruppe21.Player;

import java.util.*;

/**
 * Challenge Manager manges challenges by checking for if each challange is completed
 * and creates new challange.
 * <p>
 *     To keep it simple, players will JUST HAVE ONE CHALLENGE, but this class definitely
 *     opens the possibility for multiple challenges.
 *     Thight coupling with player as certain challanges highly depend on players actions, for example
 *     starting money.
 * </p>
 */
public class ChallengeManager {
    /** List of {@link Challenge}*/
    private final List<Challenge> challenges;

    // Random to produce random {@link ChallengeType}
    private final Random random = new Random();

    public ChallengeManager(Player player) {
        this.challenges = generateChallenges(player);
    }

    /**
     * Currently makes one {@link Challenge} with a random {@link ChallengeType}, but changing it to multiple is no problem.
     * @return list of ONE {@link Challenge}
     */
    private List<Challenge> generateChallenges(Player player) {
        List<Challenge> challenges = new ArrayList<>();

        List<ChallengeType> challengeTypesList =
                List.of(ChallengeType.values());
        int size = challengeTypesList.size();
        ChallengeType randomChallengeType = challengeTypesList.get(random.nextInt(size));

        challenges.add(new Challenge(randomChallengeType,player.getDifficulty(),player));
        return challenges;
    }

    /**
     * Checks if all challanges are complete for {@link Player}.
     * If it is, advance the challanges.
     *
     * @param player
     */
    public void evaluateChallenges(Player player) {
        boolean completedAll = true;
        for (Challenge c : challenges) {
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
     * @return list of challenges
     */
    public List<Challenge> getActiveChallenges() {
        return Collections.unmodifiableList(challenges);
    }
}
