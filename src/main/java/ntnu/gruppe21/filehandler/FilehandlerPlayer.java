package ntnu.gruppe21.filehandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ntnu.gruppe21.model.*;
import ntnu.gruppe21.model.gameEngine.Difficulty;
import ntnu.gruppe21.model.gameEngine.challenges.ChallengeType;
import ntnu.gruppe21.model.transaction.Purchase;
import ntnu.gruppe21.model.transaction.Sale;
import ntnu.gruppe21.model.transaction.TransactionArchive;

/**
 * Utility class responsible for handling file input and output operations related to {@link Player}
 * and each of its attributes.
 *
 * <p>This class provides functionality for: Saving Player data to CSV files which includes saving
 * Player's {@link Portfolio} and {@link TransactionArchive} and Loading Player data from a CSV
 * which should include all shares of stock and respective transactions (an others) to form the
 * {@link Player}s {@link Portfolio} and {@link TransactionArchive}.
 *
 * <p>All methods are static and the class is not intended to be instantiated.
 */
public class FilehandlerPlayer {

  /**
   * Saves the current state of an {@link Player} to a CSV file in a designated save folder unique
   * to player. Create that folder if not created.
   *
   * <p>Similar Function at {@link FilehandlerExchange}. Usually invoked on {@link
   * ntnu.gruppe21.filehandler.SaveManager}
   *
   * <p>The file will include: PlayerName, starting money, current money, difficulty, list of shares
   * {@link Portfolio} and list of transactions {@link TransactionArchive}
   *
   * <p>The price history is stored as a semicolon-separated list in a single column.
   *
   * @param player {@link Player} object to be saved
   * @param folderPath Full path to saves, provided by {@link ntnu.gruppe21.filehandler.SaveManager}
   * @return true of false based on if succeed
   */
  public static boolean savePlayerData(Player player, String folderPath) {
    String filename = folderPath + "/player.csv";
    try (PrintWriter pw = new PrintWriter(filename)) {
      pw.println("# SaveFile for: " + player.getName());

      pw.println();
      pw.println("# Player metadata:");
      pw.println("# name,startingMoney,currentMoney,{challenges}");
      pw.println(
          player.getName()
              + ","
              + player.getStartingMoney()
              + ","
              + player.getCurrentMoney()
              + ","
              + player.getDifficulty().toString()
              + ","
              + player.getChallengeManager().saveChallenges());
      pw.println();

      pw.println();
      pw.println("# First dataset, Portfolio/Shares:");
      pw.println("# stock,quantity,purchasePrice");
      player
          .getPortfolio()
          .getShares()
          .forEach(
              (value) -> {
                Stock stock = value.getStock();
                String stockPrices =
                    stock.getPriceHistory().stream()
                        .map(BigDecimal::toString)
                        .collect(Collectors.joining(";"));

                pw.println(
                    "1,"
                        + stock.getCompany()
                        + ","
                        + stock.getSymbol()
                        + ","
                        + stockPrices
                        + ","
                        + value.getQuantity()
                        + ","
                        + value.getPurchasePrice());
              });
      pw.println();
      pw.println("# Second dataset, Purchases:");
      pw.println("# stock,quantity,purchasePrice,week");
      player
          .getTransactionArchive()
          .getPurchases()
          .forEach(
              (value) -> {
                Share purchasedShare = value.getShare();

                Stock stock = purchasedShare.getStock();
                String stockPrices =
                    stock.getPriceHistory().stream()
                        .map(BigDecimal::toString)
                        .collect(Collectors.joining(";"));

                pw.println(
                    "2,"
                        + stock.getCompany()
                        + ","
                        + stock.getSymbol()
                        + ","
                        + stockPrices
                        + ","
                        + purchasedShare.getQuantity()
                        + ","
                        + purchasedShare.getPurchasePrice()
                        + ","
                        + value.getWeek());
              });

      pw.println(" ");
      pw.println("# Third dataset, Sales:");
      pw.println("# stock,quantity,purchasePrice,week");
      player
          .getTransactionArchive()
          .getSales()
          .forEach(
              (value) -> {
                Share purchasedShare = value.getShare();

                Stock stock = purchasedShare.getStock();
                String stockPrices =
                    stock.getPriceHistory().stream()
                        .map(BigDecimal::toString)
                        .collect(Collectors.joining(";"));

                pw.println(
                    "3,"
                        + stock.getCompany()
                        + ","
                        + stock.getSymbol()
                        + ","
                        + stockPrices
                        + ","
                        + purchasedShare.getQuantity()
                        + ","
                        + purchasedShare.getPurchasePrice()
                        + ","
                        + value.getWeek());
              });

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    System.out.println("Attempt to save file: " + filename + "; At resources/saves");
    return true;
  }

  /**
   * Loads a previously saved Player CSV file in a designated save folder. Usually invoke on {@link
   * ntnu.gruppe21.filehandler.SaveManager}
   *
   * <p>Data is split into paragraphs: First LINE will be the players metadata:
   * Name,startingMoney,currentMoney,difficulty,{challengeType}First paragraph will contain shares:
   * 1,company,symbol,{prices},quantity,purchasePrice; Second paragraph will contain Purchases:
   * 2,company,symbol,quantity,{prices},purchasePrice,week Third paragraph will contain Sales:
   * 3,company,symbol,stock,quantity,{prices},purchasePrice,week
   *
   * <p>The first price is used as the initial price when creating the {@link Stock}, and the
   * remaining prices are added to reconstruct the full price history.
   *
   * <p>Lines starting with '#' and empty lines are ignored.
   *
   * @param folderPath Full path, provided by {@link SaveManager}
   * @return a reconstructed {@link Exchange} object based on the saved data
   */
  public static Player getPlayerSavedData(String folderPath) {
    String csvfile = folderPath + "/player.csv";
    String line = "";

    Player player = null;
    Portfolio portfolio = new Portfolio();
    TransactionArchive transactionArchive = new TransactionArchive();
    String playerName = null;
    BigDecimal startingMoney = null;
    BigDecimal currentMoney = null;
    Difficulty difficulty = null;
    Map<ChallengeType, Integer> challengeMetadataMap = new HashMap<>();
    int savedTotalCompletions = 0;
    boolean metadataLinePassed = false;
    try {
      BufferedReader br = new BufferedReader(new FileReader(csvfile));
      while ((line = br.readLine()) != null) {
        String trimmedLine = line.trim();

        if (trimmedLine.isEmpty()) {
          continue;
        }
        if (trimmedLine.startsWith("#")) {
          continue;
        }

        String[] values = trimmedLine.split(",");

        if (values.length == 5 && !metadataLinePassed) {
          metadataLinePassed = true;
          playerName = values[0];
          startingMoney = BigDecimal.valueOf(Double.parseDouble(values[1]));
          currentMoney = BigDecimal.valueOf(Double.parseDouble(values[2]));
          difficulty = Difficulty.valueOf(values[3]);

          String[] challengeString = values[4].split("\\|");
          int startIdx = 0;
          try {
            savedTotalCompletions = Integer.parseInt(challengeString[0].trim());
            startIdx = 1;
          } catch (NumberFormatException ignored) {
          }
          for (int i = startIdx; i < challengeString.length; i++) {
            String[] challengeMetadata = challengeString[i].split(";");
            ChallengeType challengeType = ChallengeType.valueOf(challengeMetadata[0]);
            Integer timeCompleted = Integer.valueOf(challengeMetadata[1]);
            challengeMetadataMap.put(challengeType, timeCompleted);
            if (startIdx == 0) savedTotalCompletions += timeCompleted;
          }
          continue;
        }

        String[] stockPricesString = values[3].split(";");
        List<BigDecimal> stockPrices =
            Arrays.stream(stockPricesString).map(BigDecimal::new).toList();
        Stock savedStock = new Stock(values[2], values[1], stockPrices.getFirst());
        for (int i = 1; i < stockPrices.size(); i++) {
          savedStock.addNewSalesPrice(stockPrices.get(i));
        }
        int quantity = (int) Double.parseDouble(values[4]);
        BigDecimal purchasePrice = BigDecimal.valueOf(Double.parseDouble(values[5]));
        Share savedShare = new Share(savedStock, quantity, purchasePrice);
        switch (values[0]) {
          case "1" -> portfolio.addShare(savedShare);
          case "2" -> {
            if (values.length != 7) {
              throw new RuntimeException("Corrupted line");
            }
            int savedWeek = Integer.valueOf(values[6]);
            Purchase savedPurchase = new Purchase(savedShare, savedWeek);
            transactionArchive.add(savedPurchase);
          }
          case "3" -> {
            if (values.length != 7) {
              throw new RuntimeException("Corrupted line");
            }
            int savedWeek = Integer.valueOf(values[6]);
            Sale savedSale = new Sale(savedShare, savedWeek);
            transactionArchive.add(savedSale);
          }
          default -> throw new RuntimeException("Unexpected Line occurred");
        }
      }
      player =
          new Player.Builder(playerName, startingMoney, difficulty)
              .difficulty(difficulty)
              .currentMoney(currentMoney)
              .portfolio(portfolio)
              .transactionArchive(transactionArchive)
              .build();
      player.getChallengeManager().parseChallenges(challengeMetadataMap, player);
      player.getChallengeManager().setTotalCompletions(savedTotalCompletions);
    } catch (Exception e) {
      System.out.println("Error when saving. Remember to add challenges after player creation");
      e.printStackTrace();
      throw new RuntimeException();
    }
    return player;
  }

  /**
   * Returns string of names of saves in saves folder
   *
   * @return list of names for folder in resoucres/saves
   */
  public static List<String> getPlayerSaveOptions() {
    return getPlayerSaveOptions("src/main/resources/saves");
  }

  /**
   * Returns string of names of saves in the given root folder.
   *
   * @param rootPath path to the saves root directory
   * @return list of folder names in that directory
   */
  public static List<String> getPlayerSaveOptions(String rootPath) {
    Path pathToDataset = Path.of(rootPath);
    List<String> namesOfSaves = null;
    try (Stream<Path> stream = Files.list(pathToDataset)) {
      namesOfSaves =
          stream.filter(Files::isDirectory).map(p -> p.getFileName().toString()).toList();
    } catch (IOException e) {
      throw new RuntimeException("Getting options failed: " + e);
    }
    return namesOfSaves;
  }
}
