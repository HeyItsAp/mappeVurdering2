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
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import ntnu.gruppe21.Exchange;
import ntnu.gruppe21.Stock;
import ntnu.gruppe21.gameEngine.Difficulty;

/**
 * Utility class responsible for handling file input and output operations related to {@link
 * Exchange} and {@link Stock} objects.
 *
 * <p>This class provides functionality for: Saving exchange data to CSV files Loading initial
 * exchange data from CSV Loading previously saved exchange data
 *
 * <p>All methods are static and the class is not intended to be instantiated.
 */
public class FilehandlerExchange {
  // Root path to datasets. Used for importing of datasets
  private static final String DATASETS_ROOT = "src/main/resources/datasets/";

  /**
   * Saves the current state of an {@link Exchange} to a CSV file in a designated save folder.
   * Usually invoke on {@link SaveManager}
   *
   * <p>The file will be stored in a folder in {@code resources/saves/} directory. File includes
   * exchange name and current week (as comments) All stocks with their symbol, company name, and
   * price history
   *
   * <p>Uses comma as the seperator
   *
   * @param exchange the {@link Exchange} object to be saved
   * @param folderPath The full path which INCLUDES THE SLOT FOLDER.
   * @return the filename (without path) of the saved file
   */
  public static boolean saveExchangeData(Exchange exchange, String folderPath) {
    String filename = folderPath + "/exchangeData.csv";
    try (PrintWriter pw = new PrintWriter(filename)) {
      pw.println("# Save on Exchange: " + exchange.getName() + ", Week: " + exchange.getWeek());
      pw.println("# Ticker,Name,{Prices}");
      pw.println(exchange.getName());
      pw.println(" ");

      exchange
          .getStockMap()
          .forEach(
              (key, value) -> {
                String prices =
                    value.getPriceHistory().stream()
                        .map(BigDecimal::toString)
                        .collect(Collectors.joining(";"));

                pw.println(value.getSymbol() + "," + value.getCompany() + "," + prices);
              });

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    System.out.println("Attempt to save to: " + filename);
    return true;
  }

  /**
   * Loads initial exchange data from a predefined CSV file REQUIRED method in del 2 Probably
   * uploaded on BlackBoard on some point
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
  public static Exchange getExchangeData() {
    String csvfile = "src/main/resources/datasets/exchangeDataSet1.csv";
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
    return new Exchange("ExchangeFromFile", listOfStocks, Difficulty.EASY);
  }

  /**
   * Loads a previously saved exchange state from a CSV file in a designated folder Usually invoke
   * on {@link SaveManager}
   *
   * <p>Each data row must follow the format: Ticker,CompanyName,price1;price2;price3;...
   *
   * <p>The first price is used as the initial price when creating the {@link Stock}, and the
   * remaining prices are added to reconstruct the full price history.
   *
   * <p>Lines starting with '#' and empty lines are ignored.
   *
   * @param folderPath The full path which INCLUDES THE SLOT FOLDER.
   * @return a reconstructed {@link Exchange} object based on the saved data
   */
  public static Exchange getSaveData(String folderPath) {
    String csvfile = folderPath + "/exchangeData.csv";
    String line = "";

    List<Stock> listOfStocks = new ArrayList<>();
    String exchangeName = "";
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

        if (values.length == 1) {
          exchangeName = values[0];
          continue;
        }

        String[] savedPricesString = values[2].split(";");
        List<BigDecimal> savedPrices =
            Arrays.stream(savedPricesString).map(BigDecimal::new).toList();
        Stock stock = new Stock(values[0], values[1], savedPrices.getFirst());
        for (int i = 1; i < savedPrices.size(); i++) {
          stock.addNewSalesPrice(savedPrices.get(i));
        }
        listOfStocks.add(stock);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new Exchange(exchangeName, listOfStocks, Difficulty.EASY);
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
          new BigDecimal(values[2].trim()); // price must be a number
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
}
