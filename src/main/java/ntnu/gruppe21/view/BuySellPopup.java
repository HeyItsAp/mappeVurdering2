package ntnu.gruppe21.view;

import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public abstract class BuySellPopup extends Popup {
  private Label title;
  private Label value;
  private Button confirmBtn;

  private Label priceValue;
  private Label quantityValue;
  private Label commissionValue;
  private Label taxValue;
  private Label totalValue;

  @Override
  protected List<Node> buildContent() {
    title = new Label("");
    title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

    HBox amount = buildAmountSelector();

    value = new Label("");
    value.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

    VBox costBox = buildCostBox();

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

    confirmBtn = new Button("Confirm");
    confirmBtn.setStyle(btnBase + "-fx-background-color: #1a1a1a; -fx-text-fill: white;");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox btnBox = new HBox(8, spacer, cancelBtn, confirmBtn);

    return List.of(title, amount, value, costBox, btnBox);
  }

  private HBox buildAmountSelector() {
    String btnStyle =
        """
                -fx-font-size: 16px;
                -fx-font-weight: bold;
                -fx-background-color: #f0efeb;
                -fx-background-radius: 8;
                -fx-border-color: #d0d0d0;
                -fx-border-radius: 8;
                -fx-border-width: 1.5;
                -fx-cursor: hand;
                -fx-min-width: 36;
                -fx-min-height: 36;
                """;

    TextField field = new TextField("1");
    field.setStyle(
        """
                -fx-font-size: 24px;
                -fx-font-weight: bold;
                -fx-alignment: center;
                -fx-background-color: transparent;
                -fx-border-color: transparent;
                -fx-padding: 4 8 4 8;
                """);
    HBox.setHgrow(field, Priority.ALWAYS);

    Button decrease = new Button("−");
    decrease.setStyle(btnStyle);
    decrease.setOnAction(
        ignored -> {
          try {
            int current = Integer.parseInt(field.getText().trim());
            field.setText(String.valueOf(Math.max(1, current - 1)));
          } catch (NumberFormatException ex) {
            field.setText("1");
          }
        });

    Button increase = new Button("+");
    increase.setStyle(btnStyle);
    increase.setOnAction(
        ignored -> {
          try {
            int current = Integer.parseInt(field.getText().trim());
            field.setText(String.valueOf(current + 1));
          } catch (NumberFormatException ex) {
            field.setText("1");
          }
        });

    HBox selector = new HBox(12, decrease, field, increase);
    selector.setAlignment(Pos.CENTER_LEFT);
    return selector;
  }

  private VBox buildCostBox() {
    priceValue = new Label("420.00");
    quantityValue = new Label("× 1");
    commissionValue = new Label("4.20");
    taxValue = new Label("10.50");
    totalValue = new Label("434.70");

    Region separator = new Region();
    separator.setPrefHeight(1);
    separator.setMaxWidth(Double.MAX_VALUE);
    separator.setStyle(
        "-fx-border-color: #ccc; -fx-border-width: 1 0 0 0; -fx-border-style: dashed;");

    VBox box =
        new VBox(
            6,
            buildCostRow("Price per share", priceValue, false),
            buildCostRow("Quantity", quantityValue, false),
            buildCostRow("Commission", commissionValue, false),
            buildCostRow("Tax", taxValue, false),
            separator,
            buildCostRow("Total", totalValue, true));
    box.setStyle(
        """
        -fx-background-color: #f9f9f9;
        -fx-background-radius: 8;
        -fx-border-color: #e0e0e0;
        -fx-border-radius: 8;
        -fx-padding: 10;
        """);
    return box;
  }

  private HBox buildCostRow(String descriptor, Label valueLabel, boolean bold) {
    String labelStyle =
        bold
            ? "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;"
            : "-fx-font-size: 12px; -fx-text-fill: #888;";
    String valueStyle =
        bold
            ? "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;"
            : "-fx-font-size: 12px; -fx-text-fill: #333;";

    Label desc = new Label(descriptor);
    desc.setStyle(labelStyle);
    valueLabel.setStyle(valueStyle);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    return new HBox(desc, spacer, valueLabel);
  }

  protected void setPriceValue(String text) {
    priceValue.setText(text);
  }

  protected void setQuantityValue(String text) {
    quantityValue.setText(text);
  }

  protected void setCommissionValue(String text) {
    commissionValue.setText(text);
  }

  protected void setTaxValue(String text) {
    taxValue.setText(text);
  }

  protected void setTotalValue(String text) {
    totalValue.setText(text);
  }

  protected void setTitle(String text) {
    title.setText(text);
  }

  protected void setValue(String text) {
    value.setText(text);
  }

  protected void setConfirm(String text) {
    confirmBtn.setText(text);
  }

  protected void setOnConfirm(Runnable action) {
    confirmBtn.setOnAction(ignored -> action.run());
  }
}
