package ntnu.gruppe21.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class ExchangeMenu extends VBox {
  public ExchangeMenu() {
    super(20);
    Label header = new Label("Oslo Stock Market");
    header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

    Line line = new Line();
    line.setStartX(0);
    line.setStartY(0);
    line.setEndX(1000);
    line.setEndY(0);

    line.setStroke(Color.BLACK);
    line.setStrokeWidth(0.5);

    setStyle("-fx-padding: 20 20 30 20");
    getChildren().addAll(header, line, buildFilterButtons(), buildMarketTable());
  }

  private HBox buildFilterButtons() {
    HBox bar = new HBox(8);

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

    for (String label : java.util.List.of("All", "Owned", "Watchlist")) {
      Button btn = new Button(label);
      btn.setStyle(normal);
      btn.setOnMouseEntered(ignored -> btn.setStyle(hover));
      btn.setOnMouseExited(ignored -> btn.setStyle(normal));
      bar.getChildren().add(btn);
    }

    return bar;
  }

  record MarketRow(String symbol, String company, String price, String week) {}

  private TableView<MarketRow> buildMarketTable() {

    TableView<MarketRow> table = new TableView<>();
    table.setStyle(
        "-fx-background-color: white; -fx-border-color: #ddd; -fx-table-cell-border-color: transparent;");
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    table.setMaxWidth(Double.MAX_VALUE);
    VBox.setVgrow(table, Priority.ALWAYS);

    TableColumn<MarketRow, String> symbolCol = new TableColumn<>("SYMBOL");
    TableColumn<MarketRow, String> companyCol = new TableColumn<>("COMPANY");
    TableColumn<MarketRow, String> priceCol = new TableColumn<>("PRICE");
    TableColumn<MarketRow, String> changeCol = new TableColumn<>("CHANGE");

    symbolCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().symbol()));
    companyCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().company()));
    priceCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().price()));
    changeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().week()));

    symbolCol.setMaxWidth(1f * Integer.MAX_VALUE * 15);
    companyCol.setMaxWidth(1f * Integer.MAX_VALUE * 55);
    priceCol.setMaxWidth(1f * Integer.MAX_VALUE * 15);
    changeCol.setMaxWidth(1f * Integer.MAX_VALUE * 15);

    String headerStyle = "-fx-font-size: 11px; -fx-text-fill: #aaa; -fx-font-weight: normal;";
    symbolCol.setStyle(headerStyle);
    companyCol.setStyle(headerStyle);
    priceCol.setStyle(headerStyle);
    changeCol.setStyle(headerStyle);

    table.getColumns().addAll(symbolCol, companyCol, priceCol, changeCol);

    ObservableList<MarketRow> data = FXCollections.observableArrayList();

    for (int i = 0; i < 20; i++) {
      data.add(new MarketRow("SYMB", "Company", "420", "+3.14%"));
    }

    table.setItems(data);
    return table;
  }
}
