package ntnu.gruppe21.view;

import java.math.BigDecimal;
import java.util.function.Supplier;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ntnu.gruppe21.controller.GameController;
import ntnu.gruppe21.model.Exchange;
import ntnu.gruppe21.model.Player;
import ntnu.gruppe21.view.menus.ExchangeMenu;
import ntnu.gruppe21.view.menus.PortfolioMenu;
import ntnu.gruppe21.view.menus.ProgressMenu;
import ntnu.gruppe21.view.menus.StartMenu;
import ntnu.gruppe21.view.menus.TodayMenu;
import ntnu.gruppe21.view.popups.AdvanceWeekPopup;
import ntnu.gruppe21.view.popups.SellAllQuitPopup;

public class Screen extends StackPane {
  private final GameController controller;
  private Pane currentView;
  private Button currentButton;
  private final HBox mainPane;

  private Label nameLabel;
  private Label subtitleLabel;
  private Label netWorthLabel;
  private Label cashLabel;
  private Label weekLabel;
  private Button advanceWeekBtn;
  private Button todayBtn;

  public Screen(GameController controller) {
    this.controller = controller;
    mainPane = new HBox();
    currentView = new TodayMenu(this);
    HBox.setHgrow(currentView, Priority.ALWAYS);
    mainPane.getChildren().addAll(createSidebar(), currentView);
    getChildren().add(mainPane);
  }

  public GameController getController() {
    return controller;
  }

  public void refreshSidebar() {
    Player player = controller.getPlayer();
    Exchange exchange = controller.getExchange();
    nameLabel.setText(player.getName());
    subtitleLabel.setText("Level: " + statusLabel(player.getStatus()));
    netWorthLabel.setText("Net Worth:  " + fmt(player.getNetWorth()));
    cashLabel.setText("Balance:  " + fmt(player.getCurrentMoney()));
    weekLabel.setText("Week " + exchange.getWeek());
    advanceWeekBtn.setText("Advance to week " + (exchange.getWeek() + 1));
  }

  public void navigateToToday() {
    showView(() -> new TodayMenu(this));
    setUnselectedStyle(currentButton);
    currentButton = todayBtn;
    setSelectedStyle(currentButton);
  }

  public void showView(Supplier<Pane> supplier) {
    currentView = supplier.get();
    HBox.setHgrow(currentView, Priority.ALWAYS);
    mainPane.getChildren().set(1, currentView);
  }

  private VBox createSidebar() {
    VBox sidebar = new VBox();
    sidebar.setStyle("-fx-background-color: #e8e6e1; -fx-padding: 24;");
    sidebar.setSpacing(4);
    sidebar.prefWidthProperty().bind(this.widthProperty().multiply(0.2));

    Player player = controller.getPlayer();
    Exchange exchange = controller.getExchange();

    nameLabel = new Label(player.getName());
    nameLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

    subtitleLabel = new Label("Level: " + statusLabel(player.getStatus()));
    subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

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

    netWorthLabel = new Label("Net Worth:  " + fmt(player.getNetWorth()));
    netWorthLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

    cashLabel = new Label("Balance:  " + fmt(player.getCurrentMoney()));
    cashLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

    money.getChildren().addAll(moneyLabel, netWorthLabel, cashLabel);

    todayBtn = navBtn("Today", () -> new TodayMenu(this));
    currentButton = todayBtn;
    setSelectedStyle(currentButton);

    Region spacer = new Region();
    VBox.setVgrow(spacer, Priority.ALWAYS);

    weekLabel = new Label("Week " + exchange.getWeek());

    advanceWeekBtn = createAdvanceWeekBtn();
    Button exit = createExitBtn();

    sidebar
        .getChildren()
        .addAll(
            nameLabel,
            subtitleLabel,
            money,
            sectionLabel("PLAY"),
            todayBtn,
            navBtn("Market", () -> new ExchangeMenu(this)),
            navBtn("My portfolio", () -> new PortfolioMenu(this)),
            sectionLabel("LEVEL"),
            navBtn("Progress", () -> new ProgressMenu(this)),
            spacer,
            weekLabel,
            advanceWeekBtn,
            exit);

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
    Exchange exchange = controller.getExchange();
    Button btn = new Button("Advance to week " + (exchange.getWeek() + 1));
    btn.setMaxWidth(Double.MAX_VALUE);
    String base =
        """
        -fx-text-fill: white;
        -fx-font-size: 14px;
        -fx-font-weight: bold;
        -fx-padding: 12 0 12 0;
        -fx-background-radius: 10;
        -fx-cursor: hand;
        """;
    btn.setStyle(base + "-fx-background-color: #1a1a1a;");
    btn.setOnMouseEntered(ignored -> btn.setStyle(base + "-fx-background-color: #333;"));
    btn.setOnMouseExited(ignored -> btn.setStyle(base + "-fx-background-color: #1a1a1a;"));
    btn.setOnAction(
        ignored -> {
          int week = controller.getExchange().getWeek();
          AdvanceWeekPopup popup = new AdvanceWeekPopup();
          popup.setWeeks(week, week + 1);
          popup.setOnConfirm(
              () -> {
                controller.advanceWeek();
                refreshSidebar();
                navigateToToday();
              });
          popup.show(this);
        });
    return btn;
  }

  private Button createExitBtn() {
    String base =
        """
        -fx-font-size: 13px;
        -fx-padding: 10 0 10 0;
        -fx-background-radius: 10;
        -fx-border-radius: 10;
        -fx-cursor: hand;
        -fx-max-width: Infinity;
        """;
    Button btn = new Button("Sell all and quit");
    btn.setMaxWidth(Double.MAX_VALUE);
    btn.setStyle(
        base
            + "-fx-background-color: white; -fx-text-fill: #888; -fx-border-color: #ccc; -fx-border-width: 1;");
    btn.setOnMouseEntered(
        ignored ->
            btn.setStyle(
                base
                    + "-fx-background-color: white; -fx-text-fill: #c0392b; -fx-border-color: #c0392b; -fx-border-width: 1;"));
    btn.setOnMouseExited(
        ignored ->
            btn.setStyle(
                base
                    + "-fx-background-color: white; -fx-text-fill: #888; -fx-border-color: #ccc; -fx-border-width: 1;"));
    btn.setOnAction(
        ignored -> {
          SellAllQuitPopup popup = new SellAllQuitPopup();
          popup.populateFromController(controller);
          popup.setOnConfirm(
              () -> {
                try {
                  controller.sellAll();
                  controller.saveGame();
                } catch (Exception e) {
                  e.printStackTrace();
                }
                Stage stage = (Stage) getScene().getWindow();
                stage.setOnCloseRequest(null);
                stage.getScene().setRoot(new StartMenu(stage));
              });
          popup.show(this);
        });
    return btn;
  }

  private static String fmt(BigDecimal v) {
    return String.format("%.2f", v);
  }

  private static String statusLabel(int status) {
    return switch (status) {
      case 3 -> "Speculator";
      case 2 -> "Investor";
      default -> "Novice";
    };
  }
}
