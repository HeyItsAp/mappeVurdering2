package ntnu.gruppe21.view.menus;

import java.math.BigDecimal;
import java.util.List;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import ntnu.gruppe21.model.Player;
import ntnu.gruppe21.model.gameEngine.challenges.Challenge;
import ntnu.gruppe21.view.Screen;

public class ProgressMenu extends VBox {
  private final Screen screen;

  public ProgressMenu(Screen screen) {
    super(20);
    this.screen = screen;
    setStyle("-fx-padding: 20 20 30 20");

    Label title = new Label("Progress");
    title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

    Line line = new Line();
    line.setStartX(0);
    line.setStartY(0);
    line.setEndX(1000);
    line.setEndY(0);
    line.setStroke(Color.BLACK);
    line.setStrokeWidth(0.5);

    getChildren().addAll(title, line, buildStatusCard(), buildChallengesSection());
  }

  private VBox buildStatusCard() {
    Player player = screen.getController().getPlayer();
    int total = player.getChallengeManager().getTotalCompletions();
    int status = player.getStatus();

    String statusName =
        switch (status) {
          case 3 -> "Speculator";
          case 2 -> "Investor";
          default -> "Novice";
        };
    String color =
        switch (status) {
          case 3 -> "#e67e22";
          case 2 -> "#2980b9";
          default -> "#27ae60";
        };
    String nextLabel =
        switch (status) {
          case 3 -> "Max level reached";
          case 2 -> (8 - total) + " more challenge(s) to reach Speculator";
          default -> (3 - total) + " more challenge(s) to reach Investor";
        };

    Label heading = new Label("STATUS");
    heading.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa; -fx-font-weight: bold;");

    Label statusLabel = new Label(statusName);
    statusLabel.setStyle(
        "-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

    Label completedLabel = new Label("Total challenges completed: " + total);
    completedLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

    Label nextLevelLabel = new Label(nextLabel);
    nextLevelLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

    VBox card = new VBox(8, heading, statusLabel, completedLabel, nextLevelLabel);
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

  private VBox buildChallengesSection() {
    Label heading = new Label("ACTIVE CHALLENGES");
    heading.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa; -fx-font-weight: bold;");

    List<Challenge> challenges =
        screen.getController().getPlayer().getChallengeManager().getActiveChallenges();

    VBox cards = new VBox(10);
    if (challenges.isEmpty()) {
      Label none = new Label("No active challenges.");
      none.setStyle("-fx-font-size: 13px; -fx-text-fill: #888;");
      cards.getChildren().add(none);
    } else {
      for (Challenge c : challenges) {
        cards.getChildren().add(buildChallengeCard(c));
      }
    }

    return new VBox(10, heading, cards);
  }

  private VBox buildChallengeCard(Challenge challenge) {
    Label descLabel = new Label(challenge.getDescription());
    descLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

    Label rewardLabel =
        new Label(
            "Reward: "
                + formatReward(challenge.calculateReward(screen.getController().getPlayer())));
    rewardLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60;");

    Label timesLabel = new Label("Completed " + challenge.getTimesCompleted() + " time(s)");
    timesLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa;");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox footer = new HBox(rewardLabel, spacer, timesLabel);

    VBox card = new VBox(8, descLabel, footer);
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

  private static String formatReward(BigDecimal reward) {
    return String.format("%.2f", reward);
  }
}
