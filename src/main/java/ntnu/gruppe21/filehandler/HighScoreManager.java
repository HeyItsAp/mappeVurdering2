package ntnu.gruppe21.filehandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import ntnu.gruppe21.model.Exchange;
import ntnu.gruppe21.model.Player;

/**
 * This class handles calculations, writing and reading of highscores. Usually invoked when checking
 * highscores or when finishing a game to get highscores.
 *
 * <p>Gets week and sale-calculations from {@link Exchange} and money-related and portfolio states
 * from {@link Player}
 */
public class HighScoreManager {

  private static final String highscoreFile =
      "src/main/resources/datasets/highscores/highscores.csv";

  /**
   * Calculates the final score for a given game state.
   *
   * <p>Formula: {@code Difficulty * Week * (NetWorth / StartingMoney) * 10}. Higher weeks and a
   * greater profit margin yield a higher score.
   *
   * @param exchange the exchange at the point of scoring
   * @param player the player whose state is scored
   * @return the calculated final score
   * @throws IllegalArgumentException if the score turns out negative
   */
  public static BigDecimal calculateFinalScore(Exchange exchange, Player player) {
    int week = exchange.getWeek();
    BigDecimal startingMoney = player.getStartingMoney();
    BigDecimal fortune = player.getCurrentMoney().add(player.getNetWorth());
    BigDecimal profitScore =
        fortune.divide(startingMoney, 6, RoundingMode.HALF_UP).multiply(BigDecimal.TEN);

    BigDecimal finalScore =
        BigDecimal.valueOf(5)
            .multiply(BigDecimal.valueOf(week))
            .multiply(profitScore)
            .multiply(player.getDifficulty().getDifficultyMultiplier());

    if (finalScore.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Final score became negative: " + finalScore);
    }
    return finalScore.setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Appends a new high-score entry to the CSV file.
   *
   * <p>Format of each line: {@code week,playerName,finalScore}
   *
   * @param exchange the exchange at the point of scoring
   * @param player the player whose score is recorded
   * @return {@code true} if the entry was written successfully, {@code false} otherwise
   */
  public static boolean addFinalScoreToCsv(Exchange exchange, Player player) {
    BigDecimal finalScore;
    try {
      finalScore = calculateFinalScore(exchange, player);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    // append = true so existing entries are not overwritten
    try (PrintWriter pw = new PrintWriter(new FileWriter(highscoreFile, true))) {
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
   * <p>Each entry is a {@code List<String>} with three elements:
   *
   * <ol>
   *   <li>week (e.g. {@code "12"})
   *   <li>player name (e.g. {@code "Alice"})
   *   <li>final score (e.g. {@code "340.50"})
   * </ol>
   *
   * @return entries sorted by score descending; empty list if the file is missing or unreadable
   */
  public static List<List<String>> getHighScores() {
    List<List<String>> scores = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(highscoreFile))) {
      String line;
      while ((line = br.readLine()) != null) {
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;

        String[] values = trimmed.split(",");
        if (values.length != 3) continue;
        try {
          Integer.parseInt(values[0].trim());
          new BigDecimal(values[2].trim());
        } catch (NumberFormatException e) {
          continue;
        }
        scores.add(List.of(values[0].trim(), values[1].trim(), values[2].trim()));
      }
    } catch (Exception e) {
      // File may not exist yet — return empty list silently
    }

    scores.sort(
        (a, b) -> {
          BigDecimal scoreA = new BigDecimal(a.get(2));
          BigDecimal scoreB = new BigDecimal(b.get(2));
          return scoreB.compareTo(scoreA);
        });
    return scores;
  }
}
