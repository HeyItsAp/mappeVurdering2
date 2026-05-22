package ntnu.gruppe21.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class StockMenu extends VBox {
  public StockMenu() {
    super(20);
    setStyle("-fx-padding: 20 20 30 20");

    Label symbol = new Label("SYMB");
    symbol.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

    Label company = new Label("Company Name");
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

    for (VBox box :
        java.util.List.of(
            createStatBox("CURRENT PRICE", "420.00", "+3.14%"),
            createStatBox("CHANGE", "+12.34", "+3.14%"),
            createStatBox("RECENT HIGH", "512.50", ""),
            createStatBox("RECENT LOW", "310.00", ""))) {
      HBox.setHgrow(box, Priority.ALWAYS);
      box.setMaxWidth(Double.MAX_VALUE);
      row.getChildren().add(box);
    }

    return row;
  }

  private VBox buildChartPane() {
    String normal =
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
    String hover =
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

    HBox filters = new HBox(8);
    for (String label : java.util.List.of("3M", "ALL")) {
      Button btn = new Button(label);
      btn.setStyle(normal);
      btn.setOnMouseEntered(ignored -> btn.setStyle(hover));
      btn.setOnMouseExited(ignored -> btn.setStyle(normal));
      filters.getChildren().add(btn);
    }

    Label chartLabel = new Label("Price chart coming soon");
    chartLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 13px;");

    VBox chart = new VBox(chartLabel);
    chart.setStyle(
        "-fx-background-color: #e0e0e0; -fx-background-radius: 8; -fx-alignment: center;");
    VBox.setVgrow(chart, Priority.ALWAYS);

    VBox pane = new VBox(10, filters, chart);
    VBox.setVgrow(pane, Priority.ALWAYS);
    return pane;
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

    Button sellBtn = new Button("Sell");
    sellBtn.setMaxWidth(Double.MAX_VALUE);
    sellBtn.setStyle(
        btnBase
            + "-fx-background-color: white; -fx-text-fill: #1a1a1a;"
            + "-fx-border-color: #1a1a1a; -fx-border-radius: 8;");

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

    VBox card =
        new VBox(
            10,
            heading,
            buildPositionRow("Shares owned", "10"),
            buildPositionRow("Avg. cost", "395.20"),
            buildPositionRow("Current value", "4 200.00"));
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

  private HBox buildPositionRow(String label, String value) {
    Label l = new Label(label);
    l.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

    Label v = new Label(value);
    v.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    return new HBox(l, spacer, v);
  }

  private VBox createStatBox(String label, String value, String sub) {
    Label l1 = new Label(label);
    l1.setStyle("-fx-font-size: 12px; -fx-font-weight: lighter; -fx-text-fill: gray");

    Label l2 = new Label(value);
    l2.setStyle("-fx-font-size: 20px;");

    Label l3 = new Label(sub.isEmpty() ? " " : sub);
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
