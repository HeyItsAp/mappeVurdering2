package ntnu.gruppe21.view;

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

public class TodayMenu extends VBox {
  private final Screen screen;

  public TodayMenu(Screen screen) {
    super(20);
    this.screen = screen;
    setStyle("-fx-padding: 20 20 30 20");

    Label title = new Label("Week 7");
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

    VBox rightPane = buildWatchlist();
    rightPane.prefWidthProperty().bind(body.widthProperty().multiply(0.25));

    body.getChildren().addAll(leftPane, rightPane);
    getChildren().addAll(title, line, body);
  }

  private VBox buildLeftPane() {
    VBox GainLosss = buildGainLosssSection();
    VBox.setVgrow(GainLosss, Priority.ALWAYS);

    VBox pane = new VBox(16, buildNetWorthCard(), buildSummaryRow(), GainLosss);
    VBox.setVgrow(pane, Priority.ALWAYS);
    return pane;
  }

  private VBox buildNetWorthCard() {
    Label heading = new Label("NET WORTH CHANGE THIS WEEK");
    heading.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa; -fx-font-weight: bold;");

    Label change = new Label("+2 340 NOK");
    change.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

    Label pct = new Label("+4.72% from last week  ·  Now: 51 654 NOK");
    pct.setStyle("-fx-font-size: 13px; -fx-text-fill: #888;");

    VBox card = new VBox(6, heading, change, pct);
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
    HBox row = new HBox(10);

    for (VBox box :
        java.util.List.of(
            createStatBox("CASH", "23 420", "Available to invest"),
            createStatBox("INVESTED", "28 234", "Across 5 stocks"),
            createStatBox("BEST STOCK", "SYMB +8.1%", "This week"))) {
      HBox.setHgrow(box, Priority.ALWAYS);
      box.setMaxWidth(Double.MAX_VALUE);
      row.getChildren().add(box);
    }

    return row;
  }

  private VBox buildGainLosssSection() {
    HBox row = new HBox(10);
    VBox.setVgrow(row, Priority.ALWAYS);

    VBox gainers = buildGainLossList("TOP GAINERS", true);
    VBox losers = buildGainLossList("TOP LOSERS", false);
    HBox.setHgrow(gainers, Priority.ALWAYS);
    HBox.setHgrow(losers, Priority.ALWAYS);

    row.getChildren().addAll(gainers, losers);

    VBox section = new VBox(row);
    VBox.setVgrow(section, Priority.ALWAYS);
    return section;
  }

  private VBox buildGainLossList(String heading, boolean gainers) {
    Label title = new Label(heading);
    title.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa; -fx-font-weight: bold;");

    String sign = gainers ? "+" : "-";

    VBox cards = new VBox(6);
    for (String[] entry :
        new String[][] {
          {"SYMB", "420.00", sign + "8.14%"},
          {"SYMB", "380.50", sign + "5.32%"},
          {"SYMB", "210.75", sign + "3.90%"},
          {"SYMB", "155.20", sign + "2.11%"}
        }) {
      cards.getChildren().add(buildGainLossCard(entry[0], entry[1], entry[2]));
    }

    VBox pane = new VBox(8, title, cards);
    VBox.setVgrow(pane, Priority.ALWAYS);
    return pane;
  }

  private HBox buildGainLossCard(String symbol, String value, String change) {
    Label symbolLabel = new Label(symbol);
    symbolLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

    Label valueLabel = new Label(value);
    valueLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1a1a1a;");

    Label changeLabel = new Label(change);
    changeLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox card = new HBox(10, symbolLabel, valueLabel, spacer, changeLabel);
    card.setStyle(
        """
        -fx-padding: 10 12 10 12;
        -fx-background-color: white;
        -fx-background-radius: 8;
        -fx-border-color: #d0d0d0;
        -fx-border-radius: 8;
        """);
    card.setMaxWidth(Double.MAX_VALUE);
    return card;
  }

  record WatchRow(String symbol, String price, String change) {}

  private VBox buildWatchlist() {
    Label heading = new Label("WATCHLIST");
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
          TableRow<TodayMenu.WatchRow> row = new TableRow<>();
          row.setOnMouseClicked(
              e -> {
                if (!row.isEmpty()) {
                  screen.showView(() -> new StockMenu());
                }
              });
          return row;
        });

    symbolCol.setMaxWidth(1f * Integer.MAX_VALUE * 30);
    priceCol.setMaxWidth(1f * Integer.MAX_VALUE * 35);
    changeCol.setMaxWidth(1f * Integer.MAX_VALUE * 35);

    for (TableColumn<WatchRow, String> col : java.util.List.of(symbolCol, priceCol, changeCol)) {
      col.setStyle(headerStyle);
    }

    table.getColumns().addAll(symbolCol, priceCol, changeCol);

    ObservableList<WatchRow> data = FXCollections.observableArrayList();
    for (int i = 0; i < 8; i++) {
      data.add(new WatchRow("SYMB", "420.00", "+3.14%"));
    }
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
}
