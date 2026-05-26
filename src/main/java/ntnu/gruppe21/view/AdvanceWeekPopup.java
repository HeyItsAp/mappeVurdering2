package ntnu.gruppe21.view;

import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AdvanceWeekPopup extends Popup {
  private Label currentWeekLabel;
  private Label nextWeekLabel;
  private Runnable onConfirm;

  @Override
  protected List<Node> buildContent() {
    setContainerAlignment(Pos.CENTER);
    setPadding(new Insets(40));

    currentWeekLabel = new Label("Week —");
    currentWeekLabel.setStyle("-fx-font-size: 52px; -fx-font-weight: bold;");

    Label arrow = new Label("↓");
    arrow.setStyle("-fx-font-size: 24px; -fx-text-fill: #555;");

    nextWeekLabel = new Label("Week —");
    nextWeekLabel.setStyle("-fx-font-size: 52px; -fx-font-weight: bold; -fx-text-fill: #3a3aaa;");

    Label desc1 = new Label("All stocks get a new price.");
    desc1.setStyle("-fx-font-size: 13px; -fx-text-fill: #444;");
    Label desc2 = new Label("Your portfolio will be revalued.");
    desc2.setStyle("-fx-font-size: 13px; -fx-text-fill: #444;");

    Button cancelBtn = new Button("not yet");
    cancelBtn.setPrefWidth(120);
    cancelBtn.setPrefHeight(44);
    cancelBtn.setOnAction(ignored -> close());
    cancelBtn.setStyle(
        """
        -fx-background-color: transparent;
        -fx-border-color: #333;
        -fx-border-width: 1.5;
        -fx-border-style: dashed;
        -fx-border-radius: 6;
        -fx-font-size: 13px;
        -fx-cursor: hand;
        """);

    Button advanceBtn = new Button("advance →");
    advanceBtn.setPrefWidth(150);
    advanceBtn.setPrefHeight(44);
    advanceBtn.setStyle(
        """
        -fx-background-color: #1a1a1a;
        -fx-text-fill: white;
        -fx-background-radius: 6;
        -fx-font-size: 13px;
        -fx-cursor: hand;
        """);
    advanceBtn.setOnAction(
        ignored -> {
          if (onConfirm != null) onConfirm.run();
          close();
        });

    HBox buttons = new HBox(12, cancelBtn, advanceBtn);
    buttons.setAlignment(Pos.CENTER);
    VBox.setMargin(buttons, new Insets(10, 0, 0, 0));

    return List.of(currentWeekLabel, arrow, nextWeekLabel, desc1, desc2, buttons);
  }

  public void setWeeks(int current, int next) {
    currentWeekLabel.setText("Week " + current);
    nextWeekLabel.setText("Week " + next);
  }

  public void setOnConfirm(Runnable action) {
    this.onConfirm = action;
  }
}
