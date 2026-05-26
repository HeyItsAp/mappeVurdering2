package ntnu.gruppe21.view;

import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ntnu.gruppe21.filehandler.FilehandlerPlayer;

public class StartMenu extends StackPane {
  private final Stage stage;

  public StartMenu(Stage stage) {
    this.stage = stage;
    setStyle("-fx-background-color: #f0ede8;");
    setAlignment(Pos.CENTER);

    Label title = new Label("Stock Market");
    title.setStyle("-fx-font-size: 52px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

    Label subtitle = new Label("Build your portfolio. Beat the market.");
    subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #888;");

    HBox cards = new HBox(16, buildNewGameCard(), buildLoadGameCard());
    cards.setAlignment(Pos.CENTER);

    VBox content = new VBox(32, title, subtitle, cards);
    content.setAlignment(Pos.CENTER);
    content.setMaxWidth(700);

    getChildren().add(content);
  }

  private VBox buildNewGameCard() {
    Label heading = new Label("New Game");
    heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

    Label description = new Label("Start a fresh portfolio and build your wealth from scratch.");
    description.setWrapText(true);
    description.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

    Region spacer = new Region();
    VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

    Button startBtn = new Button("Start");
    startBtn.setMaxWidth(Double.MAX_VALUE);
    startBtn.setStyle(
        """
        -fx-font-size: 13px;
        -fx-font-weight: bold;
        -fx-background-color: #1a1a1a;
        -fx-text-fill: white;
        -fx-background-radius: 8;
        -fx-padding: 10 0 10 0;
        -fx-cursor: hand;
        """);
    startBtn.setOnAction(ignored -> launchGame());

    VBox card = new VBox(12, heading, description, spacer, startBtn);
    styleCard(card);
    return card;
  }

  private VBox buildLoadGameCard() {
    Label heading = new Label("Load Game");
    heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

    VBox saveSlots = new VBox(6);
    VBox.setVgrow(saveSlots, javafx.scene.layout.Priority.ALWAYS);

    List<String> saves;
    try {
      saves = FilehandlerPlayer.getPlayerSaveOptions();
    } catch (Exception ignored) {
      saves = List.of();
    }

    if (saves.isEmpty()) {
      Label empty = new Label("No saves found.");
      empty.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaa;");
      saveSlots.getChildren().add(empty);
    } else {
      for (String save : saves) {
        Button slotBtn = buildSaveSlotButton(save);
        saveSlots.getChildren().add(slotBtn);
      }
    }

    VBox card = new VBox(12, heading, saveSlots);
    styleCard(card);
    return card;
  }

  private Button buildSaveSlotButton(String name) {
    Button btn = new Button(name);
    btn.setMaxWidth(Double.MAX_VALUE);
    String normal =
        """
        -fx-font-size: 13px;
        -fx-background-color: #f9f9f9;
        -fx-text-fill: #1a1a1a;
        -fx-background-radius: 8;
        -fx-border-color: #e0e0e0;
        -fx-border-radius: 8;
        -fx-border-width: 1;
        -fx-padding: 10 12 10 12;
        -fx-alignment: CENTER-LEFT;
        -fx-cursor: hand;
        """;
    String hover =
        """
        -fx-font-size: 13px;
        -fx-background-color: #1a1a1a;
        -fx-text-fill: white;
        -fx-background-radius: 8;
        -fx-border-color: #1a1a1a;
        -fx-border-radius: 8;
        -fx-border-width: 1;
        -fx-padding: 10 12 10 12;
        -fx-alignment: CENTER-LEFT;
        -fx-cursor: hand;
        """;
    btn.setStyle(normal);
    btn.setOnMouseEntered(ignored -> btn.setStyle(hover));
    btn.setOnMouseExited(ignored -> btn.setStyle(normal));
    btn.setOnAction(ignored -> launchGame());
    return btn;
  }

  private void styleCard(VBox card) {
    card.setPrefWidth(300);
    card.setMinHeight(200);
    card.setStyle(
        """
        -fx-background-color: white;
        -fx-background-radius: 12;
        -fx-border-color: #c8c6c1;
        -fx-border-radius: 12;
        -fx-padding: 24;
        """);
  }

  private void launchGame() {
    stage.getScene().setRoot(new Screen());
  }
}
