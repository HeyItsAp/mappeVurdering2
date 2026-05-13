package ntnu.gruppe21.filehandler;

import ntnu.gruppe21.*;
import ntnu.gruppe21.transaction.Purchase;
import ntnu.gruppe21.transaction.Sale;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class responsible for handling file input and output operations related to {@link
 * Player} and each of its attributes.
 *
 * <p>This class provides functionality for: Saving Player data to CSV files
 * which includes saving Player's {@link Portfolio} and {@link TransactionArchive}
 * and Loading Player data from a CSV which
 * should include all shares of stock and respective transactions (an others)
 * to form the {@link Player}s {@link Portfolio} and {@link TransactionArchive}.
 *
 * <p>All methods are static and the class is not intended to be instantiated.
 */
public class FilehandlerPlayer {
    /**
     * Saves the current state of an {@link Player} to a CSV file in a designated save folder unqiue to player.
     * Similar Function at {@link FilehandlerExchange}.
     *
     * <p>The file will include:
     * Playername, starting money, current money, list of shares {@link Portfolio} and
     * list of transactions {@link TransactionArchive}
     *
     * <p>The price history is stored as a semicolon-separated list in a single column.
     *
     * @param player {@link Player} object to be saved
     * @return the filename (without path) of the saved file
     */
    public static boolean savePlayerData(Player player, String folderPath) {
        String filename = folderPath + "/player.csv ";
        try (PrintWriter pw = new PrintWriter(filename)) {
            pw.println("# SaveFile for: " + player.getName());

            pw.println();
            pw.println("# Player metadata:");
            pw.println("# name,startingMoney,currentMoney");
            pw.println(player.getName() + "," + player.getStartingMoney() + "," + player.getCurrentMoney());
            pw.println();


            pw.println();
            pw.println("# First dataset, Portofolio/Shares:");
            pw.println("# stock,quantity,purchasePrice");
            pw.println();

            player.getPortfolio().getShares()
                    .forEach(
                            (value) -> {
                                Stock stock = value.getStock();
                                String stockPrices =
                                        stock.getPriceHistory().stream()
                                                .map(BigDecimal::toString)
                                                .collect(Collectors.joining(";"));

                                pw.println(stock.getCompany() + "," + stock.getSymbol() + "," + stockPrices + "," + value.getQuantity());
                            });
            pw.println();
            pw.println("# Second dataset, Purchases:");
            pw.println("# stock,quantity,purchasePrice,week");
            pw.println();
            player.getTransactionArchive().getPurchases()
                    .forEach(
                            (value) -> {
                                Share purchasedShare = value.getShare();

                                Stock stock = purchasedShare.getStock();
                                String stockPrices =
                                        stock.getPriceHistory().stream()
                                                .map(BigDecimal::toString)
                                                .collect(Collectors.joining(";"));

                                pw.println(stock.getCompany() + "," + stock.getSymbol() + "," + stockPrices + "," + purchasedShare.getPurchasePrice() + "," + value.getWeek());
                            });

            pw.println(" ");
            pw.println("# Thrid dataset, Sales:");
            pw.println("# stock,quantity,purchasePrice,week");
            pw.println(" ");
            player.getTransactionArchive().getSales()
                    .forEach(
                            (value) -> {
                                Share purchasedShare = value.getShare();

                                Stock stock = purchasedShare.getStock();
                                String stockPrices =
                                        stock.getPriceHistory().stream()
                                                .map(BigDecimal::toString)
                                                .collect(Collectors.joining(";"));

                                pw.println(stock.getCompany() + "," + stock.getSymbol() + "," + stockPrices + "," + purchasedShare.getPurchasePrice() + "," + value.getWeek());
                            });


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("Attempt to save file: " + filename + ".csv; At resources/saves");
        return true;
    }

    /**
     * Loads a previously saved Player CSV file in a designated save folder
     *
     * <p> Data is split into paragraphs:
     *     First LINE will be the players metadata: Name,startingMoney,currentMoney
     *     First paragraph will contain shares: 1,company,symbol,{prices},quantity,purchasePrice;
     *     Second paragraph will contain Purchases: 2,company,symbol,quantity,{prices},purchasePrice,week
     *     Thrid paragraph will contain Sales: 3,company,symbol,stock,quantity,{prices},purchasePrice,week
     *
     * <p>The first price is used as the initial price when creating the {@link Stock}, and the
     * remaining prices are added to reconstruct the full price history.
     *
     * <p>Lines starting with '#' and empty lines are ignored.
     *
     * @param folderPath the name of the file (without extension) to load
     * @return a reconstructed {@link Exchange} object based on the saved data
     */
    public static Player getPlayerSavedData(String folderPath) {
        String csvfile = folderPath + "/player.csv";
        String line = "";

        Portfolio portfolio = new Portfolio();
        TransactionArchive transactionArchive = new TransactionArchive();
        String playerName = null;
        BigDecimal startingMoney = null;
        BigDecimal currentMoney = null;
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

                if(values.length == 3){
                    playerName = values[0];
                    startingMoney = BigDecimal.valueOf(Long.parseLong(values[1]));
                    currentMoney = BigDecimal.valueOf(Long.parseLong(values[2]));
                    continue;
                }

                String[] stockPricesString = values[3].split(";");
                List<BigDecimal> stockPrices =
                        Arrays.stream(stockPricesString).map(BigDecimal::new).toList();
                Stock savedStock = new Stock(values[0], values[1], stockPrices.getFirst());
                for (int i = 1; i < stockPrices.size(); i++) {
                    savedStock.addNewSalesPrice(stockPrices.get(i));
                }
                BigDecimal quantity = BigDecimal.valueOf(Long.parseLong(values[4]));
                BigDecimal purchasePrice = BigDecimal.valueOf(Long.parseLong(values[5]));
                Share savedShare = new Share(savedStock, quantity, purchasePrice);
                switch (values[0]) {
                    case "1" -> portfolio.addShare(savedShare);
                    case "2" -> {
                        if (values.length != 7){throw new RuntimeException("Corrupted line");}
                        int savedWeek = Integer.valueOf(values[6]);
                        Purchase savedPurchase = new Purchase(savedShare, savedWeek);
                    }
                    case "3" -> {
                        if (values.length != 7){throw new RuntimeException("Corrupted line");}
                        int savedWeek = Integer.valueOf(values[6]);
                        Sale savedSale = new Sale(savedShare, savedWeek);
                    }
                    default -> throw new RuntimeException("Unexpected Line occurred");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Player(playerName, startingMoney,currentMoney,portfolio,transactionArchive);
    }
}
