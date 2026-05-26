package ntnu.gruppe21.filehandler;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import ntnu.gruppe21.model.Exchange;
import ntnu.gruppe21.model.Player;

/**
 * This class implements both filehandlers {@link FilehandlerExchange} and {@link FilehandlerPlayer}
 * to manage saves by invoking static methods from both.
 *
 * <p>Mainly handles folders, creating them and places saved csv data into those folders. When
 * loading, it uses the static methods from {@link FilehandlerPlayer} and {@link
 * FilehandlerExchange} to return objects
 */
public class SaveManager {
  private static final String saves_root = "src/main/resources/saves";
  private final String folderSlot;

  /**
   * Constructor.
   *
   * @param saveName points to folder name containing csv files.
   */
  public SaveManager(String saveName) {
    this.folderSlot = saveName;
  }

  /**
   * Test constructor — accepts a full path as-is
   *
   * @param saveName name of the folder. SaveSlot
   * @param fullPath boolean to add saves_root before SaveSlot
   */
  public SaveManager(String saveName, boolean fullPath) {
    this.folderSlot = fullPath ? saveName : saves_root + "/" + saveName;
  }

  /**
   * Performs a full save of the exchange and player data. Creates folder, csv files through static
   * methods and then prints out.
   *
   * @param player {@link Player} object to be saved
   * @param exchange {@link Exchange} object to be saved
   * @throws Exception if anything goes wrong
   */
  public void save(Player player, Exchange exchange) throws Exception {
    Files.createDirectories(Paths.get(folderSlot));
    FilehandlerPlayer.savePlayerData(player, folderSlot);
    FilehandlerExchange.saveExchangeData(exchange, folderSlot);
    System.out.println("Game saved to: " + folderSlot);
  }

  /**
   * Getter, uses public static methods.
   *
   * @return List of String containing saves folderName
   */
  public List<String> getSaveOptions() {
    return FilehandlerPlayer.getPlayerSaveOptions();
  }

  /**
   * Getter, uses public static methods.
   *
   * @return List of String containing names of datasets in resoucres/datasets
   */
  public List<String> getDataSetOptions() {
    return FilehandlerExchange.getExchangeDatasetOptions();
  }

  /**
   * Reads and returns Player object through static method.
   *
   * @return {@link Player} object
   */
  public Player loadPlayer() {
    return FilehandlerPlayer.getPlayerSavedData(folderSlot);
  }

  /**
   * Reads and returns Exchange object through static method.
   *
   * @return {@link Exchange} object
   */
  public Exchange loadExchange() {
    return FilehandlerExchange.getExchangeSaveData(folderSlot);
  }

  /**
   * Gets new exchange based on a Dataset in resoucres/datasets
   *
   * @param nameOfSet name of the csv file in resoucres/datasets
   * @return complete {@link Exchange} object
   */
  public Exchange loadExchangeDataset(String nameOfSet) {
    return FilehandlerExchange.getExchangeDataset(nameOfSet);
  }
}
