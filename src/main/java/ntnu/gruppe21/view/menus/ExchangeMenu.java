package ntnu.gruppe21.view.menus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import ntnu.gruppe21.model.Stock;
import ntnu.gruppe21.view.Screen;

public class ExchangeMenu extends VBox {
  private final Screen screen;

  public ExchangeMenu(Screen screen) {
    super(20);
    this.screen = screen;

    Label title = new Label("Stocks on exchange");
    title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

    Line line = new Line();
    line.setStartX(0);
    line.setStartY(0);
    line.setEndX(1000);
    line.setEndY(0);
    line.setStroke(Color.BLACK);
    line.setStrokeWidth(0.5);

    TableView<MarketRow> table = buildMarketTable();
    setStyle("-fx-padding: 20 20 30 20");
    getChildren().addAll(title, line, buildTopBar(table), table);
  }

  private HBox buildTopBar(TableView<MarketRow> table) {
    HBox bar = new HBox(8);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    TextField search = new TextField();
    search.setPromptText("Search symbol or company...");
    search.setStyle(
        """
        -fx-font-size: 10px;
        -fx-padding: 3 10 3 10;
        -fx-background-radius: 20;
        -fx-border-radius: 20;
        -fx-border-color: #ddd;
        -fx-background-color: white;
        -fx-pref-width: 180;
        """);
    search
        .textProperty()
        .addListener(
            (obs, old, q) -> {
              List<Stock> stocks =
                  q.isBlank()
                      ? screen.getController().getExchange().getStockMap().values().stream()
                          .sorted(Comparator.comparing(Stock::getSymbol))
                          .toList()
                      : screen.getController().getExchange().findStock(q).stream()
                          .sorted(Comparator.comparing(Stock::getSymbol))
                          .toList();
              ObservableList<MarketRow> rows = FXCollections.observableArrayList();
              stocks.forEach(
                  s ->
                      rows.add(
                          new MarketRow(
                              s.getSymbol(),
                              s.getCompany(),
                              fmt(s.getSalesPrice()),
                              fmtChange(s))));
              table.setItems(rows);
            });

    bar.getChildren().addAll(spacer, search);
    return bar;
  }

  record MarketRow(String symbol, String company, String price, String change) {}

  private TableView<MarketRow> buildMarketTable() {
    TableView<MarketRow> table = new TableView<>();
    table.getStylesheets().add(getClass().getResource("/styles/table.css").toExternalForm());
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    table.setMaxWidth(Double.MAX_VALUE);
    VBox.setVgrow(table, Priority.ALWAYS);

    TableColumn<MarketRow, String> symbolCol = new TableColumn<>("SYMBOL");
    TableColumn<MarketRow, String> companyCol = new TableColumn<>("COMPANY");
    TableColumn<MarketRow, String> priceCol = new TableColumn<>("PRICE");
    TableColumn<MarketRow, String> changeCol = new TableColumn<>("CHANGE");

    symbolCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().symbol()));
    companyCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().company()));
    priceCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().price()));
    changeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().change()));

    table.setRowFactory(
        tv -> {
          TableRow<MarketRow> row = new TableRow<>();
          row.setOnMouseClicked(
              e -> {
                if (!row.isEmpty()) {
                  Stock stock =
                      screen.getController().getExchange().getStock(row.getItem().symbol());
                  screen.showView(() -> new StockMenu(stock, screen));
                }
              });
          return row;
        });

    symbolCol.setMaxWidth(1f * Integer.MAX_VALUE * 15);
    companyCol.setMaxWidth(1f * Integer.MAX_VALUE * 45);
    priceCol.setMaxWidth(1f * Integer.MAX_VALUE * 15);
    changeCol.setMaxWidth(1f * Integer.MAX_VALUE * 15);

    String headerStyle = "-fx-font-size: 11px; -fx-text-fill: #1a1a1a; -fx-font-weight: normal;";
    for (TableColumn<MarketRow, String> col : List.of(symbolCol, companyCol, priceCol, changeCol)) {
      col.setStyle(headerStyle);
    }
    table.getColumns().addAll(symbolCol, companyCol, priceCol, changeCol);

    ObservableList<MarketRow> data = FXCollections.observableArrayList();
    screen.getController().getExchange().getStockMap().values().stream()
        .sorted(Comparator.comparing(Stock::getSymbol))
        .forEach(
            s ->
                data.add(
                    new MarketRow(
                        s.getSymbol(), s.getCompany(), fmt(s.getSalesPrice()), fmtChange(s))));
    table.setItems(data);
    return table;
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
