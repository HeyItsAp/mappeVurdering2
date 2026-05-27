package ntnu.gruppe21.controller;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import ntnu.gruppe21.filehandler.FilehandlerExchange;
import ntnu.gruppe21.filehandler.FilehandlerPlayer;
import ntnu.gruppe21.filehandler.HighScoreManager;
import ntnu.gruppe21.filehandler.SaveManager;
import ntnu.gruppe21.model.Exchange;
import ntnu.gruppe21.model.Player;
import ntnu.gruppe21.model.gameEngine.Difficulty;

/**
 * Handles the start-menu actions: starting a new game and loading an existing save. Both operations
 * produce a fully initialised {@link GameController} that views can use for the active session.
 */
public class StartMenuController {

  /**
   * A save slot paired with its computed score, used to display and sort saves on the start menu.
   *
   * @param slot the save-folder name
   * @param score the score calculated from that save's current state
   */
  public record SaveSummary(String slot, BigDecimal score) {}

  /**
   * Returns the names of all available save slots so the view can list them.
   *
   * @return list of save-folder names inside {@code resources/saves}
   */
  public List<String> getSaveOptions() {
    return FilehandlerPlayer.getPlayerSaveOptions();
  }

  /**
   * Returns the names of all available exchange datasets so the view can list them.
   *
   * @return list of dataset filenames inside {@code resources/datasets}
   */
  public List<String> getDatasetOptions() {
    return FilehandlerExchange.getExchangeDatasetOptions();
  }

  /**
   * Creates a brand-new game session from the given parameters.
   *
   * @param playerName the name chosen by the player
   * @param startingMoney the amount of money the player starts with
   * @param difficulty the chosen difficulty level
   * @param datasetName the dataset filename (without extension) to load the exchange from
   * @param saveSlot the save-folder name to use when saving this session
   * @return a {@link GameController} ready to use
   */
  public GameController startNewGame(
      String playerName,
      BigDecimal startingMoney,
      Difficulty difficulty,
      String datasetName,
      String saveSlot) {
    Player player = new Player.Builder(playerName, startingMoney, difficulty).build();
    player.getChallengeManager().generateChallenges(player);

    Exchange exchange = FilehandlerExchange.getExchangeDataset(datasetName);
    exchange.setDifficulty(difficulty);

    return new GameController(player, exchange, saveSlot);
  }

  /**
   * Returns all save slots with their computed scores, sorted by score descending. If a save cannot
   * be loaded or scored it is still included with a score of zero, so it always appears last.
   *
   * @return save summaries sorted highest score first
   */
  public List<SaveSummary> getSavesRanked() {
    return getSaveOptions().stream()
        .map(
            slot -> {
              try {
                SaveManager sm = new SaveManager(slot, false);
                Player player = sm.loadPlayer();
                Exchange exchange = sm.loadExchange();
                BigDecimal score = HighScoreManager.calculateFinalScore(exchange, player);
                return new SaveSummary(slot, score);
              } catch (Exception e) {
                return new SaveSummary(slot, BigDecimal.ZERO);
              }
            })
        .sorted(Comparator.comparing(SaveSummary::score).reversed())
        .toList();
  }

  /**
   * Loads an existing save and returns a ready-to-use game session.
   *
   * @param saveSlot the save-folder name to load from
   * @return a {@link GameController} restored from the save files
   */
  public GameController loadGame(String saveSlot) {
    SaveManager saveManager = new SaveManager(saveSlot, false);
    Player player = saveManager.loadPlayer();
    Exchange exchange = saveManager.loadExchange();
    return new GameController(player, exchange, saveSlot);
  }
}
