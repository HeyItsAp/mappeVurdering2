package ntnu.gruppe21.view.menus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import ntnu.gruppe21.model.Exchange;
import ntnu.gruppe21.model.Player;
import ntnu.gruppe21.model.Share;
import ntnu.gruppe21.model.Stock;
import ntnu.gruppe21.view.Screen;

public class TodayMenu extends VBox {
  private final Screen screen;

  public TodayMenu(Screen screen) {
    super(20);
    this.screen = screen;
    setStyle("-fx-padding: 20 20 30 20");

    Exchange exchange = screen.getController().getExchange();

    Label title = new Label("Week " + exchange.getWeek());
    title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

    Line line = new Line();
    line.setStartX(0);
    line.setStartY(0);
    line.setEndX(1000);
    line.setEndY(0);
    line.setStroke(Color.BLACK);
    line.setStrokeWidth(0.5);

    HBox body = new HBox(16);
    VBox.setVgrow(body, Priority.ALWAYS);

    VBox leftPane = buildLeftPane();
    HBox.setHgrow(leftPane, Priority.ALWAYS);

    VBox rightPane = buildPortfolioWatchlist();
    rightPane.prefWidthProperty().bind(body.widthProperty().multiply(0.25));

    body.getChildren().addAll(leftPane, rightPane);
    getChildren().addAll(title, line, body);
  }

  private VBox buildLeftPane() {
    VBox gainLoss = buildGainLossSection();
    VBox.setVgrow(gainLoss, Priority.ALWAYS);
    VBox pane = new VBox(16, buildNetWorthCard(), buildSummaryRow(), gainLoss);
    VBox.setVgrow(pane, Priority.ALWAYS);
    return pane;
  }

  private VBox buildNetWorthCard() {
    Player player = screen.getController().getPlayer();
    BigDecimal netWorth = player.getNetWorth();

    BigDecimal weeklyChange =
        player.getPortfolio().getShares().stream()
            .map(
                s ->
                    BigDecimal.valueOf(s.getQuantity())
                        .multiply(s.getStock().getLatestPriceChange()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal prevNetWorth = netWorth.subtract(weeklyChange);
    String sign = weeklyChange.signum() >= 0 ? "+" : "";
    String pct =
        prevNetWorth.signum() == 0
            ? "0.00"
            : String.format(
                "%.2f",
                weeklyChange
                    .divide(prevNetWorth, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)));

    Label heading = new Label("PORTFOLIO CHANGE THIS WEEK");
    heading.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa; -fx-font-weight: bold;");

    String color = weeklyChange.signum() >= 0 ? "#27ae60" : "#c0392b";
    Label changeLabel = new Label(sign + fmt(weeklyChange));
    changeLabel.setStyle(
        "-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

    Label pctLabel = new Label(sign + pct + "%  ·  Net worth: " + fmt(netWorth));
    pctLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #888;");

    VBox card = new VBox(6, heading, changeLabel, pctLabel);
    card.setStyle(
        """
        -fx-background-color: white;
        -fx-background-radius: 8;
        -fx-border-color: #c8c6c1;
        -fx-border-radius: 8;
        -fx-padding: 16;
        """);
    return card;
  }

  private HBox buildSummaryRow() {
    Player player = screen.getController().getPlayer();
    Exchange exchange = screen.getController().getExchange();

    String cash = fmt(player.getCurrentMoney());
    String invested = fmt(player.getPortfolio().getNetWorth());
    int stockCount = player.getPortfolio().countDistinctStock();

    String bestStock = bestPortfolioStock();

    HBox row = new HBox(10);
    for (VBox box :
        List.of(
            createStatBox("CASH", cash, "Available to invest"),
            createStatBox("INVESTED", invested, "Across " + stockCount + " stocks"),
            createStatBox("BEST STOCK", bestStock, "This week"))) {
      HBox.setHgrow(box, Priority.ALWAYS);
      box.setMaxWidth(Double.MAX_VALUE);
      row.getChildren().add(box);
    }
    return row;
  }

  private String bestPortfolioStock() {
    return screen.getController().getPlayer().getPortfolio().getShares().stream()
        .map(s -> s.getStock())
        .distinct()
        .max((a, b) -> a.getLatestPriceChange().compareTo(b.getLatestPriceChange()))
        .map(s -> s.getSymbol() + " " + fmtChange(s))
        .orElse("—");
  }

  private VBox buildGainLossSection() {
    Exchange exchange = screen.getController().getExchange();
    int limit = Math.min(4, exchange.getStockMap().size());

    List<Stock> gainers = limit > 0 ? exchange.getGainers(limit) : List.of();
    List<Stock> losers = limit > 0 ? exchange.getLosers(limit) : List.of();

    HBox row = new HBox(10);
    VBox.setVgrow(row, Priority.ALWAYS);

    VBox gainerPane = buildGainLossList("TOP GAINERS", gainers);
    VBox loserPane = buildGainLossList("TOP LOSERS", losers);
    HBox.setHgrow(gainerPane, Priority.ALWAYS);
    HBox.setHgrow(loserPane, Priority.ALWAYS);

    row.getChildren().addAll(gainerPane, loserPane);
    VBox section = new VBox(row);
    VBox.setVgrow(section, Priority.ALWAYS);
    return section;
  }

  private VBox buildGainLossList(String heading, List<Stock> stocks) {
    Label title = new Label(heading);
    title.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa; -fx-font-weight: bold;");

    VBox cards = new VBox(6);
    for (Stock s : stocks) {
      cards.getChildren().add(buildGainLossCard(s));
    }

    VBox pane = new VBox(8, title, cards);
    VBox.setVgrow(pane, Priority.ALWAYS);
    return pane;
  }

  private HBox buildGainLossCard(Stock stock) {
    Label symbolLabel = new Label(stock.getSymbol());
    symbolLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

    Label priceLabel = new Label(fmt(stock.getSalesPrice()));
    priceLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1a1a1a;");

    String changeStr = fmtChange(stock);
    boolean pos = stock.getLatestPriceChange().signum() >= 0;
    Label changeLabel = new Label(changeStr);
    changeLabel.setStyle(
        "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: "
            + (pos ? "#27ae60" : "#c0392b")
            + ";");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox card = new HBox(10, symbolLabel, priceLabel, spacer, changeLabel);
    card.setStyle(
        """
        -fx-padding: 10 12 10 12;
        -fx-background-color: white;
        -fx-background-radius: 8;
        -fx-border-color: #d0d0d0;
        -fx-border-radius: 8;
        """);
    card.setMaxWidth(Double.MAX_VALUE);
    card.setOnMouseClicked(ignored -> screen.showView(() -> new StockMenu(stock, screen)));
    card.setStyle(card.getStyle() + "-fx-cursor: hand;");
    return card;
  }

  record WatchRow(String symbol, String price, String change, Stock stock) {}

  private VBox buildPortfolioWatchlist() {
    Label heading = new Label("MY HOLDINGS");
    heading.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa; -fx-font-weight: bold;");

    TableView<WatchRow> table = new TableView<>();
    table.getStylesheets().add(getClass().getResource("/styles/table.css").toExternalForm());
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    table.setMaxWidth(Double.MAX_VALUE);
    VBox.setVgrow(table, Priority.ALWAYS);

    String headerStyle = "-fx-font-size: 11px; -fx-text-fill: #1a1a1a; -fx-font-weight: normal;";

    TableColumn<WatchRow, String> symbolCol = new TableColumn<>("SYMBOL");
    TableColumn<WatchRow, String> priceCol = new TableColumn<>("PRICE");
    TableColumn<WatchRow, String> changeCol = new TableColumn<>("CHANGE");

    symbolCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().symbol()));
    priceCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().price()));
    changeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().change()));

    table.setRowFactory(
        tv -> {
          TableRow<WatchRow> row = new TableRow<>();
          row.setOnMouseClicked(
              e -> {
                if (!row.isEmpty()) {
                  Stock stock = row.getItem().stock();
                  screen.showView(() -> new StockMenu(stock, screen));
                }
              });
          return row;
        });

    symbolCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
    priceCol.setMaxWidth(1f * Integer.MAX_VALUE * 35);
    changeCol.setMaxWidth(1f * Integer.MAX_VALUE * 35);
    for (TableColumn<WatchRow, String> col : List.of(symbolCol, priceCol, changeCol)) {
      col.setStyle(headerStyle);
    }
    table.getColumns().addAll(symbolCol, priceCol, changeCol);

    ObservableList<WatchRow> data = FXCollections.observableArrayList();
    screen.getController().getPlayer().getPortfolio().getShares().stream()
        .map(Share::getStock)
        .distinct()
        .forEach(
            s -> data.add(new WatchRow(s.getSymbol(), fmt(s.getSalesPrice()), fmtChange(s), s)));
    table.setItems(data);

    Region spacer = new Region();
    spacer.setPrefHeight(4);

    VBox pane = new VBox(8, heading, spacer, table);
    VBox.setVgrow(table, Priority.ALWAYS);
    VBox.setVgrow(pane, Priority.ALWAYS);
    pane.setStyle(
        """
        -fx-padding: 12;
        -fx-background-color: white;
        -fx-background-radius: 8;
        -fx-border-color: #c8c6c1;
        -fx-border-radius: 8;
        """);
    return pane;
  }

  private VBox createStatBox(String label, String value, String sub) {
    Label l1 = new Label(label);
    l1.setStyle("-fx-font-size: 12px; -fx-font-weight: lighter; -fx-text-fill: gray");
    Label l2 = new Label(value);
    l2.setStyle("-fx-font-size: 20px;");
    Label l3 = new Label(sub);
    l3.setStyle("-fx-font-size: 10px; -fx-font-weight: lighter; -fx-text-fill: gray;");
    VBox box = new VBox(10, l1, l2, l3);
    box.setStyle(
        """
        -fx-padding: 10px;
        -fx-background-radius: 8;
        -fx-border-color: #d0d0d0;
        -fx-border-radius: 8;
        """);
    return box;
  }

  private static String fmt(BigDecimal v) {
    return String.format("%.2f", v);
  }

  private static String fmtChange(Stock s) {
    BigDecimal change = s.getLatestPriceChange();
    BigDecimal prev = s.getSalesPrice().subtract(change);
    if (prev.signum() == 0) return "0.00%";
    BigDecimal pct = change.divide(prev, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    String sign = pct.signum() >= 0 ? "+" : "";
    return sign + String.format("%.2f", pct) + "%";
  }
}
