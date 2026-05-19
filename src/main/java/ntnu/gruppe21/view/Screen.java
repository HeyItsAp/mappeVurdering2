package ntnu.gruppe21.view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class Screen extends HBox {
  public Screen() {
    VBox mainContent = new PortfolioMenu();
    HBox.setHgrow(mainContent, Priority.ALWAYS);

    getChildren().addAll(createSidebar(), mainContent);
  }

  private VBox createSidebar() {
    VBox sidebar = new VBox();

    sidebar.setStyle("-fx-background-color: GRAY; -fx-padding: 20;");
    sidebar.prefWidthProperty().bind(this.widthProperty().multiply(0.2));

    sidebar.setStyle("-fx-background-color: #e8e6e1; -fx-padding: 24;");
    sidebar.setSpacing(4);

    Label title = new Label("Name");
    title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

    Label subtitle = new Label("Level: Investor");
    subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

    VBox money = new VBox(6);
    money.setStyle(
        """
        -fx-background-color: white;
        -fx-background-radius: 8;
        -fx-border-color: #c8c6c1;
        -fx-border-radius: 8;
        -fx-padding: 12;
        """);

    Label moneyLabel = new Label("PORTFOLIO SUMMARY");
    moneyLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #aaa; -fx-font-weight: bold;");

    Label balance = new Label("Net Worth:  78 476");
    balance.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

    Label netWorth = new Label("Balance:  643 634");
    netWorth.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

    money.getChildren().addAll(moneyLabel, balance, netWorth);

    java.util.function.Function<String, Label> sectionLabel =
        text -> {
          Label l = new Label(text);
          l.setStyle("-fx-font-size: 10px; -fx-text-fill: #aaa; -fx-padding: 16 0 4 0;");
          return l;
        };

    java.util.function.Function<String, Label> menuItem =
        text -> {
          Label l = new Label("  •  " + text);
          l.setStyle("-fx-font-size: 14px; -fx-text-fill: #333; -fx-padding: 4 0 4 0;");
          l.setMaxWidth(Double.MAX_VALUE);
          return l;
        };

    java.util.function.Function<String, Label> activeItem =
        text -> {
          Label l = new Label("  •  " + text);
          l.setStyle(
              """
        -fx-font-size: 14px;
        -fx-text-fill: white;
        -fx-background-color: #1a1a1a;
        -fx-background-radius: 8;
        -fx-padding: 6 10 6 10;
        -fx-font-weight: bold;
    """);
          l.setMaxWidth(Double.MAX_VALUE);
          return l;
        };

    Region spacer = new Region();
    VBox.setVgrow(spacer, Priority.ALWAYS);

    Label week = new Label("Week N");

    Button advanceWeek = createAdvanceWeekBtn();

    sidebar
        .getChildren()
        .addAll(
            title,
            subtitle,
            money,
            sectionLabel.apply("PLAY"),
            activeItem.apply("Today"),
            menuItem.apply("Market"),
            menuItem.apply("My portfolio"),
            menuItem.apply("History"),
            sectionLabel.apply("LEVEL"),
            menuItem.apply("Progress"),
            spacer,
            week,
            advanceWeek);

    return sidebar;
  }

  private Button createAdvanceWeekBtn() {
    Button advanceWeek = new Button("Advance to week N+1");
    advanceWeek.setMaxWidth(Double.MAX_VALUE);
    advanceWeek.setStyle(
        """
            -fx-background-color: #1a1a1a;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-padding: 12 0 12 0;
            -fx-background-radius: 10;
            -fx-cursor: hand;
        """);
    advanceWeek.setOnMouseEntered(
        ignored ->
            advanceWeek.setStyle(
                """
            -fx-background-color: #333;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-padding: 12 0 12 0;
            -fx-background-radius: 10;
            -fx-cursor: hand;
        """));
    advanceWeek.setOnMouseExited(
        ignored ->
            advanceWeek.setStyle(
                """
            -fx-background-color: #1a1a1a;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-padding: 12 0 12 0;
            -fx-background-radius: 10;
            -fx-cursor: hand;
        """));
    return advanceWeek;
  }
}
