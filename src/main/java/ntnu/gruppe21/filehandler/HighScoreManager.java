package ntnu.gruppe21.filehandler;

import ntnu.gruppe21.Exchange;
import ntnu.gruppe21.Player;

import java.io.PrintWriter;
import java.math.BigDecimal;

/**
 * This class handles calculations, writing and reading of highscores.
 *      Usually invoked when checking highscores or when finishing a game to get highscores.
 *
 * Gets week and sale-calculations from {@link Exchange} and money-related and portfolio states
 * from {@link Player}
 */

public class HighScoreManager {
    private static final String highscoreFile = "src/main/resources/datasets/highscores.csv";

    /**
     * Method that calculates your final score based on an algorithm. Can be seen as a helper for
     * {@link #calculateFinalScore(Exchange, Player)}
     *
     * <p>
     *     Final Score is calculated by the following algorithm:
     *     FinalScore = Difficulty (not yet implemented) * Week * NetWorth/StartingMoney * 10
     *     Higher weeks and greater profit margin will wield a higher score.
     * </p>
     *
     * @param exchange The exchange at current point to calculate networth at current state
     * @param player Player with portfolio to calculate final score.
     * @return highscore final highscore
     */
    public static BigDecimal calculateFinalScore(Exchange exchange, Player player){
        // Difficulty not yet implemented
        Object difficulty = null;
        int difficultyValue = difficulty.getValue();
        BigDecimal difficultyBigDecimalValue = BigDecimal.valueOf(difficultyValue);

        int week = exchange.getWeek();
        BigDecimal weekBigDecimal = BigDecimal.valueOf(week);

        BigDecimal startingMoney = player.getStartingMoney();
        BigDecimal fortune = player.getCurrentMoney().add(player.getNetWorth());
        BigDecimal profitScore = fortune.divide(startingMoney).multiply(BigDecimal.TEN);

        BigDecimal finalScore = difficultyBigDecimalValue.multiply(weekBigDecimal).multiply(profitScore);
        if (finalScore.compareTo(BigDecimal.ONE) < 0){
            throw new IllegalArgumentException("Final score became negative");
        }
        return finalScore;
    }
    /**
     * Method to add a new line in datasets/highscores.csv containing player name, week and
     * calculated finalScore.
     *      Uses {@link  #calculateFinalScore(Exchange, Player)} to calculate final score.
     *
     * @param exchange The exchange at current point to calculate networth at current state
     * @param player Player with portfolio to calculate final score.
     * @return true/false if is successes or not
     */
    public static boolean addFinalScoreToCsv(Exchange exchange, Player player){
        BigDecimal finalScore = null;
        try {
            finalScore = calculateFinalScore(exchange, player);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        try (PrintWriter pw = new PrintWriter(highscoreFile)) {
            pw.println(exchange.getWeek() + "," + player.getName() + "," + finalScore);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
