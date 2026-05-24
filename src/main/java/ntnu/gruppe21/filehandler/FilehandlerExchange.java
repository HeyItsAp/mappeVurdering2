package ntnu.gruppe21.filehandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import ntnu.gruppe21.Exchange;
import ntnu.gruppe21.Stock;
import ntnu.gruppe21.gameEngine.Difficulty;
import ntnu.gruppe21.gameEngine.strategies.PriceStrategy;
import ntnu.gruppe21.gameEngine.strategies.StrategyRegister;
import ntnu.gruppe21.gameEngine.strategies.marketsimulator.MarketSimulator;

/**
 * Responsible for handling file input and output operations related to {@link Exchange} and {@link
 * Stock} objects.
 *
 * <p>This class provides functionality for: Saving exchange data to CSV files Loading initial
 * exchange data from CSV Loading previously saved exchange data
 *
 * <p>All methods are static and the class is not intended to be instantiated.
 */
public class FilehandlerExchange {
  // Root path to datasets. Used for importing of datasets
  private static final String DATASETS_ROOT = "src/main/resources/datasets/";
  private static final String SAVES_ROOT = "src/main/resources/saves/";

  /**
   * Saves the current state of an {@link Exchange} to a CSV file in a designated save folder.
   * Usually invoke on {@link SaveManager}
   *
   * <p>The file will be stored in a folder in {@code resources/saves/} directory. File includes
   * exchange name, difficulty during playtime and current week (as comments) All stocks with their
   * symbol, company name, and price history AND Extras stock attributes if the strategy implemented
   *
   * <p>Uses comma as the seperator
   *
   * @param exchange the {@link Exchange} object to be saved
   * @param folderName THE SLOT FOLDER NAME, not the path.
   * @return the filename (without path) of the saved file
   */
  public static boolean saveExchangeData(Exchange exchange, String folderName) {
    String folderSlotPath = SAVES_ROOT + "/" + folderName;
    try {
      Files.createDirectories(Paths.get(folderSlotPath));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    String filename = folderSlotPath + "/exchangeData.csv";
    PriceStrategy strategy = exchange.getStrategy();
    try (PrintWriter pw = new PrintWriter(filename)) {
      pw.println("# Save on Exchange: " + exchange.getName() + ", Week: " + exchange.getWeek());
      pw.println("# Ticker,Name,{Prices}");
      pw.println(
          exchange.getName()
              + ","
              + exchange.getWeek()
              + ","
              + exchange.getDifficulty().toString()
              + ","
              + strategy.getStrategyId());
      pw.println(" ");

      exchange
          .getStockMap()
          .forEach(
              (key, value) -> {
                String prices =
                    value.getPriceHistory().stream()
                        .map(BigDecimal::toString)
                        .collect(Collectors.joining(";"));

                String extras = strategy.saveStockExtras(value); // If any extras within stock.

                pw.println(
                    value.getSymbol() + "," + value.getCompany() + "," + prices + "," + extras);
              });

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    System.out.println("Attempt to save to: " + filename);
    return true;
  }

  /**
   * Loads initial exchange data from a predefined
   *
   * <p>The file is expected to be located at: {@code resources/Exchanges/exchangeDataSet1.csv}
   *
   * <p>Each valid line in the file should contain: Ticker,CompanyName,InitialPrice (As demonstrated
   * at del 2 in Mappe)
   *
   * <p>Lines starting with '#' and empty lines are ignored.
   *
   * @return a new {@link Exchange} object populated with stocks from the file
   */
  public static Exchange getExchangeDataset(String filename) {
    String folderLocationString = "src/main/resources/datasets/";
    String csvfile = folderLocationString + filename + ".csv";
    Path fullPath = Path.of(csvfile);
    if (!validFormat(fullPath)) {
      throw new RuntimeException("Attempted getting dataset " + filename + ".csv, is invalid");
    }

    String line = "";
    List<Stock> listOfStocks = new ArrayList<>();
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

        // Just a normal print out
        for (String value : values) {
          System.out.print(value.trim() + " ");
        }
        System.out.print("\n");

        Stock stock = new Stock(values[0], values[1], new BigDecimal(values[2]));
        listOfStocks.add(stock);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new Exchange.Builder("ExchangeFromFile").stockMap(listOfStocks).build();
  }

  /**
   * Loads a previously saved exchange state from a CSV file in a designated folder Usually invoke
   * on {@link SaveManager}
   *
   * <p>First row contains the Metadata: Name,week,difficulty,Strategy THIS ONLY APPLIES TO SAVES,
   * not regular/imported exchange data
   *
   * <p>Each data (expect the first) row must follow the format:
   * Ticker,CompanyName,price1;price2;price3;... Strategies might implement extra stock data, for
   * example {@link MarketSimulator}. Extras will then be inserted into a fourth row.
   * Ticker,CompanyName,price1;price2;price3,0|21|9|0
   *
   * <p>The first price is used as the initial price when creating the {@link Stock}, and the
   * remaining prices are added to reconstruct the full price history.
   *
   * <p>Lines starting with '#' and empty lines are ignored.
   *
   * @param folderName THE SLOT FOLDER.
   * @return a reconstructed {@link Exchange} object based on the saved data
   */
  public static Exchange getExchangeSaveData(String folderName) {
    String csvfile = SAVES_ROOT + "/" + folderName + "/exchangeData.csv";
    String line = "";

    Exchange exchange = null;
    List<Stock> listOfStocks = new ArrayList<>();
    String exchangeName = "";
    Difficulty difficulty = null;
    PriceStrategy strategy = null;
    boolean basedMetaDataline = false;
    int week = 1;
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

        // Just a normal print out
        for (String value : values) {
          System.out.print(value.trim() + " ");
        }
        System.out.print("\n");

        if (values.length == 4 && !basedMetaDataline) {
          basedMetaDataline = true;
          exchangeName = values[0];
          week = Integer.parseInt(values[1]);
          difficulty = Difficulty.valueOf(values[2]);
          strategy = StrategyRegister.fromId(values[3]);
          continue;
        }

        String[] savedPricesString = values[2].split(";");
        ArrayList<BigDecimal> savedPrices = new ArrayList<>();
        Arrays.stream(savedPricesString).map(BigDecimal::new).forEach(savedPrices::add);

        Stock stock = strategy.createStock(values[0], values[1], savedPrices);
        listOfStocks.add(stock);

        // if there are extra stuff, add it to stock.
        if (values.length == 4 && !values[3].isBlank()) {
          strategy.deserializeStockExtras(stock, values[3]);
        }
      }
      exchange =
          new Exchange.Builder(exchangeName)
              .strategy(strategy)
              .difficulty(difficulty)
              .week(week)
              .stockMap(listOfStocks)
              .build();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return exchange;
  }

  /**
   * Method to import your own Exchange Data in a valid csv format that resembles ours.
   *
   * <p>Should be able to access the systems file explorer, search for a csv file, add it and be
   * sent into the {@link resources/datasets} folder. This method cannot be Unit tested as it
   * incorporates GUI dialogs which cannot be tested.
   *
   * @return true/false based on if it worked or not.
   */
  public static Boolean importExternalData() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Import Exchange Data");
    fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));

    int result = fileChooser.showOpenDialog(null);
    if (result != JFileChooser.APPROVE_OPTION) {
      System.out.println("Import cancelled.");
      return false;
    }

    Path chosen = fileChooser.getSelectedFile().toPath();

    if (!validFormat(chosen)) {
      System.out.println("Invalid file format: " + chosen.getFileName());
      return false;
    }

    try {
      Path saved = copyToDatasets(chosen);
      System.out.println("Imported to: " + saved);
      return true; // reuse your existing load logic
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Helper function for {@link #importExternalData()} method that checks if attempted import is off
   * valid format.
   *
   * <p>Valid format should be
   *
   * <ul>
   *   <li>At least 5 rows of data
   *   <li>Each row of data is separated by comma and are of three columns
   *   <li>Third column is parsable to BigDecimal
   * </ul>
   *
   * @return true/false based on if the selected is valid format or not
   */
  public static boolean validFormat(Path filePath) {
    try (BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()))) {
      String line;
      int lineCount = 0;

      while ((line = br.readLine()) != null) {
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;

        String[] values = trimmed.split(",");

        if (values.length != 3) return false;

        try {
          BigDecimal importedNumber = new BigDecimal(values[2].trim()); // price must be a number
          if (importedNumber.compareTo(BigDecimal.ZERO) < 0) {
            throw new NumberFormatException();
          }
        } catch (NumberFormatException e) {
          return false;
        }

        lineCount += 1;
      }
      return lineCount >= 5; // should be atleast 5 valid lines

    } catch (IOException e) {
      return false;
    }
  }

  /**
   * Helper function for {@link #importExternalData()} method that copies the selected csv file to
   * the {@link resources/datasets} folder to be selected later.
   *
   * @param source Selected file during ui file selecting
   * @return Destination
   * @throws IOException, if something wrong. Catched in {@link #importExternalData()} method
   */
  public static Path copyToDatasets(Path source) throws IOException {
    Path destination = Paths.get(DATASETS_ROOT + source.getFileName());
    Files.createDirectories(Paths.get(DATASETS_ROOT));
    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    return destination;
  }

  /** Returns a string list of valid datasets in {@link resources/datasets}. */
  public static List<String> getExchangeDatasetOptions() {
    Path pathToDataset = Path.of(DATASETS_ROOT);
    List<String> namesOfDatasets = null;
    try (Stream<Path> stream = Files.list(pathToDataset)) {
      namesOfDatasets =
          stream.filter(Files::isRegularFile).map(p -> p.getFileName().toString()).toList();
    } catch (IOException e) {
      throw new RuntimeException("Getting options failed: " + e);
    }
    return namesOfDatasets;
  }
}
