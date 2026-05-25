package ntnu.gruppe21.filehandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;
import ntnu.gruppe21.Exchange;
import ntnu.gruppe21.Player;

/**
 * This class handles calculations, writing and reading of highscores. Usually invoked when checking
 * highscores or when finishing a game to get highscores.
 *
 * <p>Gets week and sale-calculations from {@link Exchange} and money-related and portfolio states
 * from {@link Player}
 */
public class HighScoreManager {
  private static final String highscoreFile = "src/main/resources/datasets/highscores.csv";

  /**
   * Method that calculates your final score based on an algorithm. Can be seen as a helper for
   * {@link #calculateFinalScore(Exchange, Player)}
   *
   * <p>Final Score is calculated by the following algorithm: FinalScore = Difficulty (not yet
   * implemented) * Week * NetWorth/StartingMoney * 10 Higher weeks and greater profit margin will
   * wield a higher score.
   *
   * @param exchange The exchange at current point to calculate networth at current state
   * @param player Player with portfolio to calculate final score.
   * @return highscore final highscore
   */
  public static BigDecimal calculateFinalScore(Exchange exchange, Player player) {
    // TODO: Difficulty mechanism not yet implemented
    BigDecimal difficultyBigDecimalValue = BigDecimal.valueOf(5);

    int week = exchange.getWeek();
    BigDecimal weekBigDecimal = BigDecimal.valueOf(week);

    BigDecimal startingMoney = player.getStartingMoney();
    BigDecimal fortune = player.getCurrentMoney().add(player.getNetWorth());
    BigDecimal profitScore = fortune.divide(startingMoney).multiply(BigDecimal.TEN);
    System.out.println(startingMoney + " " + fortune + " " + profitScore);

    BigDecimal playerDifficultyFinalMultiplier = player.getDifficulty().getDifficultyMultiplier();
    BigDecimal finalScore =
        difficultyBigDecimalValue
            .multiply(weekBigDecimal)
            .multiply(profitScore)
            .multiply(playerDifficultyFinalMultiplier);

    if (finalScore.compareTo(BigDecimal.ZERO) < 0) {
      System.out.println(finalScore);
      throw new IllegalArgumentException("Final score became negative");
    }
    return finalScore;
  }

  /**
   * Method to add a new line in datasets/highscores.csv containing player name, week and calculated
   * finalScore. Uses {@link #calculateFinalScore(Exchange, Player)} to calculate final score.
   *
   * <p>Format of generated lines: Week,playerName,finalScore
   *
   * @param exchange The exchange at current point to calculate networth at current state
   * @param player Player with portfolio to calculate final score.
   * @return true/false if is successes or not
   */
  public static boolean addFinalScoreToCsv(Exchange exchange, Player player) {
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

  /**
   * Reads all high scores from the CSV and returns them sorted by final score descending.
   *
   * <p>Each entry in the returned list is a {@code List<String>} with three elements:
   *
   * <ol>
   *   <li>week (e.g. {@code "12"})
   *   <li>player name (e.g. {@code "Alice"})
   *   <li>final score (e.g. {@code "340.50"})
   * </ol>
   *
   * @return a list of score entries sorted by final score descending, or an empty list if the file
   *     is missing or unreadable
   */
  public static ArrayList<List> getHighScores() {
    ArrayList<List> scores = new ArrayList<>();
    String line = "";
    try {
      BufferedReader br = new BufferedReader(new FileReader(highscoreFile));
      while ((line = br.readLine()) != null) {
        String trimmedLine = line.trim();

        if (trimmedLine.isEmpty()) {
          continue;
        }
        if (trimmedLine.startsWith("#")) {
          continue;
        }

        String[] values = trimmedLine.split(",");

        // Just a normal print out
        for (String value : values) {
          System.out.print(value.trim() + " ");
        }

        if (values.length != 3) throw new IllegalArgumentException("Illegal format");
        try {
          new Integer(values[0]);
          new BigDecimal(values[2].trim()); // price must be a number
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("Illegal format");
        }

        String week = values[0];
        String playerName = values[1];
        String finalScore = (values[2]);
        scores.add(List.of(week, playerName, finalScore));
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    if (!scores.isEmpty()) {
      // Sort by final score descending
      scores.sort(
          (a, b) -> {
            BigDecimal scoreA = new BigDecimal((char[]) a.get(2));
            BigDecimal scoreB = new BigDecimal((char[]) b.get(2));
            return scoreB.compareTo(scoreA);
          });
    }
    return scores;
  }
}
