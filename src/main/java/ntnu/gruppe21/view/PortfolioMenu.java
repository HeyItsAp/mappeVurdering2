package ntnu.gruppe21.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class PortfolioMenu extends VBox {
  public PortfolioMenu() {
    super(20);
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

    Region placeholder = new Region();
    placeholder.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 8;");
    HBox.setHgrow(placeholder, Priority.ALWAYS);

    bottom.getChildren().addAll(leftPane, placeholder);

    getChildren().addAll(title, line, createMoneyDisplay(), bottom);
  }

  public HBox createMoneyDisplay() {
    HBox moneyDisplay = new HBox(8);

    for (VBox box :
        java.util.List.of(
            createBox("NET WORTH", "51 654", "+0.43%"),
            createBox("CASH", "23 420", "Started with 20 000"),
            createBox(
                "STATUS/ASSETS",
                "NOVICE/28 234",
                "N weeks played"))) { // Havent decided what should be here
      HBox.setHgrow(box, Priority.ALWAYS);
      box.setMaxWidth(Double.MAX_VALUE);
      moneyDisplay.getChildren().add(box);
    }

    return moneyDisplay;
  }

  record HoldingRow(String symbol, String company, String shares, String value, String change) {}

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

    symbolCol.setMaxWidth(1f * Integer.MAX_VALUE * 12);
    companyCol.setMaxWidth(1f * Integer.MAX_VALUE * 14);
    sharesCol.setMaxWidth(1f * Integer.MAX_VALUE * 3);
    valueCol.setMaxWidth(1f * Integer.MAX_VALUE * 10);
    changeCol.setMaxWidth(1f * Integer.MAX_VALUE * 12);

    for (TableColumn<HoldingRow, String> col :
        java.util.List.of(symbolCol, companyCol, sharesCol, valueCol, changeCol)) {
      col.setStyle(headerStyle);
    }

    table.getColumns().addAll(symbolCol, companyCol, sharesCol, valueCol, changeCol);

    ObservableList<HoldingRow> data = FXCollections.observableArrayList();
    for (int i = 0; i < 8; i++) {
      data.add(new HoldingRow("SYMB", "Company", "10", "4 200", "+3.14%"));
    }
    table.setItems(data);

    VBox pane = new VBox(table);
    VBox.setVgrow(table, Priority.ALWAYS);
    return pane;
  }

  private VBox createBox(String text1, String text2, String text3) {
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
}
