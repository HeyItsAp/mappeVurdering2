package ntnu.gruppe21.view.menus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import ntnu.gruppe21.model.Share;
import ntnu.gruppe21.model.Stock;
import ntnu.gruppe21.model.transaction.TransactionException;
import ntnu.gruppe21.view.Screen;
import ntnu.gruppe21.view.popups.BuyPopup;
import ntnu.gruppe21.view.popups.SellPopup;

public class StockMenu extends VBox {
  private final Stock stock;
  private final Screen screen;

  private Label sharesOwnedLabel;
  private Label avgCostLabel;
  private Label currentValueLabel;

  public StockMenu(Stock stock, Screen screen) {
    super(20);
    this.stock = stock;
    this.screen = screen;
    setStyle("-fx-padding: 20 20 30 20");

    Label symbol = new Label(stock.getSymbol());
    symbol.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

    Label company = new Label(stock.getCompany());
    company.setStyle("-fx-font-size: 14px; -fx-text-fill: #888;");

    VBox header = new VBox(2, symbol, company);

    Line line = new Line();
    line.setStartX(0);
    line.setStartY(0);
    line.setEndX(1000);
    line.setEndY(0);
    line.setStroke(Color.BLACK);
    line.setStrokeWidth(0.5);

    HBox bottom = new HBox(10);
    VBox.setVgrow(bottom, Priority.ALWAYS);

    VBox leftPane = buildChartPane();
    HBox.setHgrow(leftPane, Priority.ALWAYS);

    VBox rightPane = buildActionPane();
    rightPane.prefWidthProperty().bind(bottom.widthProperty().multiply(0.3));

    bottom.getChildren().addAll(leftPane, rightPane);
    getChildren().addAll(header, line, buildStatsRow(), bottom);
  }

  private HBox buildStatsRow() {
    HBox row = new HBox(10);

    BigDecimal price = stock.getSalesPrice();
    BigDecimal change = stock.getLatestPriceChange();
    String changeStr = fmtChange(change);
    String pctStr = fmtPct(stock);

    for (VBox box :
        List.of(
            createStatBox("CURRENT PRICE", fmt(price), pctStr),
            createStatBox("CHANGE", changeStr, pctStr),
            createStatBox("RECENT HIGH", fmt(stock.getHighestPrice()), ""),
            createStatBox("RECENT LOW", fmt(stock.getLowestPrice()), ""))) {
      HBox.setHgrow(box, Priority.ALWAYS);
      box.setMaxWidth(Double.MAX_VALUE);
      row.getChildren().add(box);
    }

    return row;
  }

  private VBox buildChartPane() {
    String inactive =
        """
        -fx-background-color: #f0efeb;
        -fx-text-fill: #333;
        -fx-font-size: 10px;
        -fx-padding: 3 10 3 10;
        -fx-background-radius: 20;
        -fx-border-radius: 20;
        -fx-border-color: #ddd;
        -fx-cursor: hand;
        """;
    String active =
        """
        -fx-background-color: #1a1a1a;
        -fx-text-fill: white;
        -fx-font-size: 10px;
        -fx-padding: 3 10 3 10;
        -fx-background-radius: 20;
        -fx-border-radius: 20;
        -fx-border-color: #1a1a1a;
        -fx-cursor: hand;
        """;

    NumberAxis xAxis = new NumberAxis();
    xAxis.setLabel("Week");
    xAxis.setMinorTickCount(0);

    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Price");
    yAxis.setMinorTickCount(0);

    LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
    chart.setLegendVisible(false);
    chart.setAnimated(false);
    chart.setCreateSymbols(false);
    chart.getStylesheets().add(getClass().getResource("/styles/chart.css").toExternalForm());
    VBox.setVgrow(chart, Priority.ALWAYS);

    Button tmBtn = new Button("3M");
    Button allBtn = new Button("ALL");

    loadSeries(chart, Integer.MAX_VALUE);
    allBtn.setStyle(active);
    tmBtn.setStyle(inactive);

    allBtn.setOnAction(
        ignored -> {
          loadSeries(chart, Integer.MAX_VALUE);
          allBtn.setStyle(active);
          tmBtn.setStyle(inactive);
        });
    tmBtn.setOnAction(
        ignored -> {
          loadSeries(chart, 12);
          tmBtn.setStyle(active);
          allBtn.setStyle(inactive);
        });

    HBox filters = new HBox(8, tmBtn, allBtn);

    VBox pane = new VBox(10, filters, chart);
    VBox.setVgrow(pane, Priority.ALWAYS);
    return pane;
  }

  private void loadSeries(LineChart<Number, Number> chart, int limit) {
    chart.getData().clear();
    XYChart.Series<Number, Number> series = new XYChart.Series<>();
    List<BigDecimal> history = stock.getPriceHistory();
    int start = Math.max(0, history.size() - limit);
    for (int i = start; i < history.size(); i++) {
      series.getData().add(new XYChart.Data<>(i + 1, history.get(i).doubleValue()));
    }
    chart.getData().add(series);
  }

  private VBox buildActionPane() {
    String btnBase =
        """
        -fx-font-size: 13px;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-padding: 10 0 10 0;
        -fx-cursor: hand;
        -fx-max-width: Infinity;
        """;

    Button buyBtn = new Button("Buy");
    buyBtn.setMaxWidth(Double.MAX_VALUE);
    buyBtn.setStyle(btnBase + "-fx-background-color: #1a1a1a; -fx-text-fill: white;");
    buyBtn.setOnAction(
        ignored -> {
          BuyPopup popup = new BuyPopup(stock);
          popup.setOnConfirm(
              () -> {
                try {
                  screen.getController().buyStock(stock.getSymbol(), popup.getQuantity());
                  popup.close();
                  screen.refreshSidebar();
                  refreshPositionCard();
                } catch (IllegalArgumentException | TransactionException e) {
                  e.printStackTrace();
                }
              });
          popup.show((Pane) getScene().getRoot());
        });

    Button sellBtn = new Button("Sell");
    sellBtn.setMaxWidth(Double.MAX_VALUE);
    sellBtn.setDisable(ownedShares() == 0);
    sellBtn.setStyle(
        btnBase
            + "-fx-background-color: white; -fx-text-fill: #1a1a1a;"
            + "-fx-border-color: #1a1a1a; -fx-border-radius: 8;");
    sellBtn.setOnAction(
        ignored -> {
          int owned = ownedShares();
          SellPopup popup = new SellPopup(stock, owned);
          popup.setOnConfirm(
              () -> {
                try {
                  screen.getController().sellStock(stock.getSymbol(), popup.getQuantity());
                  popup.close();
                  screen.refreshSidebar();
                  refreshPositionCard();
                  sellBtn.setDisable(ownedShares() == 0);
                } catch (IllegalArgumentException | TransactionException e) {
                  e.printStackTrace();
                }
              });
          popup.show((Pane) getScene().getRoot());
        });

    Button watchlistBtn = new Button("Add to Watchlist");
    watchlistBtn.setMaxWidth(Double.MAX_VALUE);
    watchlistBtn.setStyle(
        btnBase
            + "-fx-background-color: white; -fx-text-fill: #555;"
            + "-fx-border-color: #ccc; -fx-border-radius: 8;");

    Region spacer = new Region();
    VBox.setVgrow(spacer, Priority.ALWAYS);

    VBox pane = new VBox(12, buildPositionCard(), spacer, buyBtn, sellBtn, watchlistBtn);
    pane.setStyle(
        """
        -fx-padding: 10px;
        -fx-background-radius: 8;
        -fx-border-color: #d0d0d0;
        -fx-border-radius: 8;
        """);
    return pane;
  }

  private VBox buildPositionCard() {
    Label heading = new Label("YOUR POSITION");
    heading.setStyle("-fx-font-size: 10px; -fx-text-fill: #aaa; -fx-font-weight: bold;");

    int totalShares = ownedShares();
    BigDecimal avgCost = avgCost();
    BigDecimal currentValue = BigDecimal.valueOf(totalShares).multiply(stock.getSalesPrice());

    sharesOwnedLabel = new Label(String.valueOf(totalShares));
    avgCostLabel = new Label(fmt(avgCost));
    currentValueLabel = new Label(fmt(currentValue));

    VBox card =
        new VBox(
            10,
            heading,
            buildPositionRow("Shares owned", sharesOwnedLabel),
            buildPositionRow("Avg. cost", avgCostLabel),
            buildPositionRow("Current value", currentValueLabel));
    card.setStyle(
        """
        -fx-background-color: white;
        -fx-background-radius: 8;
        -fx-border-color: #c8c6c1;
        -fx-border-radius: 8;
        -fx-padding: 12;
        """);
    return card;
  }

  private void refreshPositionCard() {
    int totalShares = ownedShares();
    sharesOwnedLabel.setText(String.valueOf(totalShares));
    avgCostLabel.setText(fmt(avgCost()));
    currentValueLabel.setText(fmt(BigDecimal.valueOf(totalShares).multiply(stock.getSalesPrice())));
  }

  private int ownedShares() {
    return screen.getController().getPlayer().getPortfolio().getShares().stream()
        .filter(s -> s.getStock().getSymbol().equals(stock.getSymbol()))
        .mapToInt(Share::getQuantity)
        .sum();
  }

  private BigDecimal avgCost() {
    List<Share> matching =
        screen.getController().getPlayer().getPortfolio().getShares().stream()
            .filter(s -> s.getStock().getSymbol().equals(stock.getSymbol()))
            .toList();
    if (matching.isEmpty()) return BigDecimal.ZERO;
    BigDecimal totalCost =
        matching.stream()
            .map(s -> s.getPurchasePrice().multiply(BigDecimal.valueOf(s.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    int totalQty = matching.stream().mapToInt(Share::getQuantity).sum();
    return totalQty == 0
        ? BigDecimal.ZERO
        : totalCost.divide(BigDecimal.valueOf(totalQty), 2, RoundingMode.HALF_UP);
  }

  private HBox buildPositionRow(String label, Label valueLabel) {
    Label l = new Label(label);
    l.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
    valueLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    return new HBox(l, spacer, valueLabel);
  }

  private VBox createStatBox(String label, String value, String sub) {
    Label l1 = new Label(label);
    l1.setStyle("-fx-font-size: 12px; -fx-font-weight: lighter; -fx-text-fill: gray");

    Label l2 = new Label(value);
    l2.setStyle("-fx-font-size: 20px;");

    boolean positive = sub.startsWith("+");
    String color = sub.isEmpty() ? "#888" : (positive ? "#27ae60" : "#c0392b");
    Label l3 = new Label(sub.isEmpty() ? " " : sub);
    l3.setStyle("-fx-font-size: 10px; -fx-font-weight: lighter; -fx-text-fill: " + color + ";");

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

  private static String fmtChange(BigDecimal change) {
    String sign = change.signum() >= 0 ? "+" : "";
    return sign + String.format("%.2f", change);
  }

  private static String fmtPct(Stock s) {
    BigDecimal change = s.getLatestPriceChange();
    BigDecimal prev = s.getSalesPrice().subtract(change);
    if (prev.signum() == 0) return "0.00%";
    BigDecimal pct = change.divide(prev, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    String sign = pct.signum() >= 0 ? "+" : "";
    return sign + String.format("%.2f", pct) + "%";
  }
}
