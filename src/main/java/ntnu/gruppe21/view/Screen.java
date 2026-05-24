package ntnu.gruppe21.view;

import java.util.function.Supplier;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class Screen extends HBox {
  private Pane currentView;
  private Button currentButton;

  public Screen() {
    currentView = new TodayMenu();
    HBox.setHgrow(currentView, Priority.ALWAYS);

    getChildren().addAll(createSidebar(), currentView);
  }

  private void showView(Supplier<Pane> supplier) {
    currentView = supplier.get();
    HBox.setHgrow(currentView, Priority.ALWAYS);
    getChildren().set(1, currentView);
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

    Button todayBtn = navBtn("Today", TodayMenu::new);
    currentButton = todayBtn;
    setSelectedStyle(currentButton);

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
            sectionLabel("PLAY"),
            todayBtn,
            navBtn("Market", ExchangeMenu::new),
            navBtn("My portfolio", PortfolioMenu::new),
            navBtn("History", ExchangeMenu::new),
            sectionLabel("LEVEL"),
            navBtn("Progress", ExchangeMenu::new),
            spacer,
            week,
            advanceWeek);

    return sidebar;
  }

  private Label sectionLabel(String text) {
    Label l = new Label(text);
    l.setStyle("-fx-font-size: 10px; -fx-text-fill: #aaa; -fx-padding: 16 0 4 0;");
    return l;
  }

  private Button navBtn(String text, Supplier<Pane> supplier) {
    Button btn = new Button("  •  " + text);
    btn.setMaxWidth(Double.MAX_VALUE);
    btn.setStyle(
        """
        -fx-font-size: 14px;
        -fx-text-fill: #333;
        -fx-padding: 4 0 4 0;
        -fx-background-color: transparent;
        -fx-border-color: transparent;
        -fx-alignment: CENTER-LEFT;
        -fx-cursor: hand;
        """);
    btn.setOnMouseEntered(
        ignored -> {
          if (btn != currentButton) setHoverStyle(btn);
        });
    btn.setOnMouseExited(
        ignored -> {
          if (btn != currentButton) setUnselectedStyle(btn);
        });
    btn.setOnAction(
        ignored -> {
          showView(supplier);
          setUnselectedStyle(currentButton);
          currentButton = btn;
          setSelectedStyle(currentButton);
        });
    return btn;
  }

  private void setHoverStyle(Button btn) {
    btn.setStyle(
        """
        -fx-font-size: 14px;
        -fx-text-fill: #333;
        -fx-padding: 4 0 4 0;
        -fx-background-color: #d0ceca;
        -fx-background-radius: 8;
        -fx-border-color: transparent;
        -fx-alignment: CENTER-LEFT;
        -fx-cursor: hand;
        """);
  }

  private void setSelectedStyle(Button btn) {
    btn.setStyle(
        """
        -fx-font-size: 14px;
        -fx-text-fill: white;
        -fx-background-color: #1a1a1a;
        -fx-background-radius: 8;
        -fx-padding: 6 10 6 10;
        -fx-font-weight: bold;
        -fx-border-color: transparent;
        -fx-alignment: CENTER-LEFT;
        -fx-cursor: hand;
        """);
  }

  private void setUnselectedStyle(Button btn) {
    btn.setStyle(
        """
        -fx-font-size: 14px;
        -fx-text-fill: #333;
        -fx-padding: 4 0 4 0;
        -fx-background-color: transparent;
        -fx-border-color: transparent;
        -fx-alignment: CENTER-LEFT;
        -fx-cursor: hand;
        """);
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
