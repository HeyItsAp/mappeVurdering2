package ntnu.gruppe21.view.popups;

import java.math.BigDecimal;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ntnu.gruppe21.model.gameEngine.Difficulty;

public class NewGamePopup extends Popup {

  private TextField nameField;
  private TextField moneyField;
  private ComboBox<Difficulty> difficultyBox;
  private Label errorLabel;
  private Button confirmBtn;

  @Override
  protected List<Node> buildContent() {
    Label title = new Label("New Game");
    title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

    nameField = buildTextField("Player name", false);
    moneyField = buildTextField("Starting money (e.g. 10000)", false);

    difficultyBox = new ComboBox<>();
    difficultyBox.getItems().addAll(Difficulty.values());
    difficultyBox.setValue(Difficulty.EASY);
    difficultyBox.setMaxWidth(Double.MAX_VALUE);
    difficultyBox.setStyle(
        """
        -fx-font-size: 13px;
        -fx-background-color: #f9f9f9;
        -fx-border-color: #d0d0d0;
        -fx-border-radius: 8;
        -fx-background-radius: 8;
        -fx-border-width: 1;
        -fx-cursor: hand;
        """);

    errorLabel = new Label("");
    errorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #c0392b;");
    errorLabel.setWrapText(true);

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

    confirmBtn = new Button("Start →");
    confirmBtn.setStyle(btnBase + "-fx-background-color: #1a1a1a; -fx-text-fill: white;");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox btnBox = new HBox(8, spacer, cancelBtn, confirmBtn);

    VBox form =
        new VBox(
            6,
            buildFieldLabel("Player name"),
            nameField,
            buildFieldLabel("Starting money"),
            moneyField,
            buildFieldLabel("Difficulty"),
            difficultyBox);
    form.setStyle(
        """
        -fx-background-color: #f9f9f9;
        -fx-background-radius: 8;
        -fx-border-color: #e0e0e0;
        -fx-border-radius: 8;
        -fx-padding: 12;
        """);

    return List.of(title, form, errorLabel, btnBox);
  }

  private Label buildFieldLabel(String text) {
    Label label = new Label(text);
    label.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
    return label;
  }

  private TextField buildTextField(String prompt, boolean ignored) {
    TextField field = new TextField();
    field.setPromptText(prompt);
    field.setStyle(
        """
        -fx-font-size: 13px;
        -fx-background-color: white;
        -fx-border-color: #d0d0d0;
        -fx-border-radius: 8;
        -fx-background-radius: 8;
        -fx-border-width: 1;
        -fx-padding: 7 10 7 10;
        """);
    return field;
  }

  /** Returns the entered player name, or null if validation fails. */
  public String getPlayerName() {
    return nameField.getText().trim();
  }

  /** Returns the entered starting money, or null if not a valid positive number. */
  public BigDecimal getStartingMoney() {
    try {
      BigDecimal value = new BigDecimal(moneyField.getText().trim());
      return value.compareTo(BigDecimal.ZERO) > 0 ? value : null;
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /** Returns the selected difficulty. */
  public Difficulty getDifficulty() {
    return difficultyBox.getValue();
  }

  /**
   * Returns a save slot name derived from the player name (lowercase, spaces replaced with
   * underscores).
   */
  public String getSaveSlot() {
    return nameField.getText().trim().toLowerCase().replace(" ", "_");
  }

  /**
   * Sets the action to run when the player clicks "Start →". Validates inputs first; shows an
   * inline error message if anything is missing or malformed.
   */
  public void setOnConfirm(Runnable action) {
    confirmBtn.setOnAction(
        ignored -> {
          if (getPlayerName().isEmpty()) {
            errorLabel.setText("Please enter a player name.");
            return;
          }
          if (getStartingMoney() == null) {
            errorLabel.setText("Starting money must be a positive number.");
            return;
          }
          errorLabel.setText("");
          action.run();
        });
  }
}
