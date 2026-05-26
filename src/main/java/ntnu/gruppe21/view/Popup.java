package ntnu.gruppe21.view;

import java.util.List;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public abstract class Popup extends StackPane {
  private VBox container;

  public Popup() {
    setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");
    setAlignment(Pos.CENTER);

    container = new VBox(16);
    container.setMaxWidth(420);
    container.setMaxHeight(Region.USE_PREF_SIZE);
    container.setStyle(
        """
        -fx-background-color: white;
        -fx-background-radius: 15;
        -fx-border-color: #000000;
        -fx-border-radius: 12;
        -fx-border-width: 2;
        -fx-padding: 24;
        """);
    container.getChildren().addAll(buildContent());
    getChildren().add(container);

    container.setOnMouseClicked(Event::consume);
    setOnMouseClicked(e -> close());
  }

  protected abstract List<Node> buildContent();

  protected void setContainerAlignment(Pos pos) {
    container.setAlignment(pos);
  }

  public void show(Pane parent) {
    prefWidthProperty().bind(parent.widthProperty());
    prefHeightProperty().bind(parent.heightProperty());
    parent.getChildren().add(this);
  }

  public void close() {
    prefWidthProperty().unbind();
    prefHeightProperty().unbind();
    if (getParent() instanceof Pane parent) {
      parent.getChildren().remove(this);
    }
  }
}
