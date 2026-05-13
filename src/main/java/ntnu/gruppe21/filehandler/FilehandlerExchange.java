package ntnu.gruppe21.filehandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import ntnu.gruppe21.Exchange;
import ntnu.gruppe21.Stock;

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

  /**
   * Saves the current state of an {@link Exchange} to a CSV file in a designated save folder.
   *
   * <p>The file will be stored in a folder in {@code resources/saves/} directory. File includes exchange
   * name and current week (as comments) All stocks with their symbol, company name, and price
   * history
   *
   * <p>Uses comma as the seperator
   *
   * @param exchange the {@link Exchange} object to be saved
   * @return the filename (without path) of the saved file
   */
  public static String saveExchangeData(Exchange exchange, String folderPath) {
    String filename = folderPath + "exchangeData.csv";
    try (PrintWriter pw = new PrintWriter(filename)) {
      pw.println("# Save on Exchange: " + exchange.getName() + ", Week: " + exchange.getWeek());
      pw.println("# Ticker,Name,{Prices}");
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
    }
    System.out.println("Attempt to save file: " + filename + ".csv; At resources/saves");
    return filename;
  }

  /**
   * Loads initial exchange data from a predefined CSV file REQUIRED method in del 2 Probably
   * uploaded on BlackBoard on some point
   *
   * <p>The file is expected to be located at: {@code resources/Exchanges/exchangeData.csv}
   *
   * <p>Each valid line in the file should contain: Ticker,CompanyName,InitialPrice (As demonstrated
   * at del 2 in Mappe)
   *
   * <p>Lines starting with '#' and empty lines are ignored.
   *
   * @return a new {@link Exchange} object populated with stocks from the file
   */
  public static Exchange getExchangeData() {
    String csvfile = "src/main/resources/datasets/exchangeData.csv";
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
    return new Exchange("ExchangeFromFile", listOfStocks);
  }

  /**
   * Loads a previously saved exchange state from a CSV file in a designated folder
   **
   * <p>Each data row must follow the format: Ticker,CompanyName,price1;price2;price3;...
   *
   * <p>The first price is used as the initial price when creating the {@link Stock}, and the
   * remaining prices are added to reconstruct the full price history.
   *
   * <p>Lines starting with '#' and empty lines are ignored.
   *
   * @param folderPath the name of the file (without extension) to load
   * @return a reconstructed {@link Exchange} object based on the saved data
   */
  public static Exchange getSaveData(String folderPath) {
    String csvfile = folderPath + "exchangeData.csv";
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
    return new Exchange("Save:" + filename, listOfStocks);
  }
}
