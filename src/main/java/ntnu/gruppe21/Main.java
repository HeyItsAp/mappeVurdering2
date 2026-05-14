package ntnu.gruppe21;

import java.math.BigDecimal;
import ntnu.gruppe21.filehandler.FilehandlerExchange;
import ntnu.gruppe21.filehandler.FilehandlerPlayer;
import ntnu.gruppe21.transaction.TransactionException;

public class Main {
  public static void main(String[] args) {

    // Fragmentry Simulation for periodic/random save after some actions:
    Player player = new Player("Adrian", new BigDecimal(20000));
    Exchange exchange = FilehandlerExchange.getExchangeData();

    exchange
        .getStockMap()
        .forEach(
            (s, stock) -> {
              System.out.println(s + ": " + stock);
            });

    try {
      exchange.buy("HST", new BigDecimal(31.0), player).commit(player);
      exchange.buy("AAPL", new BigDecimal(31.0), player).commit(player);
      exchange.buy("MSFT", new BigDecimal(1.0), player).commit(player);
      exchange.sell("MSFT", new BigDecimal(1.0), player).commit(player);
    } catch (TransactionException e) {
      throw new RuntimeException(e);
    }

    FilehandlerPlayer.savePlayerData(player, "src/main/resources/saves/testgetsaveslot");

    Player savedPlayer =
        FilehandlerPlayer.getPlayerSavedData("src/main/resources/saves/testgetsaveslot");
  }
}
