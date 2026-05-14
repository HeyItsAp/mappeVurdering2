package ntnu.gruppe21.filehandler;

import java.nio.file.Files;
import java.nio.file.Paths;
import ntnu.gruppe21.Exchange;
import ntnu.gruppe21.Player;

/**
 * This class implements both Filehandlers {@link FilehandlerExchange} and {@link FilehandlerPlayer}
 * to manage saves by invoking static methods from both.
 *
 * <p>Mainly handles folders, creating them and places saved csv data into those folders. When
 * loading, it uses the static methods from {@link FilehandlerPlayer} and {@link
 * FilehandlerExchange} to return objects
 */
public class SaveManager {
  private static final String saves_root = "src/main/resources/saves";
  private final String folderPath;

  /**
   * Constructor.
   *
   * @param saveName points to folder name containing csv files.
   */
  public SaveManager(String saveName) {
    this.folderPath = saves_root + '/' + saveName;
  }

  /** Test constructor — accepts a full path as-is */
  public SaveManager(String saveName, boolean fullPath) {
    this.folderPath = fullPath ? saveName : saves_root + "/" + saveName;
  }

  /**
   * Performs a full save of the exchange and player data. Creates folder, csv files through static
   * methods and then prints out.
   *
   * @param player
   * @param exchange
   * @throws Exception
   */
  public void save(Player player, Exchange exchange) throws Exception {
    Files.createDirectories(Paths.get(folderPath));
    FilehandlerPlayer.savePlayerData(player, folderPath);
    FilehandlerExchange.saveExchangeData(exchange, folderPath);
    System.out.println("Game saved to: " + folderPath);
  }

  /**
   * Reads and returns Player object through static method.
   *
   * @return Player object
   */
  public Player loadPlayer() {
    return FilehandlerPlayer.getPlayerSavedData(folderPath);
  }

  /**
   * Reads and returns Exchange object through static method.
   *
   * @return Exchange object
   */
  public Exchange loadExchange() {
    return FilehandlerExchange.getSaveData(folderPath);
  }
}
