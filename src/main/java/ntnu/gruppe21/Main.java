package ntnu.gruppe21;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ntnu.gruppe21.view.Screen;

public class Main extends Application {
  public static void main(String[] args) {

    //    // Fragmentary simulation for periodic/random save after some actions:
    //    Player player = new Player.Builder("Adrian", new BigDecimal(200000),
    // Difficulty.EASY).build();
    //    Exchange exchange = FilehandlerExchange.getExchangeDataset("exchangeDataSet1");
    //
    //    exchange
    //        .getStockMap()
    //        .forEach(
    //            (s, stock) -> {
    //              System.out.println(s + ": " + stock);
    //            });
    //
    //    try {
    //      exchange.buy("HST", new BigDecimal(31.0), player).commit(player);
    //      exchange.buy("AAPL", new BigDecimal(31.0), player).commit(player);
    //      exchange.buy("MSFT", new BigDecimal(1.0), player).commit(player);
    //      exchange.sell("MSFT", new BigDecimal(1.0), player).commit(player);
    //    } catch (TransactionException e) {
    //      throw new RuntimeException(e);
    //    }
    //
    //    FilehandlerPlayer.savePlayerData(player, "src/main/resources/saves/testgetsaveslot");
    //
    //    Player savedPlayer =
    //        FilehandlerPlayer.getPlayerSavedData("src/main/resources/saves/testgetsaveslot");

    launch(args);
  }

  public void start(Stage stage) {
    Scene scene = new Scene(new Screen(), 1000, 700);
    stage.setScene(scene);
    stage.setTitle("Lets go gambling!");
    stage.setMaximized(true);
    stage.show();
  }
}
