package ntnu.gruppe21.filehandler;

import ntnu.gruppe21.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
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
     * Saves the current state of an {@link Player} to a CSV file. Similar Function at {@link FilehandlerExchange}.
     *
     * <p>The file will be stored in the {@code resources/saves/} directory and will include:
     * Playername, starting money, current money, list of shares {@link Portfolio} and
     * list of transactions {@link TransactionArchive}
     *
     * <p>The price history is stored as a semicolon-separated list in a single column.
     *
     * @param Player {@link Player} object to be saved
     * @return the filename (without path) of the saved file
     */
    public static String savePlayerData(Player player) {
        return "";
    }

    /**
     * Loads a previously saved Player CSV file.
     *
     * <p>The file is expected to be located in: {@code resources/saves/}
     *
     * <p> Data is split into paragraphs:
     *     First paragraph will contain shares: Stock,quantity,purchasePrice;
     *     Second paragraph will contain Purchases: stock,quantity,purchasePrice,week
     *     Thrid paragraph will contain Sales: stock,quantity,purchasePrice,week
     *
     * <p>The first price is used as the initial price when creating the {@link Stock}, and the
     * remaining prices are added to reconstruct the full price history.
     *
     * <p>Lines starting with '#' and empty lines are ignored.
     *
     * @param filename the name of the file (without extension) to load
     * @return a reconstructed {@link Exchange} object based on the saved data
     */
    public static Player getPlayerSavedData(String filename) {

        return null;
    }
}
