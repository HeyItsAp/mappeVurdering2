package ntnu.gruppe21.view;

import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class SellAllQuitPopup extends Popup {
  private Runnable onConfirm;

  @Override
  protected List<Node> buildContent() {
    Label title = new Label("Sell all and quit?");
    title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

    Label description =
        new Label("All your shares will be sold at current market price and the game will end.");
    description.setWrapText(true);
    description.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

    VBox holdingsBox =
        new VBox(
            4,
            buildHoldingRow("SYMB", "10 shares", "4 200.00"),
            buildHoldingRow("SYMB", "5 shares", "1 750.00"),
            buildHoldingRow("SYMB", "20 shares", "840.00"));
    holdingsBox.setStyle(
        """
        -fx-background-color: #f9f9f9;
        -fx-background-radius: 8;
        -fx-border-color: #e0e0e0;
        -fx-border-radius: 8;
        -fx-padding: 10;
        """);

    ScrollPane scroll = new ScrollPane(holdingsBox);
    scroll.setFitToWidth(true);
    scroll.setMaxHeight(180);
    scroll.setStyle(
        "-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

    Region dashSep1 = buildDashSeparator();

    Label cashLabel = new Label("Current cash:  23 420.00");
    cashLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

    Label proceedsLabel = new Label("Proceeds from sale:  6 790.00");
    proceedsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

    Region dashSep2 = buildDashSeparator();

    Label totalLabel = new Label("Total after sale:  30 210.00");
    totalLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

    VBox summaryBox = new VBox(6, dashSep1, cashLabel, proceedsLabel, dashSep2, totalLabel);

    String btnBase =
        """
        -fx-font-size: 13px;
        -fx-font-weight: bold;
        -fx-background-radius: 8;
        -fx-border-radius: 8;
        -fx-padding: 8 20 8 20;
        -fx-cursor: hand;
        """;

    Button cancelBtn = new Button("Cancel");
    cancelBtn.setStyle(
        btnBase
            + "-fx-background-color: white; -fx-text-fill: #1a1a1a;"
            + "-fx-border-color: #1a1a1a; -fx-border-width: 1;");
    cancelBtn.setOnAction(ignored -> close());

    Button confirmBtn = new Button("Sell all and quit");
    confirmBtn.setStyle(btnBase + "-fx-background-color: #c0392b; -fx-text-fill: white;");
    confirmBtn.setOnAction(
        ignored -> {
          if (onConfirm != null) onConfirm.run();
          close();
        });

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    HBox btnBox = new HBox(8, spacer, cancelBtn, confirmBtn);

    return List.of(title, description, scroll, summaryBox, btnBox);
  }

  private HBox buildHoldingRow(String symbol, String qty, String value) {
    Label symbolLabel = new Label(symbol);
    symbolLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

    Label qtyLabel = new Label(qty);
    qtyLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

    Label valueLabel = new Label(value);
    valueLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333;");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    return new HBox(8, symbolLabel, qtyLabel, spacer, valueLabel);
  }

  private Region buildDashSeparator() {
    Region sep = new Region();
    sep.setPrefHeight(1);
    sep.setMaxWidth(Double.MAX_VALUE);
    sep.setStyle("-fx-border-color: #ccc; -fx-border-width: 1 0 0 0; -fx-border-style: dashed;");
    return sep;
  }

  public void setOnConfirm(Runnable action) {
    this.onConfirm = action;
  }
}
