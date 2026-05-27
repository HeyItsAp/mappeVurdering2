package ntnu.gruppe21.view.menus;

import java.math.BigDecimal;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import ntnu.gruppe21.controller.GameController;
import ntnu.gruppe21.model.Player;
import ntnu.gruppe21.model.Stock;
import ntnu.gruppe21.view.Screen;
import ntnu.gruppe21.view.StockFormatter;

public class PortfolioMenu extends VBox {
  private final Screen screen;

  public PortfolioMenu(Screen screen) {
    super(20);
    this.screen = screen;
    setStyle("-fx-padding: 20 20 30 20");

    Label title = new Label("Portfolio");
    title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

    Line line = new Line();
    line.setStartX(0);
    line.setStartY(0);
    line.setEndX(1000);
    line.setEndY(0);
    line.setStroke(Color.BLACK);
    line.setStrokeWidth(0.5);

    HBox bottom = new HBox(10);
    VBox.setVgrow(bottom, Priority.ALWAYS);

    VBox leftPane = buildHoldingsTable();
    leftPane.prefWidthProperty().bind(bottom.widthProperty().multiply(0.5).subtract(5));
    leftPane.setMaxWidth(Double.MAX_VALUE);

    VBox rightPane = buildTransactionHistory();
    HBox.setHgrow(rightPane, Priority.ALWAYS);

    bottom.getChildren().addAll(leftPane, rightPane);
    getChildren().addAll(title, line, buildMoneyDisplay(), bottom);
  }

  public HBox buildMoneyDisplay() {
    Player player = screen.getController().getPlayer();
    int weeks = player.getTransactionArchive().countDistinctWeeks();
    String statusStr = player.getStatusName();

    HBox moneyDisplay = new HBox(8);
    for (VBox box :
        List.of(
            buildBox(
                "NET WORTH", StockFormatter.fmt(player.getNetWorth()), fmtChangeVsStart(player)),
            buildBox(
                "CASH",
                StockFormatter.fmt(player.getCurrentMoney()),
                "Started with " + StockFormatter.fmt(player.getStartingMoney())),
            buildBox(
                "STATUS / ASSETS",
                statusStr + " / " + StockFormatter.fmt(player.getPortfolio().getNetWorth()),
                weeks + " weeks played"))) {
      HBox.setHgrow(box, Priority.ALWAYS);
      box.setMaxWidth(Double.MAX_VALUE);
      moneyDisplay.getChildren().add(box);
    }
    return moneyDisplay;
  }

  private static String fmtChangeVsStart(Player player) {
    BigDecimal change = player.getNetWorth().subtract(player.getStartingMoney());
    String sign = change.signum() >= 0 ? "+" : "";
    return sign + StockFormatter.fmt(change);
  }

  record HoldingRow(
      String symbol, String company, String shares, String value, String change, Stock stock) {}

  private VBox buildHoldingsTable() {
    TableView<HoldingRow> table = new TableView<>();
    table.getStylesheets().add(getClass().getResource("/styles/table.css").toExternalForm());
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    table.setMaxWidth(Double.MAX_VALUE);
    VBox.setVgrow(table, Priority.ALWAYS);

    String headerStyle = "-fx-font-size: 11px; -fx-text-fill: #1a1a1a; -fx-font-weight: normal;";

    TableColumn<HoldingRow, String> symbolCol = new TableColumn<>("SYMBOL");
    TableColumn<HoldingRow, String> companyCol = new TableColumn<>("COMPANY");
    TableColumn<HoldingRow, String> sharesCol = new TableColumn<>("SHARES");
    TableColumn<HoldingRow, String> valueCol = new TableColumn<>("VALUE");
    TableColumn<HoldingRow, String> changeCol = new TableColumn<>("CHANGE");

    symbolCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().symbol()));
    companyCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().company()));
    sharesCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().shares()));
    valueCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().value()));
    changeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().change()));

    table.setRowFactory(
        tv -> {
          TableRow<HoldingRow> row = new TableRow<>();
          row.setOnMouseClicked(
              e -> {
                if (!row.isEmpty()) {
                  Stock stock = row.getItem().stock();
                  screen.showView(() -> new StockMenu(stock, screen));
                }
              });
          return row;
        });

    symbolCol.setMaxWidth(1f * Integer.MAX_VALUE * 12);
    companyCol.setMaxWidth(1f * Integer.MAX_VALUE * 14);
    sharesCol.setMaxWidth(1f * Integer.MAX_VALUE * 3);
    valueCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
    changeCol.setMaxWidth(1f * Integer.MAX_VALUE * 12);

    for (TableColumn<HoldingRow, String> col :
        List.of(symbolCol, companyCol, sharesCol, valueCol, changeCol)) {
      col.setStyle(headerStyle);
    }
    table.getColumns().addAll(symbolCol, companyCol, sharesCol, valueCol, changeCol);

    ObservableList<HoldingRow> data = FXCollections.observableArrayList();
    for (GameController.HoldingSummary h : screen.getController().getHoldings()) {
      data.add(
          new HoldingRow(
              h.symbol(),
              h.company(),
              String.valueOf(h.quantity()),
              StockFormatter.fmt(h.currentValue()),
              StockFormatter.fmtChange(h.stock()),
              h.stock()));
    }
    table.setItems(data);

    VBox pane = new VBox(table);
    VBox.setVgrow(table, Priority.ALWAYS);
    return pane;
  }

  private VBox buildBox(String text1, String text2, String text3) {
    Label label1 = new Label(text1);
    label1.setStyle("-fx-font-size: 12px; -fx-font-weight: lighter; -fx-text-fill: gray");
    Label label2 = new Label(text2);
    label2.setStyle("-fx-font-size: 20px;");
    Label label3 = new Label(text3);
    label3.setStyle("-fx-font-size: 10px; -fx-font-weight: lighter; -fx-text-fill: gray;");
    VBox box = new VBox(10, label1, label2, label3);
    box.setStyle(
        """
        -fx-padding: 10px;
        -fx-background-radius: 8;
        -fx-border-color: #d0d0d0;
        -fx-border-radius: 8;
        """);
    return box;
  }

  record TxRow(String week, String type, String symbol, String qty, String total) {}

  private VBox buildTransactionHistory() {
    Label heading = new Label("TRANSACTION HISTORY");
    heading.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa; -fx-font-weight: bold;");

    TableView<TxRow> table = new TableView<>();
    table.getStylesheets().add(getClass().getResource("/styles/table.css").toExternalForm());
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    table.setMaxWidth(Double.MAX_VALUE);
    VBox.setVgrow(table, Priority.ALWAYS);

    String headerStyle = "-fx-font-size: 11px; -fx-text-fill: #1a1a1a; -fx-font-weight: normal;";

    TableColumn<TxRow, String> weekCol = new TableColumn<>("WEEK");
    TableColumn<TxRow, String> typeCol = new TableColumn<>("TYPE");
    TableColumn<TxRow, String> symbolCol = new TableColumn<>("SYMBOL");
    TableColumn<TxRow, String> qtyCol = new TableColumn<>("QTY");
    TableColumn<TxRow, String> totalCol = new TableColumn<>("TOTAL");

    weekCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().week()));
    typeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().type()));
    symbolCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().symbol()));
    qtyCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().qty()));
    totalCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().total()));

    weekCol.setMaxWidth(1f * Integer.MAX_VALUE * 12);
    typeCol.setMaxWidth(1f * Integer.MAX_VALUE * 12);
    symbolCol.setMaxWidth(1f * Integer.MAX_VALUE * 18);
    qtyCol.setMaxWidth(1f * Integer.MAX_VALUE * 12);
    totalCol.setMaxWidth(1f * Integer.MAX_VALUE * 18);

    for (TableColumn<TxRow, String> col : List.of(weekCol, typeCol, symbolCol, qtyCol, totalCol)) {
      col.setStyle(headerStyle);
    }
    table.getColumns().addAll(weekCol, typeCol, symbolCol, qtyCol, totalCol);

    ObservableList<TxRow> data = FXCollections.observableArrayList();
    for (GameController.TransactionSummary tx : screen.getController().getTransactionHistory()) {
      data.add(
          new TxRow(
              "Wk " + tx.week(),
              tx.type(),
              tx.symbol(),
              String.valueOf(tx.quantity()),
              StockFormatter.fmt(tx.total())));
    }
    table.setItems(data);

    VBox pane = new VBox(8, heading, table);
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
}
